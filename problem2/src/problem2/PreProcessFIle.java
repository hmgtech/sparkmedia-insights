package problem2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessFIle {

    /**
     * Extract list of titles from the file
     * @param folderPath is folder path where .sgm files stored
     * @param listOfFiles is the list of files
     * @return list of titles
     */
    public static List<String> extractTitle(String folderPath, File listOfFiles) {
        String filePath = folderPath + listOfFiles.getName();
        String content = readReuterFile(filePath);
        return extractTitles(content);
    }

    /**
     * Checks if it is Reuter file or not
     * @param listOfFiles is list of files
     * @return true ot false
     */
    public static boolean isReuterFile(File listOfFiles) {
        return listOfFiles.isFile() && listOfFiles.getName().toLowerCase().endsWith(".sgm");
    }

    /**
     * Extract title from the raw text
     * @param content is the raw content
     * @return list of titles
     */
    public static List<String> extractTitles(String content) {
        List<String> titles = new ArrayList<>();
        Pattern pattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String title = matcher.group(1);
            titles.add(filterText(title));
        }
        return titles;
    }

    /**
     * Returns raw content of the reuter(.sgm) file
     * @param fileName is the file name
     * @return reuter content
     */
    public static String readReuterFile(String fileName){
        try{
            return new Scanner(new File(fileName)).useDelimiter("\\Z").next();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return "";
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
}
