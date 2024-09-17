package com.documentService.document;

import org.springframework.boot.SpringApplication;

public class TestDocumentApplication {

	public static void main(String[] args) {
		SpringApplication.from(DocumentApplication::main).run(args);
	}

}
