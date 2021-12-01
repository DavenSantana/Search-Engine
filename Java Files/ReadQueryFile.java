import java.io.*;
import java.util.*;
        
public class ReadQueryFile{
    
    public static ArrayList<String> wordsToQuery = new ArrayList<>();
    public static ArrayList<String> stopWords = new ArrayList<>();
    public static ArrayList<String> notInAnyDocument = new ArrayList<>();
    public static ArrayList<String> wordsToStem = new ArrayList<>();
    public static ArrayList<String> stemmedWordsToQuery = new ArrayList<>();
    public static ArrayList<String> stemmedWordsStopWords = new ArrayList<>();
    public static ArrayList<String> stemmedWordsThatStayedTheSame = new ArrayList<>();
    public static ArrayList<String> stemmedWordsNotInAnyDocument = new ArrayList<>();
    
    
    //This method will read the query file and add every query to a specific array depending on what the word is
    //If the word exists then this will go into wordsToQuery, if the word is a stop word it will go into StopWords, if it doesn't exist
    //in any document it will go into notInAnyDocument, this is so if someone looks up a stopword they will know it is a stopword and not
    //query that word again
    public static void readQuery(String QueryFile, String ResultsFile, BuildAllIndex index, String SearchWord, String SearchDoc, String WriteDecision, String Stem, String Snippet, String Output){

        try(BufferedReader file = new BufferedReader(new FileReader(QueryFile))){
                    
            String lineBeingRead = "";

            
            //So take every word in the query file and add it to its own separate array
            //Then create a function that takes in that array, and stems every word in that array - Now we have all the stemmed words
            //Now after we do this, we will run another for loop on the array we get and we will organize the words to where they should go
            
            while((lineBeingRead = file.readLine()) != null){
                        
                String[] currentLine = lineBeingRead.split("\\W+");
                String currentWord = "";
                int copy = 0;

                for(int x = 0; x < currentLine.length; x++){
                    
                    currentWord = currentLine[x]; 
                    
                    if(currentWord.equals("Query")){ //Query file must follow a certain format, if the first word in a line isn't "Query" it won't work
                        x++;
                        currentWord = currentLine[x]; //Get the word after query which should be our query term
                        
                        wordsToStem.add(currentWord);
                        
                        if(index.myMap.containsKey(currentWord)) //If the word exists in a document
                            wordsToQuery.add(currentWord);
                        else if(index.allStopWords.isStopWord(currentWord)) //If the word is a stopword
                            stopWords.add(currentWord);
                        else
                            notInAnyDocument.add(currentWord); //If the word isn't a stopword and doesn't exist
                    }               
                }
            }
            
            if(Stem.equals("yes")){ //If the stem decision was yes we will stem it here
                
                wordsToStem = stemWords(wordsToStem); //Take the array with all the words in the query file and stem them by using the stemWords method that calls on Stemmer
                
                for(int x = 0; x < wordsToStem.size(); x++){ //For loop to decide where the word belongs, does the word exist is a file, does it not exist, does the word stay the same even after being stemmed
                    
                    if(index.myMap.containsKey(wordsToStem.get(x)) && !sameWordWhenStemmed(wordsToQuery, wordsToStem.get(x))) //If the word is contained in a document and it's not the same word after being stemmed we will query it here
                        stemmedWordsToQuery.add(wordsToStem.get(x));
                    else if(index.allStopWords.isStopWord(wordsToStem.get(x))) //If the word is a stopword we will add it to this array and print that it's a stopword
                        stemmedWordsStopWords.add(wordsToStem.get(x));
                    else if(sameWordWhenStemmed(wordsToQuery, wordsToStem.get(x))) //If the word stayed the same meaning it has no stem we will print that it stayed the same
                        stemmedWordsThatStayedTheSame.add(wordsToStem.get(x));
                    else
                        stemmedWordsNotInAnyDocument.add(wordsToStem.get(x)); //If the word doesn't exist then we will print that it doesn't exist
                }
            }
            
            if(Output.equals("file"))
                Print.printToResultsFile(ResultsFile, wordsToQuery, stopWords, notInAnyDocument, stemmedWordsToQuery, stemmedWordsStopWords, stemmedWordsNotInAnyDocument, stemmedWordsThatStayedTheSame, index, SearchWord, SearchDoc, WriteDecision, Stem, Snippet); //Function to print all the information to ResultsFile
            else if(Output.equals("gui"))
                Print.printToGUI(wordsToQuery, stopWords, notInAnyDocument, stemmedWordsToQuery, stemmedWordsStopWords, stemmedWordsNotInAnyDocument, stemmedWordsThatStayedTheSame, index, SearchWord, SearchDoc, Stem, Snippet);
            else if(Output.equals("both")){
                Print.printToResultsFile(ResultsFile, wordsToQuery, stopWords, notInAnyDocument, stemmedWordsToQuery, stemmedWordsStopWords, stemmedWordsNotInAnyDocument, stemmedWordsThatStayedTheSame, index, SearchWord, SearchDoc, WriteDecision, Stem, Snippet);
                Print.printToGUI(wordsToQuery, stopWords, notInAnyDocument, stemmedWordsToQuery, stemmedWordsStopWords, stemmedWordsNotInAnyDocument, stemmedWordsThatStayedTheSame, index, SearchWord, SearchDoc, Stem, Snippet);
            }     
        }catch (IOException e){                                                                                                            
            System.out.println("File " + QueryFile + " not found.");                                                                      
        }       
    }
    
    //This method calls on the stemmer
    public static ArrayList<String> stemWords(ArrayList<String> wordsToStem){
        
        ArrayList<String> stemmedWords = new ArrayList<>(); //This is the array that has the words after we stem them
        Stemmer s = new Stemmer(); //Instantiate the stemmer class
        String tempword = ""; //Keep a tempword so we know which word we're stemming
        String stemmedWord = ""; //After we stem the word save it here
        
        for(int x = 0; x < wordsToStem.size(); x++){ //Iterate through the array that has the words that we need to stem
            tempword = wordsToStem.get(x); //Save the word at position x
            for(int y = 0; y < tempword.length(); y++){ //This is how the stemmer works so this for loop just does what the stemmer needs to do
                s.add(tempword.charAt(y));              //This is how the code was shown from the code I used for the stemmer
            }
            s.stem(); //Stem the word
            stemmedWord = s.toString(); //More code from the stemmer class I used
            stemmedWords.add(stemmedWord); //Add to the arraylist that has all the stemmed words the word we just stemmed
        }    

        return stemmedWords; //Return all the stemmed words
    }
    
    //This method just checks that if the word we stemmed is just the same word meaning it has no stem, for example, goodbye = goodbye
    public static boolean sameWordWhenStemmed(ArrayList<String> originalQuery, String stemmedQuery){
        
        for(int x = 0; x < originalQuery.size(); x++){
            if(originalQuery.get(x).equals(stemmedQuery))
                return true; //If the word is the same after stemming return true
        }
        
        return false;
    }
    
}