import java.io.*;
import java.util.*;

public class BuildAllIndex{
     
        public HashMap<String, WordsForUniversalIndex> myMap = new HashMap<String, WordsForUniversalIndex>();
        public WordsForUniversalIndex invertedIndexWord = new WordsForUniversalIndex("");
        public ArrayList<Document> allDocuments = new ArrayList<>();
        public WordsForDocumentIndex wordForDocument = new WordsForDocumentIndex("");
        public Document document = new Document("");
        public StopWords allStopWords = new StopWords();
        public int wordspassed = 0;
        int n = 0;
        
        public void buildIndex(File[] files){
            
            allStopWords.fillStopWordsHashMap(SearchEngine.NameOfStopListFile); //Fill the stoplist hashmap with all the stop words from file
        
            for(int f = 0; f < files.length; f++){
            
                String fileName = files[f].getName();
                document = new Document(fileName); //Create new document with the name of what the file is called
                allDocuments.add(document); //Add this new document to a list containing all the documents
                wordspassed = 0; //Reset wordspassed for every specific document
        
                try(BufferedReader file = new BufferedReader(new FileReader(fileName))){
                    
                    String lineBeingRead = "";

                    while((lineBeingRead = file.readLine()) != null){ //Read current line
                        
                        String[] currentLine = lineBeingRead.split("\\W+"); //Split the current line into separate words
                        String currentWord = currentLine[0]; //First spot is the word   
                        wordForDocument = new WordsForDocumentIndex(currentWord); //We save the word into the document since of the way the txt file was set up so we know we will only see the word once and it will be followed
                        document.words.add(wordForDocument);                      //by all of its occurrences
                        
                        for(int x = 1; x < currentLine.length; x++){ //Start at 1 because [0] is the word, every thing after it is the occurrence of that word
                            
                            //I'm able to do this because of the custom .txt files I created so the program runs from 1 minute 30 to 12 seconds, persistent data
                            wordForDocument.occurrences.add(Integer.parseInt(currentLine[x])); //Add to that words occurrence array the position the words appear
                            
                            
                            
                            //This part handles the unique words in every single document, if we haven't seen the word then we want to add the word to the hashmap
                            //and add the document that the word is in
                            if(!myMap.containsKey(currentWord) && !allStopWords.isStopWord(currentWord) && !currentWord.equals("")){
                                invertedIndexWord = new WordsForUniversalIndex(currentWord); //Create a new word which will be the value for the current word
                                myMap.put(currentWord, invertedIndexWord); //Put in the map the key being the word, for example, "hello" and then put in a hello object which contains the documents hello is in
                                invertedIndexWord.addToDocumentList(fileName); //Add to hello's object the current document we're in
                            }else if(myMap.containsKey(currentWord)){ //If the map already contains the word this means we also have an object for it
                                invertedIndexWord = myMap.get(currentWord); //Get the word
                                if(!invertedIndexWord.documentList.contains(fileName)){ //If the document list of the current word doesn't contain the file we're in then we'll add it
                                    invertedIndexWord.addToDocumentList(fileName);      //we don't want to have the same document in the list every time the word appears
                                }
                            }
                        }    
                    }
                }catch (IOException e){
                    System.out.println("File " + fileName + " not found.");  
                }       
            }
        }
        
        //This method will get the document with a specific name, this is for lookup, if I want document "q1d2.html" that will be the parameter we enter
        public Document getDocument(String name){
            for(int x = 0; x < allDocuments.size(); x++)
                if(allDocuments.get(x).documentName.equals(name)) return allDocuments.get(x); //Iterate through all the list till we find the document with the name we entered
            
            return allDocuments.get(0); //This line is useless since we will check if the document exists before we try to get it
        }
        
        //This method will check if the document with the name we're looking for exists, if it exists it will return true, if it doesn't will return false
        public boolean checkIfDocumentExists(String name){
            for(int x = 0; x < allDocuments.size(); x++)
                if(allDocuments.get(x).documentName.equals(name)) return true;
            
            return false;
        }
}