package com.projetofinal.backend.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final String rootLocation = "c:/tmp/stationery-storage/";
	private final String photoDirectory = "photos";

	public void savePhoto(MultipartFile file) {
		this.save(this.photoDirectory, file);
	}

	private void save(String directory, MultipartFile file) {
		Path directoryPath = Paths.get(this.rootLocation, directory);
		Path filePath = directoryPath.resolve(file.getOriginalFilename());

		try {
			Files.createDirectories(directoryPath);
			file.transferTo(filePath.toFile());
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
		}
	}
}
