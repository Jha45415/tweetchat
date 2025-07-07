package oth.ics.wtp.tweetchatbackend.Service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {

        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }



    public String storeFile(MultipartFile file) {

        String originalFileName = file.getOriginalFilename();


        if (originalFileName == null || originalFileName.contains("..")) {
            throw new RuntimeException("Sorry! Filename contains an invalid path sequence: " + originalFileName);
        }


        String sanitizedFileName = StringUtils.cleanPath(originalFileName);
        String fileExtension = "";
        try {

            fileExtension = sanitizedFileName.substring(sanitizedFileName.lastIndexOf("."));
        } catch (Exception e) {
            fileExtension = "";
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + uniqueFileName + ". Please try again!", ex);
        }
    }
}