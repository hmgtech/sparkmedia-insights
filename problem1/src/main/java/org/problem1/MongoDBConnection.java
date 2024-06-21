package org.problem1;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection {
    /**
     * Build connection and returns mongoConnection
     * @param connectionString is the connection string url
     * @return mongoClient Object
     */
    public static MongoClient makeConnection(String connectionString){
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        return MongoClients.create(settings);
    }

    /**
     * Returns database reference
     * @param mongoClient is mongoDb client
     * @param databaseName is databaseName
     * @param collectionName is collectionName
     * @return database reference
     */
    public static MongoCollection<Document> readDatabase(MongoClient mongoClient, String databaseName, String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        System.out.println("Database Name: " + database.getName());
        return database.getCollection(collectionName);
    }
}
