package com.suleyman.jobportal.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadUtil {

    public static Resource getFileAsResourse(String downloadDir, String fileName) throws IOException {

        Path downloadPath = Paths.get(downloadDir);
        Path foundFile = downloadPath.resolve(fileName);

        if (Files.exists(foundFile) && Files.isReadable(foundFile)) {
            Resource resource = new UrlResource(foundFile.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("Could not read file: " + fileName);
            }
        }

        throw new IOException("File not found: " + fileName);
    }
}