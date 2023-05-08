package de.hs_mannheim.informatik.lambda;

import org.apache.spark.sql.SparkSession;

public class SparkSessionSingleton {
    private static transient SparkSession instance = null;
    public SparkSessionSingleton() {}

    public static SparkSession getInstance() {
        if (instance == null) {
            instance = SparkSession
                    .builder()
                    .master("local")
                    .appName("wc")
                    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/servinglayer.df")
                    .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/servinglayer.df")
                    .getOrCreate();
        }
        return instance;
    }
}