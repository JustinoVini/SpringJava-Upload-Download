package com.examplo.uploaddownload.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.examplo.uploaddownload.model.UploadFileResponse;

// A linha abaixo, transforma a classe como um controle.
@RestController
public class FileController {

	@PostMapping("/uploadFile")
	// O método response traz uma "Resposta ao servidor"
	public UploadFileResponse uploadFile(@RequestParam("file")MultipartFile file) {
		
		return null;
	}
	
	
	
}
