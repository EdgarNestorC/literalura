package com.alura.literalura;

import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.PrincipalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {
	private final LibroRepository libroRepository;
	private final AutorRepository autorRepository;

	public LiteraluraApplication(LibroRepository libroRepository, AutorRepository autorRepository) {
		this.libroRepository = libroRepository;
		this.autorRepository = autorRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		PrincipalService principalService = new PrincipalService(libroRepository, autorRepository);
		principalService.mostrarMenu();
	}
}
