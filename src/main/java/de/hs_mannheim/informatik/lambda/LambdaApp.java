package de.hs_mannheim.informatik.lambda;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import de.hs_mannheim.informatik.lambda.model.DocumentFrequency;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import scala.Tuple2;


@SpringBootApplication
@EnableMongoRepositories
public class LambdaApp implements CommandLineRunner {
/*@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = {ItemRepository.class})
public class LambdaApp {*/




	public static void main(String[] args) {
		SparkSession spark = SparkSessionSingleton.getInstance();

		SpringApplication.run(LambdaApp.class, args);

		System.out.println("Ausführungsort: " + new File(".").getAbsolutePath());

		spark.stop();

		//SparkConf conf = new SparkConf().setAppName("wc").setMaster("local[4]");
		//JavaSparkContext sc = new JavaSparkContext(conf);

//		SparkSession spark = SparkSession
//				.builder()
//				.master("local")
//				.appName("wc")
//				.config("spark.mongodb.input.uri", "mongodb://127.0.0.1/servinglayer.df")
//				.config("spark.mongodb.output.uri", "mongodb://127.0.0.1/servinglayer.df")
//				.getOrCreate();



/*		JavaRDD<String> tokens = sc.textFile("spark-it/src/main/resources/Faust.txt").flatMap(
				s -> Arrays.asList(s.split("\\W+")).iterator());

		JavaPairRDD<String, Integer> counts = tokens.mapToPair(
				token -> new Tuple2<>(token, 1)).reduceByKey((x, y) -> x+y);

		List<Tuple2<String, Integer>> results = counts.collect();
		results.forEach(System.out::println);*/

	}

	@Override
	public void run(String... args) {
		// Read Dokumentenfrequenz from DB
		DocumentFrequency Beispielwort = new DocumentFrequency("Beispiel");
		//getDFByWord(Beispielwort);

		/*System.out.println("-------------CREATE GROCERY ITEMS-------------------------------\n");

		createGroceryItems();

		System.out.println("\n----------------SHOW ALL GROCERY ITEMS---------------------------\n");

		showAllGroceryItems();

		System.out.println("\n--------------GET ITEM BY NAME-----------------------------------\n");

		getGroceryItemByName("Whole Wheat Biscuit");

		System.out.println("\n-----------GET ITEMS BY CATEGORY---------------------------------\n");

		getItemsByCategory("millets");

		System.out.println("\n-----------UPDATE CATEGORY NAME OF SNACKS CATEGORY----------------\n");

		updateCategoryName("snacks");

		System.out.println("\n----------DELETE A GROCERY ITEM----------------------------------\n");

		deleteGroceryItem("Kodo Millet");

		System.out.println("\n------------FINAL COUNT OF GROCERY ITEMS-------------------------\n");

		findCountOfGroceryItems();

		System.out.println("\n-------------------THANK YOU---------------------------");*/

	}




/*	//CREATE
	public void createGroceryItems() {
		System.out.println("Data creation started...");
		itemRepo.save(new Word("Whole Wheat Biscuit", "Whole Wheat Biscuit", 5, "snacks"));
		itemRepo.save(new Word("Kodo Millet", "XYZ Kodo Millet healthy", 2, "millets"));
		itemRepo.save(new Word("Dried Red Chilli", "Dried Whole Red Chilli", 2, "spices"));
		itemRepo.save(new Word("Pearl Millet", "Healthy Pearl Millet", 1, "millets"));
		itemRepo.save(new Word("Cheese Crackers", "Bonny Cheese Crackers Plain", 6, "snacks"));
		System.out.println("Data creation complete...");
	}*/

/*	// READ
	// 1. Show all the data
	public void showAllGroceryItems() {
		itemRepo.findAll().forEach(item -> System.out.println(getItemDetails(item)));
	}

	// 2. Get item by name
	public void getGroceryItemByName(String name) {
		System.out.println("Getting item by name: " + name);
		Word item = itemRepo.findItemByName(name);
		System.out.println(getItemDetails(item));
	}*/

/*	// 3. Get name and quantity of a all items of a particular category
	public void getItemsByCategory(String category) {
		System.out.println("Getting items for the category " + category);
		List<Word> list = itemRepo.findAll(category);

		list.forEach(item -> System.out.println("Name: " + item.getName() + ", Quantity: " + item.getQuantity()));
	}*/

/*	// 4. Get count of documents in the collection
	public void findCountOfGroceryItems() {
		long count = itemRepo.count();
		System.out.println("Number of documents in the collection: " + count);
	}*/



	/*public void updateCategoryName(String category) {

		// Change to this new value
		String newCategory = "munchies";

		// Find all the items with the category snacks
		List<Word> list = itemRepo.findAll(category);

		list.forEach(item -> {
			// Update the category in each document
			item.setCategory(newCategory);
		});

		// Save all the items in database
		List<Word> itemsUpdated = itemRepo.saveAll(list);

		if(itemsUpdated != null)
			System.out.println("Successfully updated " + itemsUpdated.size() + " items.");
	}

	// DELETE
	public void deleteGroceryItem(String id) {
		itemRepo.deleteById(id);
		System.out.println("Item with id " + id + " deleted...");
	}*/
}
