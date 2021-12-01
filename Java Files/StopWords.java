import java.io.*;
import java.util.*;

public class StopWords{
    
    public static HashMap<String, String> allStopWords = new HashMap<String, String>();
   
    //This function will read the stop list file and fill a hash map with all the words in the stop list file
    public void fillStopWordsHashMap(String NameOfStopListFile){
        
        try(BufferedReader file = new BufferedReader(new FileReader(NameOfStopListFile))){
                    
            String lineBeingRead = "";

            while((lineBeingRead = file.readLine()) != null){
                        
                String[] currentLine = lineBeingRead.split("\\W+");
                String currentWord = "";   

                for(int x = 0; x < currentLine.length; x++){
                            
                    currentWord = currentLine[x]; 
                    allStopWords.put(currentWord, "");
                }
            }
        }catch (IOException e){
            System.out.println("StopListFile : " + NameOfStopListFile + " not found.");  
        }       
    }
    
    //This will check if a word is a stopword, if it is a stopword it will return true, if its not a stopword it will return false
    public static boolean isStopWord(String word){
        
        if(allStopWords.containsKey(word))
            return true;

        return false;
    }
}