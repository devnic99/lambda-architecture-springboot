package de.hs_mannheim.informatik.lambda;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import de.hs_mannheim.informatik.lambda.model.WordCount;
import de.hs_mannheim.informatik.lambda.model.DocumentFrequency;
import de.hs_mannheim.informatik.lambda.SparkSessionSingleton;
import de.hs_mannheim.informatik.lambda.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

@SpringBootApplication
@EnableMongoRepositories
public class LambdaApp implements CommandLineRunner {
/*@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {ItemRepository.class})
public class LambdaApp {*/


	@Autowired
	ItemRepository itemRepo;

	public static void main(String[] args) {
		SparkSession spark = SparkSessionSingleton.getInstance();

		SpringApplication.run(LambdaApp.class, args);

		System.out.println("Ausführungsort: " + new File(".").getAbsolutePath());

		spark.stop();
	}

	@Override
	public void run(String... args) {
		DocumentFrequency Beispielwort = new DocumentFrequency("Beispiel");
	}

	
}
