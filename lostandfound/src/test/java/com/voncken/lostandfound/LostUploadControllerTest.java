package com.voncken.lostandfound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.voncken.lostandfound.Contracts.ILostItemService;
import com.voncken.lostandfound.Contracts.IStorageService;
import com.voncken.lostandfound.Contracts.IUserClaimService;
import com.voncken.lostandfound.Controllers.LostUploadController;
import com.voncken.lostandfound.Repositories.States.LostItemState;
import com.voncken.lostandfound.Repositories.States.UserClaimState;

//based on https://spring.io/guides/gs/testing-web
// and https://www.baeldung.com/spring-boot-testing

@SpringBootTest
public class LostUploadControllerTest {

    @Mock
    private IStorageService storageService;

    @Mock
    private ILostItemService lostItemService;

    @Mock
    private IUserClaimService userClaimService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Model model;

    private LostUploadController lostUploadController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        lostUploadController = new LostUploadController(storageService, lostItemService, userClaimService);
    }


// example from: https://www.baeldung.com/spring-boot-testing
//     Mockito.when(employeeRepository.findByName(alex.getName()))
//       .thenReturn(alex);
//

    @Test
    public void testGetUser() {
        String userDetailsString = "username: testUser, authorities: [ROLE_USER]";
        when(userDetails.toString()).thenReturn(userDetailsString);
        
        ResponseEntity<String> response = lostUploadController.getUser(userDetails);
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals("User Details: \n\n" + userDetailsString, response.getBody());
    }

    @Test
    public void testClaimedItems() {
        Iterable<UserClaimState> expectedClaimedItems = List.of(
            new UserClaimState(UUID.randomUUID(), "user1", UUID.randomUUID(), 2),
            new UserClaimState(UUID.randomUUID(), "user2", UUID.randomUUID(), 1)
        );
        when(userClaimService.GetAll()).thenReturn(expectedClaimedItems);

        ResponseEntity<Iterable<UserClaimState>> response = lostUploadController.claimedItems();
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedClaimedItems, response.getBody());
        verify(userClaimService).GetAll();
    }

    @Test
    public void testLostItems() {
        Iterable<LostItemState> expectedLostItems = List.of(
            new LostItemState(),
            new LostItemState()
        );
        when(lostItemService.GetAll()).thenReturn(expectedLostItems);
        
        ResponseEntity<Iterable<LostItemState>> response = lostUploadController.lostItems();
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedLostItems, response.getBody());
        verify(lostItemService).GetAll();
    }

    @Test
    public void testHandleFileUpload() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-file.jpg");
        
        // Act
        String result = lostUploadController.handleFileUpload(mockFile, redirectAttributes);
        
        // Assert
        verify(storageService).store(mockFile);
        verify(redirectAttributes).addFlashAttribute(
            eq("message"),
            eq("Succesfully uploaded your request. [test-file.jpg] !")
        );
        assertEquals("redirect:/", result);
    }

    // using https://www.baeldung.com/mockito-argumentcaptor
    @Test
    public void testClaimItem() {
        UUID lostItemGuid = UUID.randomUUID();
        int claimedQuantity = 5;
        String userId = "testUser";
        
        UserClaimState claimState = new UserClaimState();
        claimState.LostItemGuid = lostItemGuid;
        claimState.ClaimedQuantity = claimedQuantity;
        
        when(userDetails.getUsername()).thenReturn(userId);
        
        String result = lostUploadController.claimItem(claimState, redirectAttributes, userDetails, model);
        
        ArgumentCaptor<UserClaimState> captor = ArgumentCaptor.forClass(UserClaimState.class);
        verify(userClaimService).Add(captor.capture());
        
        UserClaimState capturedState = captor.getValue();
        assertEquals(lostItemGuid, capturedState.LostItemGuid);
        assertEquals(claimedQuantity, capturedState.ClaimedQuantity);
        assertEquals(userId, capturedState.UserId);
        
        verify(redirectAttributes).addFlashAttribute(
            eq("message"),
            eq("A claim for lost item with identifier [" + lostItemGuid + "] for a quantity of " + claimedQuantity + " was succesfull!")
        );
        
        assertEquals("redirect:/", result);
    }
}