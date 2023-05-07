package de.hs_mannheim.informatik.lambda;

import org.springframework.beans.factory.annotation.Autowired;

// import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import repository.ItemRepository.ItemRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.File;

import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongoRepositories
public class LambdaApp {

	@Autowired
	// ItemRepository documentItemRepo;
	public static void main(String[] args) {
		SpringApplication.run(LambdaApp.class, args);

		System.out.println("Ausführungsort: " + new File(".").getAbsolutePath());
	}

}