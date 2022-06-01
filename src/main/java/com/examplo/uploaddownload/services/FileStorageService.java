package com.examplo.uploaddownload.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.examplo.uploaddownload.exception.FileStorageException;
import com.examplo.uploaddownload.exception.MyFileNotFoundException;
import com.examplo.uploaddownload.property.FilesStorageProperties;

@Service
public class FileStorageService {

	// atributo para assumir o valor de path.
	private final Path fileStorageLocation;

	// Contrutor - injeção de dependencias
	@Autowired
	public FileStorageService(FilesStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Não foi possivel criar o diretorio no local indicado para o upload", ex);
		}
	}

	// Método que acessa o arquivo - a partir de sua identificação
	public String storeFile(MultipartFile file) {
		// Tratamento de normalização do path para acessar o arquivo.
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		// Bloco try, para verificar se o arquivo não contem caracteres invalidos.
		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("O arquivo contém uma sequencia invalida para o caminho" + fileName);
			}
			// "Copiar" o arquivo para o local indicado (caso exista um arquivo com o mesmo
			// nome, será substituido)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new FileStorageException("Não foi possível armazenar o arquivo" + fileName + ".Tente novamente", ex);
		}
		return fileName;
	}

	// Tentativa de recuperar o arquivo "uppado"
	public Resource loadFileAsResource(String fileName) {
		// bloco try
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());

			// Verificar se o recurso existe
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("Arquivo não encontrado. " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("Arquivo não encontrado. " + fileName, ex);
		}
	}
}
