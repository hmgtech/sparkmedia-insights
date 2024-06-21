package problem2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {
    static int totalPositive = 0;
    static int totalNegative = 0;
    static int totalNeutral = 0;

    /**
     * Prints header of the table
     */
    public static void printHeader(){
        int widthOfTable = 160;
        for (int i = 1; i < widthOfTable; i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.print("|");
        String title = "Sentiment Analysis using BOW model";
        int titlePadding = (widthOfTable - title.length()) / 2;
        System.out.printf("%" + titlePadding + "s%s%" + (titlePadding) + "s%n", "", title, "|");
        for (int i = 1; i < widthOfTable; i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.printf("| %-8s | %-50s | %16s | %16s | %13s | %26s | %10s |%n",
                "News #", "Title", "Positive Score", "Negative Score",
                "Total Score", "Matched Words", "Polarity");
        for (int i = 1; i < widthOfTable; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * Prints content of the table
     * @param title is the title of the news
     * @param positiveScore is positive score
     * @param negativeScore is negative score
     * @param totalScore is total score
     * @param matchedWords is the words matched with positive and negative words
     * @param polarity is polarity
     * @param newsCounter is news number
     */
    public static void printData(String title, int positiveScore,
                                 int negativeScore, int totalScore, String matchedWords,
                                 String polarity, int newsCounter) {
        int lineLength = 160;
        int titleMaxLength = 50;
        // Here I am checking, if length of the title is bigger then 50 chars, then
        // I am dividing them into two halves so that it wont break the table columns
        if (title.length() > titleMaxLength) {
            String[] titleLines = splitTitle(title, titleMaxLength);
            title = titleLines[0];
            System.out.printf("| %-8s | %-50s | %16s | %16s | %13s | %26s | %10s |%n",
                    newsCounter, title, positiveScore, negativeScore,
                    totalScore, matchedWords, polarity);
            System.out.printf("| %-8s | %-50s | %16s | %16s | %13s | %26s | %10s |%n",
                    "", titleLines[1], "", "", "", "", "");
        }
        // Else, one in one row, it can adjust
        else {
            System.out.printf("| %-8s | %-50s | %16s | %16s | %13s | %26s | %10s |%n",
                    newsCounter, title, positiveScore, negativeScore,
                    totalScore, matchedWords, polarity);
        }

        for (int i = 1; i < lineLength; i++) {
            System.out.print("-");
        }
        System.out.println();
    }


    /**
     * Checks if the title is too long, the break the title into two halves and
     * print titles in two lines
     * @param title is the title
     * @param maxLength allocated to the title space
     * @return array of title
     */
    public static String[] splitTitle(String title, int maxLength) {
        String[] result = new String[2];
        // Do I need to split? If yes then
        if (title.length() > maxLength) {
            int middle = title.length() / 2;
            int indexOfSpace = title.lastIndexOf(' ', maxLength);
            //Here instead of splitting form anywhere, I am finding a space so that I won't any meaning of title
            if (splitFromSpace(indexOfSpace, middle)) {
                result[0] = title.substring(0, indexOfSpace);
                result[1] = title.substring(indexOfSpace + 1);
            }
            //Else, I need to split title from between
            else {
                result[0] = title.substring(0, middle);
                result[1] = title.substring(middle);
            }
        }
        //Else no splitting requires
        else {
            result[0] = title;
            result[1] = ""; // Set an empty string for the second line
        }
        return result;
    }

    /**
     * Generated polarity of the news
     * @param score is the final score of the news
     * @return String either "Positive" or "Negative"
     */
    public static String getPolarity(int score) {
        if (score > 0){
            totalPositive++;
            return "Positive";
        }
        else if(score == 0){
            totalNeutral++;
            return "Neutral";
        }
        else{
            totalNegative++;
            return "Negative";
        }
    }
    private static boolean splitFromSpace(int indexOfSpace, int middle) {
        return indexOfSpace != -1 && indexOfSpace >= middle - 10 && indexOfSpace <= middle + 10;
    }

    /**
     * Analyse the positive, negative and neutral news.
     * @param newsCounter is total news
     */
    public static void doAnalysis(int newsCounter){
        System.out.println("Total News: " + newsCounter);
        System.out.println("Total Positive News: " + totalPositive/2 + " => " + ((float)totalPositive/newsCounter)*100/2 + " %");
        System.out.println("Total Negative News: " + totalNegative/2 + " => " + ((float)totalNegative/newsCounter)*100/2 + " %");
        System.out.println("Total Neutral News: " + totalNeutral/2 + " => " + ((float)totalNeutral/newsCounter)*100/2 + " %");
    }

    /**
     * Create new csv file for storing data and add headers to it
     */
    public static void createCSVFile(){
        String[] headersForCSV = {"News #", "Title", "Positive Score", "Negative Score",
                "Total Score", "Matched Words", "Polarity"};
        try (BufferedWriter createCSV = new BufferedWriter(new FileWriter("./csv-output.csv"))) {
            // Write the headersForCSV line
            createCSV.write(String.join(",", headersForCSV));
            createCSV.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Append data to csv file.
     * @param rowData is the row which will be added to csv file
     */
    public static void writeDataToCSV(String[] rowData){
        try (BufferedWriter csvWrite = new BufferedWriter(new FileWriter("./csv-output.csv", true))) {
            csvWrite.write(String.join(",", rowData));
            csvWrite.newLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
