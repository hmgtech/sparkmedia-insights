package problem2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BagOfWords {

    static List<String> positiveWords;
    static List<String> negativeWords;
    static int newsCounter = 1;

    /**
     * Driver code to find sentiment of news
     */
    public static void start(){
        Utils.printHeader();
        Utils.createCSVFile();
        String positiveWordFilePath = "src/assets/positive-words.txt";
        String negativeWordFilePath = "src/assets/negative-words.txt";
        positiveWords = readPositiveWordsFromFile(positiveWordFilePath);
        negativeWords = readPositiveWordsFromFile(negativeWordFilePath);

        String folderPath = "src/assets/";
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
            if (PreProcessFIle.isReuterFile(listOfFiles[i])) {
                List<String> titles = PreProcessFIle.extractTitle(folderPath, listOfFiles[i]);
                for (String title : titles) {
                    Map<String, Integer> bagOfWords = createBagOfWords(title);
                    /* Commenting this because I am printing data in tabular format.
                    System.out.println("News: " + title);
                    System.out.println("Bow: " + bagOfWords);
                    System.out.println("-------------------------------------------------------------------------------");
                    */
                    getSentiment(title);
                }
            }
        }
        Utils.doAnalysis(--newsCounter);
    }

    /**
     * Create bag of words
     * @param inputString is the content
     * @return String and count of words
     */
    public static Map<String, Integer> createBagOfWords(String inputString) {
        Map<String, Integer> bagOfWords = new HashMap<>();
        String[] words = inputString.toLowerCase().split("\\s+"); // Split the input string into words
        for (String word : words) {
            bagOfWords.put(word, bagOfWords.getOrDefault(word, 0) + 1);
        }
        return bagOfWords;
    }

    /**
     * Generating sentiment of the title based on positive and negative score
     * print table and writes result to csv file
     * @param title is the title of news
     */
    public static void getSentiment(String title){
        int positivePolarity = 0;
        int negativePolarity = 0;
        List<String> matchedWords = new ArrayList<>();
        String[] words = title.split("\\s+");
        for (String word : words) {
            if (positiveWords.contains(word.toLowerCase())) {
                matchedWords.add(word);
                positivePolarity++;
            }
            else if (negativeWords.contains(word.toLowerCase())) {
                matchedWords.add(word);
                negativePolarity++;
            }
        }
        Utils.printData(title,positivePolarity,negativePolarity, (positivePolarity - negativePolarity),
                String.join(", ", matchedWords),Utils.getPolarity((positivePolarity - negativePolarity)),
                newsCounter
        );
        String[] rowData = {String.valueOf(newsCounter), title, String.valueOf(positivePolarity), String.valueOf(negativePolarity),
                String.valueOf((positivePolarity - negativePolarity)),
                String.join(", ", matchedWords),Utils.getPolarity((positivePolarity - negativePolarity))
                };
        Utils.writeDataToCSV(rowData);
        newsCounter++;
    }

    /**
     * Read positive words from the file
     * @param filePath is the file path
     * @return list of positive words
     */
    private static List<String> readPositiveWordsFromFile(String filePath) {
        List<String> listOfWords = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                listOfWords.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("No file found: "+ filePath);
            System.exit(1);
        }
        return listOfWords;
    }
}
