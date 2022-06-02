package com.examplo.uploaddownload.controllers;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.examplo.uploaddownload.model.UploadFileResponse;
import com.examplo.uploaddownload.services.FileStorageService;

// A linha abaixo, transforma a classe como um controle.
@RestController
public class FileController {
	// Criar uma propriedade para servir de log de manipulação a partir do controller
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	// Injeção de dependencia 
	@Autowired
	private FileStorageService fileStorageService;
	
	@PostMapping("/uploadFile")
	// O método response traz uma "Resposta ao servidor"
	public UploadFileResponse uploadFile(@RequestParam("file")MultipartFile file) {
		String fileName = fileStorageService.storeFile(file);
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/downloadFile/")
				.path(fileName)
				.toUriString();
		
		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}	
	
	// Método para upar 2 ou mais arquivos.
	@PostMapping("/uploadMultipleFiles")
	public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files){
		
		return Arrays.asList(files)
				.stream()
				.map(file -> uploadFile(file))
				.collect(Collectors.toList());
	}
	
	// Criar a estrutura lógica que possibilitará  o download de arquivos
	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request){
		// Criar uma propriedade para acessar o método que executa os downloads
		Resource resource = fileStorageService.loadFileAsResource(fileName);
		// Tentativa de determinar um conteudo do arquivo.
		String contentType = null;
		
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Não foi possivel determinar o tipo do arquivo.");
		}
		
		// verificar se o tipo de conteudo não pode ser verificada porque a variavel não conseguiu acessa-lá
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
		
	}
}
