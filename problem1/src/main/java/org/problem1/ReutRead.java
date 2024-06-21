package org.problem1;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

public class ReutRead {
    /**
     * Read Reuter file
     * @param fileName is the filename
     * @return content of the file in String
     */
    public static String readReuterFile(String fileName){
        StringBuilder reuterFileContent = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                reuterFileContent.append(line).append("\n");
            }
            return reuterFileContent.toString();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Transforms raw content by extracting information.
     * @param rawContent is the content
     */
    public static void extractNews(String rawContent, MongoCollection<Document> collection){
        String[] news = rawContent.split("(?i)<REUTERS[^>]*>"); // Extract individual news
        // Parse through each news
        for (int i = 1; i < news.length; i++) {
            String newsTitle = filterText(extractTextBetweenTags(news[i], "TITLE"));
            String newsBody = filterText(extractTextBetweenTags(news[i], "BODY"));
            addDataToMongoDB(newsTitle, newsBody, collection);
        }
    }

    /**
     * Inserts data to mongoDb
     * @param titleText is the title of the news
     * @param bodyText is the body of the news
     * @param collection is mongoDb collection
     */
    private static void addDataToMongoDB(String titleText, String bodyText, MongoCollection<Document> collection) {
        // Inserting title and body together by combining
        Document document = new Document("title", titleText).append("body", bodyText);
        collection.insertOne(document);
        System.out.println("Document added to MongoDB:");
        System.out.println(document.toJson());
    }

    /**
     * extract text between specified tags
     * @param input is the content
     * @param tagName is either "Title" or "Body"
     * @return extracted text
     */
    private static String extractTextBetweenTags(String input, String tagName) {
        String startTag = "<" + tagName + ">"; // Forming start tag
        String endTag = "</" + tagName + ">"; // Forming end tag
        int startIndex = input.indexOf(startTag);
        int endIndex = input.indexOf(endTag, startIndex + startTag.length());
        if (startIndex != -1 && endIndex != -1) {
            return input.substring(startIndex + startTag.length(), endIndex).trim();
        } else {
            return ""; // I'm returning blank string as a indication that no tag is found
        }
    }

    /**
     * Remove unwanted content
     * @param input string
     * @return filtered string
     */
    private static String filterText(String input) {
        // Remove "&#3;", "&#5;", "&lt;", and special symbol.
        String textWithSymbolAndHTMLTags = input.replaceAll("&#\\d+;|&lt;", "");
        String textWithHTML = textWithSymbolAndHTMLTags.replaceAll("<[^>]+>", "");
        String cleanedText = textWithHTML.replaceAll("[\",.:=<>]", "");
        return cleanedText.trim();
    }

    /**
     * Removes stop words
     * @param inputText is content
     * @param stopWordsFilePath is path for stop words
     * @return filtered text
     */
    public static String removeStopWords(String inputText, String stopWordsFilePath) {
        Set<String> stopWords = loadStopWords(stopWordsFilePath); //loading stop words from file
        String[] tokens = inputText.split("\\s+"); // splitting each words in list

        StringBuilder textWithoutStopWords = new StringBuilder();
        // Iterating through each stop words and token to check.
        for (String token : tokens) {
            if (!stopWords.contains(token.toLowerCase())) {
                textWithoutStopWords.append(token).append(" ");
            }
        }
        return textWithoutStopWords.toString().trim();
    }

    /**
     * Loads the stop words
     * @param stopWordsFilePath is the file path
     * @return unique list of stop words
     */
    private static Set<String> loadStopWords(String stopWordsFilePath) {
        Set<String> stopWords = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    /**
     * Generates cleaned reut009 file
     */
    public static void generateCleanReut009File(){
        System.out.println("Generating Clean Reuter file...");
        String content = readReuterFile("src/main/resources/reuters/reut2-009.sgm");
        String stopWordFile = "src/main/resources/assets/stopwords.txt";
        String outputCleanedFile = "src/main/resources/output/cleaned-reut2-009.sgm";
        String cleanedContent = removeStopWords(filterText(content), stopWordFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputCleanedFile));
            writer.write(cleanedContent);
            writer.close();
            System.out.println("Reuter File successfully generated!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
