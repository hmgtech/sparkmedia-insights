package org.problem1;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.problem1.ReutRead.generateCleanReut009File;

public class WordFrequencySpark {

    public static void main(String[] args) {
        generateCleanReut009File();
        startSpark();
    }

    /**
     * Initiates the apache spark to process reut-009.txt file
     */
    public static void startSpark() {
        System.out.println("Starting Spark System...");
        SparkConf conf = new SparkConf().setAppName("WordFrequencySpark").setMaster("local[*]"); // Setting up Spark configuration
        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            String inputFilePath = "./cleaned-reut2-009.sgm"; // Load ret2-009 text file
            System.out.println("Starting Spark System...");
            JavaRDD<String> lines = sc.textFile(inputFilePath); // Reading text tile using Java Resilient Distributed Dataset (RDD)
            System.out.println("Reading file...");
            JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator()); // Tokenize words
            System.out.println("Mapping...");
            JavaPairRDD<String, Integer> wordCounts = words.mapToPair(word -> new Tuple2<>(word, 1)); // Map each word to a tuple
            System.out.println("Reduce...");
            JavaPairRDD<String, Integer> wordFrequencies = wordCounts.reduceByKey(Integer::sum); // Reduce by key to calculate word frequencies
            List<Tuple2<String, Integer>> results = wordFrequencies.collect(); // Collect the results locally to preprocess
            System.out.println("Saving data to file...");
            saveResultsToFile(results); // Save result in txt file
            Tuple2<String, Integer> minCountWord = findMinCountWord(results); // Find minimum count word
            Tuple2<String, Integer> maxCountWord = findMaxCountWord(results); // Find maximum count word
            sc.stop(); // Stop Spark context
            System.out.println("Minimum Count Word -> " + minCountWord._1 + " | Count: " + minCountWord._2); // Printing the minimum word count
            System.out.println("Maximum Count Word -> " + maxCountWord._1 + " | Count: " + maxCountWord._2); // Printing the maximum word count
            System.out.println("Successfully stopped Spark System...");
        }
    }

    /**
     * Save data to word_frequencies.txt file
     * @param results is the word frequency count
     */
    private static void saveResultsToFile(List<Tuple2<String, Integer>> results) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./wordFrequencies.txt"))) {
            for (Tuple2<String, Integer> result : results) {
                writer.write(result._1() + ": " + result._2() + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Finds the word with the minimum count
     * @param results is content from which minimum word count will be searched
     * @return tuple of string word and int count
     */
        private static Tuple2<String, Integer> findMinCountWord(List<Tuple2<String, Integer>> results) {
        return Collections.min(results, Comparator.comparing(Tuple2::_2));
    }

    /**
     * Finds the word with the maximum count
     * @param results is content from which maximum word count will be searched
     * @return tuple of string word and int count
     */
    private static Tuple2<String, Integer> findMaxCountWord(List<Tuple2<String, Integer>> results) {
        return Collections.max(results, Comparator.comparing(Tuple2::_2));
    }
}

