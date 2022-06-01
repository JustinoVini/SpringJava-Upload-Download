package com.examplo.uploaddownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.examplo.uploaddownload.property.FilesStorageProperties;

@SpringBootApplication

@EnableConfigurationProperties({
		FilesStorageProperties.class
})
public class UploaddownloadApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploaddownloadApplication.class, args);
	}

}
