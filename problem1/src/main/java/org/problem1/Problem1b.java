package org.problem1;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Problem1b {
    static List<String> stopWords = new ArrayList<>();
    public static void main(String[] args) {
        loadStopWords();
        calculateFrequency();
    }

    private static void calculateFrequency() {
        SparkConf sparkCongig = new SparkConf().setAppName("CalculateWordFrequency").setMaster("local[*]");

        JavaSparkContext sc = new JavaSparkContext(sparkCongig);
        JavaRDD<String> textRDD = sc.textFile("reut2-009.sgm");

        JavaPairRDD<String, Integer> wordFrequencies = textRDD
                .flatMap(line -> Arrays.asList(line.split("\\s+")).iterator())
                .mapToPair(word -> new Tuple2<>(word.toLowerCase(), 1))
                .reduceByKey((countOne, countTwo) -> countOne + countTwo);

        AtomicReference<String> mostFrequentWord = new AtomicReference<>("");
        AtomicReference<String> leastFrequentWord = new AtomicReference<>("");
        AtomicInteger mostFrequentWordCount = new AtomicInteger();
        AtomicInteger leastFrequentWordCount = new AtomicInteger(1000);

        wordFrequencies.collect().forEach(tuple -> {
            String currentWord = tuple._1();
            Integer currentWordCount = tuple._2();

            if (!stopWords.contains(currentWord) && !currentWord.contentEquals("") && !currentWord.contentEquals(" ")) {
                if (currentWordCount > mostFrequentWordCount.get()) {
                    mostFrequentWordCount.set(currentWordCount);
                    mostFrequentWord.set(currentWord);
                }
                if (currentWordCount < leastFrequentWordCount.get()) {
                    leastFrequentWordCount.set(currentWordCount);
                    leastFrequentWord.set(currentWord);
                }
            }
        });
        System.out.println("Most frequent word: " + mostFrequentWord.get() + " | Count: " + mostFrequentWordCount.get());
        System.out.println("Least frequent word: " + leastFrequentWord.get() + " | Count: " + leastFrequentWordCount.get());
        sc.stop();
    }

    public static void loadStopWords() {
        String file1Path = "stopwords.txt";
        try {
            BufferedReader stopWordsFile = new BufferedReader(new FileReader(file1Path));
            for (String s : stopWordsFile.lines().collect(Collectors.toList())) {
                stopWords.add(s);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
