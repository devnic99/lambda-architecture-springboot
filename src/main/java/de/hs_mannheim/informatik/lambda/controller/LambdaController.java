package de.hs_mannheim.informatik.lambda.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.*;

//import de.hs_mannheim.informatik.lambda.SparkSessionSingleton;
import de.hs_mannheim.informatik.lambda.model.DocumentFrequency;
import de.hs_mannheim.informatik.lambda.repository.ItemRepository;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import scala.Tuple2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;
import java.nio.file.*;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

@Controller
public class LambdaController {

	@Autowired
	ItemRepository itemRepo;

	public final static String CLOUD_PATH = "tagclouds/";

	public final static String RAW_PATH = "rawfiles/";

	@GetMapping("/upload")
	public String forward(Model model) {
		model.addAttribute("files", listTagClouds());

		return "upload";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
		try {
			saveFile(file);
			model.addAttribute("message", "Datei erfolgreich hochgeladen: " + file.getOriginalFilename());
			createTagCloud(file.getOriginalFilename(), new String(file.getBytes()));
			model.addAttribute("files", listTagClouds());
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("message", "Da gab es einen Fehler: " + e.getMessage());
		}

		return "upload";
	}

	@GetMapping("/dokumentenfrequenz")
	public String forwardToDf(Model model) {
		/*try {
			model.addAttribute("message", "Dokumentenfrequenz erfolgreich berechnet");
			//Map<String, Integer> dfList = calculateDF();
			calculateDF();

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("message", "Da gab es einen Fehler: " + e.getMessage());
		}*/

		model.addAttribute("message", "Dokumentenfrequenz erfolgreich berechnet");
		//Map<String, Integer> dfList = calculateDF();
		calculateDF();

		return "dokumentenfrequenz";
	}




	@PostMapping("/uploadWord")
	public String handleWordUpload(@RequestParam("word") String word, Model model) {
		model.addAttribute("word", word);
		return "upload";
	}

	@GetMapping("/uploadWord")
	public String handleWordUploadGet(Model model) {
		return "upload";
	}

	/*@GetMapping("/dokumentenfrequenz")
	public String handleDFCalculation(String searchWord, Model model) {

		//Batch Job auslösen:

		File[] files = new File(CLOUD_PATH).listFiles();
		SparkSession spark = SparkSessionSingleton.getInstance();

		// Create RDD from text files
		//JavaRDD<String> lines = spark.textFile(files).javaRDD();

		// Split each line into words and create a flatMap of words
		//JavaRDD<String> words = lines.flatMap(
				//line -> Arrays.asList(line.split("\\W+")).iterator());

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

		//Dataset<Row> ds =  spark.read().format("json").load(documents);

		// write the DataFrame to MongoDB
		ds.write().format("mongodb").mode("overwrite").save();

*//*		List<Tuple2<String, Integer>> results = counts.collect();
		results.forEach(System.out::println);*//*

		// Aus servinglayer database auslesen:
		DocumentFrequency df = getDFByWord(searchWord);

		// Befehl ähnlich zu dem
		model.addAttribute("files", df);
		return "dokumentenfrequenz";
	}*/

/*	@PostMapping("/dokumentenfrequenz")
	public String handleDFCalculation2(@RequestParam("file") MultipartFile file, Model model) {

		try {
			model.addAttribute("message", "Datei erfolgreich hochgeladen: " + file.getOriginalFilename());
			createTagCloud(file.getOriginalFilename(), new String(file.getBytes()));
			model.addAttribute("files", listTagClouds());
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("message", "Da gab es einen Fehler: " + e.getMessage());
		}

		return "dokumentenfrequenz";
	}*/

