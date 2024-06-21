package org.problem1;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

import static org.problem1.MongoDBConnection.makeConnection;
import static org.problem1.MongoDBConnection.readDatabase;
import static org.problem1.ReutRead.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        start();
    }

    /**
     * Setup connection with mongoDB, read database and write into it.
     * @throws FileNotFoundException if no file is found
     */
    public static void start() throws FileNotFoundException {
        String connectionString = "mongodb+srv://b00954481:b00954481@reuterdb.r0zhkyt.mongodb.net/?retryWrites=true&w=majority";
        String databaseName = "ReuterDB";
        String collectionName = "ReuterNewsColletion";
        String folderPath = "src/main/resources/reuters/";
        MongoClient mongoClient = makeConnection(connectionString);
        MongoCollection<Document> collection = readDatabase(mongoClient, databaseName, collectionName);
        insertInDb(folderPath, collection);
        System.out.println("Data added successfully!");
    }

    /**
     * Insert data in mongoDb
     * @param folderPath is source folder
     * @param collection is mongoDb collection
     */
    private static void insertInDb(String folderPath, MongoCollection<Document> collection) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        try {
            for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().endsWith(".sgm")) {
                    String filePath = folderPath + listOfFiles[i].getName();
                    String content = readReuterFile(filePath);
                    extractNews(content, collection);
                }
            }
        }
        catch (NullPointerException nullPointerException){
            System.out.println("No Reuter files Found!");
            System.exit(1);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}