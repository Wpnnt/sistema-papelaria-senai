package com.projetofinal.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projetofinal.backend.storage.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/photos")
@Tag(name = "Photos", description = "Photo upload and storage endpoints")
public class PhotoController {

	@Autowired
	private FileStorageService storageService;

	@Operation(summary = "Upload photo", description = "Uploads and saves a photo file to storage")
	@PostMapping
	public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
		storageService.savePhoto(file);
		return ResponseEntity.ok().build();
	}
}
