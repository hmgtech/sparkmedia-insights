package org.problem1;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SparkWordCounter {
    public static void main(String[] args) {
        wordCount();
    }
    public static void wordCount() {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Spark Word Counter");

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        JavaRDD<String> inputFile = sparkContext.textFile("cleaned-reut2-009.sgm");

        JavaRDD<String> wordsFromFile = inputFile.flatMap(content -> Arrays.asList(content.split("\\s")).iterator());

        JavaPairRDD countData = wordsFromFile.mapToPair(t -> new Tuple2(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);

        List<Tuple2<String, Integer>> results = countData.collect();

        Tuple2<String, Integer> minWOrd = Collections.min(results, Comparator.comparing(Tuple2::_2));
        Tuple2<String, Integer> maxWOrd = Collections.max(results, Comparator.comparing(Tuple2::_2));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ReuterWordFrequency.txt"))) {
            for (Tuple2<String, Integer> result : results) {
                writer.write(result._1() + ": " + result._2() + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Minimum Frequency Word -> " + minWOrd._1 + " " + minWOrd._2);
        System.out.println("Maximum Frequency Word -> " + maxWOrd._1 + " " + maxWOrd._2);

        sparkContext.stop();
    }
}