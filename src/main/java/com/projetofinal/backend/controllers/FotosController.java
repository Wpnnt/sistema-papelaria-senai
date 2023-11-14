package com.projetofinal.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projetofinal.backend.storage.Disco;

@RestController
@RequestMapping("/fotos")
public class FotosController {
	
@Autowired	
private Disco disco;

@PostMapping
public void upload(@RequestParam MultipartFile foto) {
	disco.salvarFoto (foto);
}
}
