package de.hs_mannheim.informatik.lambda;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import de.hs_mannheim.informatik.lambda.model.WordCount;
import de.hs_mannheim.informatik.lambda.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

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
		SpringApplication.run(LambdaApp.class, args);

		System.out.println("Ausführungsort: " + new File(".").getAbsolutePath());
	}

	@Override
	public void run(String... args) {

		System.out.println("-------------CREATE GROCERY ITEMS-------------------------------\n");

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

		System.out.println("\n-------------------THANK YOU---------------------------");

	}

	//CREATE
	public void createGroceryItems() {
		System.out.println("Data creation started...");
		itemRepo.save(new WordCount("Whole Wheat Biscuit", "Whole Wheat Biscuit", 5, "snacks"));
		itemRepo.save(new WordCount("Kodo Millet", "XYZ Kodo Millet healthy", 2, "millets"));
		itemRepo.save(new WordCount("Dried Red Chilli", "Dried Whole Red Chilli", 2, "spices"));
		itemRepo.save(new WordCount("Pearl Millet", "Healthy Pearl Millet", 1, "millets"));
		itemRepo.save(new WordCount("Cheese Crackers", "Bonny Cheese Crackers Plain", 6, "snacks"));
		System.out.println("Data creation complete...");
	}

	// READ
	// 1. Show all the data
	public void showAllGroceryItems() {

		itemRepo.findAll().forEach(item -> System.out.println(getItemDetails(item)));
	}

	// 2. Get item by name
	public void getGroceryItemByName(String name) {
		System.out.println("Getting item by name: " + name);
		WordCount item = itemRepo.findItemByName(name);
		System.out.println(getItemDetails(item));
	}

	// 3. Get name and quantity of a all items of a particular category
	public void getItemsByCategory(String category) {
		System.out.println("Getting items for the category " + category);
		List<WordCount> list = itemRepo.findAll(category);

		list.forEach(item -> System.out.println("Name: " + item.getName() + ", Quantity: " + item.getQuantity()));
	}

	// 4. Get count of documents in the collection
	public void findCountOfGroceryItems() {
		long count = itemRepo.count();
		System.out.println("Number of documents in the collection: " + count);
	}

	// Print details in readable form
	public String getItemDetails(WordCount item) {

		System.out.println(
				"Item Name: " + item.getName() +
						", \nQuantity: " + item.getQuantity() +
						", \nItem Category: " + item.getCategory()
		);

		return "";
	}

	public void updateCategoryName(String category) {

		// Change to this new value
		String newCategory = "munchies";

		// Find all the items with the category snacks
		List<WordCount> list = itemRepo.findAll(category);

		list.forEach(item -> {
			// Update the category in each document
			item.setCategory(newCategory);
		});

		// Save all the items in database
		List<WordCount> itemsUpdated = itemRepo.saveAll(list);

		if(itemsUpdated != null)
			System.out.println("Successfully updated " + itemsUpdated.size() + " items.");
	}

	// DELETE
	public void deleteGroceryItem(String id) {
		itemRepo.deleteById(id);
		System.out.println("Item with id " + id + " deleted...");
	}
}
