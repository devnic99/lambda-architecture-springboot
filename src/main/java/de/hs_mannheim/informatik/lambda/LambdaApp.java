package de.hs_mannheim.informatik.lambda;

import org.springframework.beans.factory.annotation.Autowired;

// import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import repository.ItemRepository.ItemRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import de.hs_mannheim.informatik.lambda.model.WordCount;
import de.hs_mannheim.informatik.lambda.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongoRepositories
public class LambdaApp {
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class LambdaApp implements CommandLineRunner {

	@Autowired
	ItemRepository itemRepo;

	@Autowired
	// ItemRepository documentItemRepo;
	public static void main(String[] args) {
		SpringApplication.run(LambdaApp.class, args);

		System.out.println("Ausführungsort: " + new File(".").getAbsolutePath());
	}