	private void saveFile(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			Path uploadDir = Paths.get(RAW_PATH);
			Path filePath = uploadDir.resolve(filename);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
		}

	}

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
	public void calculateDF() {
		System.out.println("calculateDF wird ausgeführt");
		SparkConf conf = new SparkConf().setAppName("wc").setMaster("local[4]");
		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaRDD<String> tokens = sc.textFile("Lambda-Architecture/rawfiles/loremipsum.txt").flatMap(
				s -> Arrays.asList(s.split("\\W+")).iterator());

		JavaPairRDD<String, Integer> counts = tokens.mapToPair(
				token -> new Tuple2<>(token, 1)).reduceByKey((x,y) -> x+y);

		List<Tuple2<String, Integer>> results = counts.collect();
		results.forEach(System.out::println);

		sc.close();

		// Batch Job auslösen:
		/*SparkSession spark = SparkSessionSingleton.getInstance();
		String dir = "Lambda-Architecture/rawfiles/*";

		SparkConf conf = new SparkConf().setAppName("wc").setMaster("local[4]");
		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaRDD<String> tokens = sc.textFile("Lambda-Architecture/rawfiles/loremipsum.txt").flatMap(
				s -> Arrays.asList(s.split("\\W+")).iterator());

		JavaPairRDD<String, Integer> counts = tokens.mapToPair(
				token -> new Tuple2<>(token, 1)).reduceByKey((x,y) -> x+y);

		List<Tuple2<String, Integer>> results = counts.collect();
		results.forEach(System.out::println);

		sc.close();*/

/*
		// read text files and create RDD of file contents
		JavaRDD<String> fileContentsRDD = spark.sparkContext().textFile(dir, 2).toJavaRDD();

		// split lines into words
		JavaRDD<String> wordsRDD = fileContentsRDD.flatMap(line -> Arrays.asList(line.split("\\W+")).iterator());

		// count occurrences of each word
		JavaPairRDD<String, Integer> wordCountsRDD = wordsRDD
				.mapToPair(word -> new Tuple2<>(word, 1))
				.reduceByKey((count1, count2) -> count1 + count2);

		List<Tuple2<String, Integer>> wordCounts = wordCountsRDD.collect();

		wordCounts.forEach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));
*/



/*
		File[] files = new File(RAW_PATH).listFiles();
		String[] filePaths = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			filePaths[i] = files[i].getPath();
		}

		Dataset<Row> lines = spark.read().wholeTextFile(filePaths);

		// Einlesen des Texts aus der Datei und Aufspaltung in Wörter


		for (String filePath : filePaths) {
			DataFrame lines = spark.read().text(filePath);



			Dataset<String> words = lines.flatMap(row -> Arrays.asList(row.getString(0).split("\\W+")).iterator(), Encoders.STRING());
			Dataset<Tuple2<String, Integer>> counts = tokens.mapToPair(token -> new Tuple2<>(token, 1), Encoders.tuple(Encoders.STRING(), Encoders.INT()))
					.groupByKey(pair -> pair._1())
					.reduceGroups((x, y) -> new Tuple2<>(x._1(), x._2() + y._2()))
					.map(row -> row._2(), Encoders.tuple(Encoders.STRING(), Encoders.INT()));


			JavaRDD<String> tokens = sc.textFile("spark-it/src/main/resources/Faust.txt").flatMap(
					s -> Arrays.asList(s.split("\\W+")).iterator());
		}
		// Create DataFrame representing the stream of input lines from connection to localhost:9999
		Dataset<Row> lines = spark
				.readStream()
				.format("socket")
				.option("host", "localhost")
				.option("port", 9999)
				.load();

// Split the lines into words
		Dataset<String> words = lines
				.as(Encoders.STRING())
				.flatMap((FlatMapFunction<String, String>) x -> Arrays.asList(x.split(" ")).iterator(), Encoders.STRING());

// Generate running word count
		Dataset<Row> wordCounts = words.groupBy("value").count();


		JavaPairRDD<String, Integer> counts = tokens.mapToPair(
				token -> new Tuple2<>(token, 1)).reduceByKey((x,y) -> x+y);

		List<Tuple2<String, Integer>> results = counts.collect();
		results.forEach(System.out::println);







		// Create RDD from text files
		JavaRDD<String> lines = spark.textFile(files).javaRDD();

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

		Dataset<Row> ds =  spark.read().format("json").load(documents);

		// write the DataFrame to MongoDB
		ds.write().format("mongodb").mode("overwrite").save();

		List<Tuple2<String, Integer>> results = counts.collect();
		results.forEach(System.out::println);*//*

		// Aus servinglayer database auslesen:
		DocumentFrequency df = getDFByWord(searchWord);

		Map<String, Integer> result = new HashMap<String, Integer>();
		return result;
		*/
	}
}
