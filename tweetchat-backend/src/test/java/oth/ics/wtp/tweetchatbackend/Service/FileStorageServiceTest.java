package oth.ics.wtp.tweetchatbackend.Service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private final Path root = Paths.get("uploads");

    @BeforeEach
    void setUp() {

        fileStorageService = new FileStorageService();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @AfterEach
    void tearDown() throws IOException {

        if (Files.exists(root)) {
            try (Stream<Path> walk = Files.walk(root)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {

                            }
                        });
            }
        }
    }

    @Test
    void storeFile_withValidFile_shouldReturnFileName() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );


        String fileName = fileStorageService.storeFile(file);


        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".txt"));
        assertTrue(Files.exists(root.resolve(fileName)));
    }

    @Test
    void storeFile_withInvalidPathInFileName_shouldThrowException() {

        MockMultipartFile invalidFile = new MockMultipartFile("file", "../../invalid.txt", "text/plain", "test".getBytes());


        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(invalidFile);
        });
        assertTrue(exception.getMessage().contains("invalid path sequence"));
    }
    @Test
    void storeFile_withValidFile_shouldReturnFileNameAndCreateFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        String fileName = fileStorageService.storeFile(file);

        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".jpg"));
        assertTrue(Files.exists(Paths.get("uploads").resolve(fileName)));
    }
    @Test
    void storeFile_withInvalidFileName_shouldThrowException() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "../../invalid.txt", "text/plain", "test".getBytes());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(invalidFile);
        });

        assertTrue(exception.getMessage().contains("invalid path sequence"));
    }
}

