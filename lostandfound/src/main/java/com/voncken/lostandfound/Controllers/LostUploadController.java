package com.voncken.lostandfound.Controllers;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.voncken.lostandfound.Contracts.ILostItemService;
import com.voncken.lostandfound.Contracts.IStorageService;
import com.voncken.lostandfound.Contracts.IUserClaimService;
import com.voncken.lostandfound.Repositories.States.LostItemState;
import com.voncken.lostandfound.Repositories.States.UserClaimState;
import com.voncken.lostandfound.Services.Exceptions.StorageFileNotFoundException;

// based on: https://spring.io/guides/gs/uploading-files
@Controller
public class LostUploadController {
    private final IStorageService storageService;
    private final ILostItemService lostItemService;
    private final IUserClaimService userClaimService;

    @Autowired
    public LostUploadController(IStorageService storageService, ILostItemService lostItemService,
            IUserClaimService userClaimService) {
        this.storageService = storageService;
        this.lostItemService = lostItemService;
        this.userClaimService = userClaimService;
    }

    @GetMapping("/login")
    public String login(Model model) throws IOException {
        return "login";
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(
                        LostUploadController.class,
                        "serveFile",
                        path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        model.addAttribute("lostItems", lostItemService.GetAll());
        model.addAttribute("claimedItems", userClaimService.GetAll());

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" +
                        file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/claimedItems")
    @ResponseBody
    public ResponseEntity<Iterable<UserClaimState>> claimedItems() {
        var claimedItems = userClaimService.GetAll();

        return ResponseEntity.ok(claimedItems);
    }

    @GetMapping("/lostItems")
    @ResponseBody
    public ResponseEntity<Iterable<LostItemState>> lostItems() {
        var lostItems = lostItemService.GetAll();

        return ResponseEntity.ok(lostItems);
    }

    @PostMapping("/upload") 
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "Succesfully uploaded your request. [" + file.getOriginalFilename() + "] !");

        return "redirect:/";
    }

    @PostMapping("/claimItem")
    public String claimItem(@ModelAttribute UserClaimState claimState, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        
        var guid =  UUID.randomUUID();
        var quantity = claimState.ClaimedQuantity;
        var lostItemGuid = claimState.LostItemGuid;
        var userId = userDetails.getUsername();

        UserClaimState userClaimState = new UserClaimState(guid, userId, lostItemGuid, quantity);

        userClaimService.Add(userClaimState);

        redirectAttributes.addFlashAttribute("message",
                "A claim for lost item with identifier [" + claimState.LostItemGuid + "] for a quantity of " + claimState.ClaimedQuantity + " was succesfull!");
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user")
    public ResponseEntity<String> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("User Details: \n\n" + userDetails.toString());
    }
}
