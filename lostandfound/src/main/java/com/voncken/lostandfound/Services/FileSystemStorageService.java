package com.voncken.lostandfound.Services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

// oddly enough guide says this package needs to be imported
// but I don't see why yet
// import org.springframework.util.StringUtils;

import org.springframework.web.multipart.MultipartFile;

import com.voncken.lostandfound.Contracts.IStorageService;
import com.voncken.lostandfound.Repositories.States.LostItemState;
import com.voncken.lostandfound.Services.Exceptions.StorageException;
import com.voncken.lostandfound.Services.Exceptions.StorageFileNotFoundException;

@Service
public class FileSystemStorageService implements IStorageService {

    private final Path rootLocation;
    private final LostItemService lostItemService;

    @Autowired
    public FileSystemStorageService(LostItemService lostItemService, StorageProperties properties) {
        if (properties.getFolderPath().trim().length() == 0) {
            throw new StorageException("Location of file upload cannot be empty.");
        }

        this.rootLocation = Paths.get(properties.getFolderPath());
        this.lostItemService = lostItemService;
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Unable to store because it's content is empty");
            }
            
            var fileContentType = file.getContentType();

            if (fileContentType != null && !fileContentType.contains("pdf")) {
                throw new StorageException("We only support PDF files at the moment");
            }

            Path desPath = this.rootLocation.resolve(Paths.get(file.getOriginalFilename())).normalize()
                    .toAbsolutePath();

            if (!desPath.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // security check
                throw new StorageException("Can't store the file outside of it's current folder");
            }

            try (InputStream inputStream = file.getInputStream()) {

                // from previous expierence, you can check your mime types here
                // this way you can make sure you only support certain file types
                // and also check file integrity, but it's a bit out of scope here
                // imagine FileIntegrity is a class which checks the integrity and determines
                // it's mimeType
                // var fileIntegrity = new FileIntegrity(file);
                // if (!(fileIntegrity.IsValid == true && fileIntegrity.mimeType ==
                // MimeType.Pdf))
                // {
                // throw new FileIntegrityException("The file you have uploaded is not of an
                // supported type or invalid");
                // };

                Files.copy(inputStream, desPath, StandardCopyOption.REPLACE_EXISTING);
                addLostItems(desPath);
            }
        } catch (IOException ex) {
            throw new StorageException("Unable to store the uploaded file");
        }
    }

    // based on: https://www.tutorialspoint.com/pdfbox/pdfbox_quick_guide.htm
    public void addLostItems(Path desPath) {
        try {
            File file = new File(desPath.toString());

            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            final Pattern itemPattern = Pattern.compile("ItemName:\\s[A-Za-z]+", Pattern.CASE_INSENSITIVE);
            final Pattern quantityPattern = Pattern.compile("Quantity:\\s\\d", Pattern.CASE_INSENSITIVE);
            final Pattern lostFoundPlacePattern = Pattern.compile("Place:\\s[A-Za-z]+", Pattern.CASE_INSENSITIVE);

            final Matcher itemMatcher = itemPattern.matcher(text);
            final Matcher quantityMatcher = quantityPattern.matcher(text);
            final Matcher lostFoundPlaceMatcher = lostFoundPlacePattern.matcher(text);

            LostItemState lostItemState = new LostItemState();

            // this is a bit of a green thread situation, but it's a start to a complex mapping system, consider using a trained OCR model on a predefined set of fields
            while (itemMatcher.find()) {
                quantityMatcher.find();
                lostFoundPlaceMatcher.find();
                System.out.println("Full match: " + itemMatcher.group(0));
                lostItemState.Guid = java.util.UUID.randomUUID();
                lostItemState.Name = itemMatcher.group(0).split(":")[1].trim();
                lostItemState.Quantity = Integer.parseInt(quantityMatcher.group(0).split(":")[1].trim());
                lostItemState.LostAndFoundPlace = lostFoundPlaceMatcher.group(0).split(":")[1].trim();
                
                lostItemService.Add(lostItemState);
                
                for (int i = 1; i <= itemMatcher.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + itemMatcher.group(i));
                }
            }
            System.out.println(text);
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
    }



	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

    @Override
    public Path load(String fileName) {
        return rootLocation.resolve(fileName);
    }

    @Override
    public Resource loadAsResource(String fileName) {
        try {
            Path file = load(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Unable to read the file: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new StorageFileNotFoundException("Unable to read the file: " + fileName, ex);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively((rootLocation.toFile()));
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException ex) {
            throw new StorageException("Unable to find the storage folder");
        }
    }
}
