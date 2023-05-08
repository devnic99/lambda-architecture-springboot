package de.hs_mannheim.informatik.lambda.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import de.hs_mannheim.informatik.lambda.SparkSessionSingleton;
import de.hs_mannheim.informatik.lambda.model.DocumentFrequency;
import de.hs_mannheim.informatik.lambda.repository.ItemRepository;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import de.hs_mannheim.informatik.lambda.model.WordCount;

import javax.annotation.Resource;

import scala.Tuple2;

import org.apache.spark.SparkConf;

@Controller
public class LambdaController {

/*	@Autowired
	private ItemRepository itemRepository;*/

	public final static String CLOUD_PATH = "tagclouds/";

	@Autowired
	ItemRepository itemRepo;
	


	@GetMapping("/upload")
	public String forward(Model model) {
		model.addAttribute("files", listTagClouds());

		return "upload";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {

		try {
			model.addAttribute("message", "Datei erfolgreich hochgeladen: " + file.getOriginalFilename());
			createTagCloud(file.getOriginalFilename(), new String(file.getBytes()));
			model.addAttribute("files", listTagClouds());
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("message", "Da gab es einen Fehler: " + e.getMessage());
		}

		return "upload";
	}

	@GetMapping("/uploadWord")
	public String handleWordUploadGet(Model model) {
		return "upload";
	}

	@PostMapping("/uploadWord")
    public String handleWordUpload(@RequestParam("word") String word, Model model) {
        model.addAttribute("word", word);
        return "upload";
    }



	@GetMapping("/dokumentenfrequenz")
	public String handleDFCalculation(String searchWord, Model model) {

		//Batch Job auslösen:

		File[] files = new File(CLOUD_PATH).listFiles();
		SparkSession spark = SparkSessionSingleton.getInstance();

		// Create RDD from text files
		JavaRDD<String> lines = spark.read().textFile(files.toString()).javaRDD();

		// Split each line into words and create a flatMap of words
		JavaRDD<String> words = lines.flatMap(
				line -> Arrays.asList(line.split("\\W+")).iterator());

		// Map each word to a tuple with the word as the key and 1 as the value
		JavaPairRDD<String, Integer> pairs = words.mapToPair(
				word -> new Tuple2<>(word, 1));

		// Reduce by key to get the count of each word
		JavaPairRDD<String, Integer> counts = pairs.reduceByKey((x, y) -> x+y);

		// convert the JavaPairRDD to a DataFrame
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		JavaRDD<Document> documents = counts.map(tuple -> {
			Document doc = new Document();
			doc.append("word", tuple._1());
			doc.append("count", tuple._2());
			return doc;
		});

		Dataset<Row> ds =  spark.read().format("json").load(documents.toString());

		// write the DataFrame to MongoDB
		ds.write().format("mongodb").mode("overwrite").save();

/*		List<Tuple2<String, Integer>> results = counts.collect();
		results.forEach(System.out::println);*/

		// Aus servinglayer database auslesen:
		DocumentFrequency df = getDFByWord(searchWord);

		// Befehl ähnlich zu dem
		model.addAttribute("files", df);
		return "dokumentenfrequenz";
	}


	// Get DF by word
	public DocumentFrequency getDFByWord(String word) {
		System.out.println("Getting document frequency by word: " + word);
		DocumentFrequency df = itemRepo.findItemByName(word);
		return df;
		//System.out.println(getWordDetails(item));
	}

	// Show whole DF
	public List<DocumentFrequency> showWholeDF() {
		System.out.println("Getting the whole document frequency");
		List<DocumentFrequency> wholeDF = itemRepo.findAll();
		return wholeDF;
		//itemRepo.findAll().forEach(item -> System.out.println(getWordDetails(item)));
	}

	// Print details in readable form
	public String getWordDetails(DocumentFrequency documentFrequency) {

		System.out.println(
				"Word: " + documentFrequency.getWord() +
						", \nQuantity: " + documentFrequency.getQuantity()
		);

		return "";
	}

	// Start Spark Batch Job to calculate DF
	public DocumentFrequency calculateDF() {
		return null;
	}

	/*@GetMapping("/tutorials")
	public ResponseEntity<List<WordCount>> getAllTutorials(@RequestParam(required = false) String title) {
		try {
			List<WordCount> tutorials = new ArrayList<WordCount>();
			if (title == null)
				itemRepository.findAll().forEach(tutorials::add);
			else
				itemRepository.findByTitleContaining(title).forEach(tutorials::add);
			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping("/tutorials/{id}")
	public ResponseEntity<WordCount> getTutorialById(@PathVariable("id") String id) {
		Optional<WordCount> tutorialData = itemRepository.findById(id);
		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@PostMapping("/tutorials")
	public ResponseEntity<WordCount> createTutorial(@RequestBody WordCount tutorial) {
		try {
			WordCount _tutorial = itemRepository.save(new WordCount(tutorial.getTitle(), tutorial.getDescription(), false));
			return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping("/tutorials/{id}")
	public ResponseEntity<WordCount> updateTutorial(@PathVariable("id") String id, @RequestBody WordCount tutorial) {
		Optional<WordCount> tutorialData = itemRepository.findById(id);
		if (tutorialData.isPresent()) {
			WordCount _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());
			return new ResponseEntity<>(itemRepository.save(_tutorial), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@DeleteMapping("/tutorials/{id}")
	public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") String id) {
		try {
			itemRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@DeleteMapping("/tutorials")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			itemRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping("/tutorials/published")
	public ResponseEntity<List<WordCount>> findByPublished() {
		try {
			List<WordCount> tutorials = itemRepository.findByPublished(true);
			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/

	private String[] listTagClouds() {
		File[] files = new File(CLOUD_PATH).listFiles();
		String[] clouds = new String[files.length];

		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			if (files[i].getName().endsWith(".png"))
				clouds[i] = CLOUD_PATH + name;
		}

		return clouds;
	}

	private void createTagCloud(String filename, String inhalt) throws IOException {
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setWordFrequenciesToReturn(300);
		frequencyAnalyzer.setMinWordLength(4);

		List<String> texts = new ArrayList<>();
		texts.add(inhalt);
		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(texts);

		final Dimension dimension = new Dimension(600, 600);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new CircleBackground(300));
		wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
		wordCloud.setFontScalar(new SqrtFontScalar(8, 50));
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile(CLOUD_PATH + filename + ".png");
	}

}
