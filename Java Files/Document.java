import java.util.*;

//This class will keep track of every specific document and it will tell us what words are in the document

public class Document{
    
    public String documentName = "";
    public ArrayList<WordsForDocumentIndex> words = new ArrayList<>();
    
    public Document(String docname){ //Constructor
        documentName = docname;
    }
    
    //This method will tell us if a word is already in a document, we need to know this because instead of creating a new word object to give the word entered a new position
    //it will tell us the word is already there and then we can get that word and update that word object
    public boolean checkIfWordIsThereAlready(String wordToCheck){
        for(int x = 0; x < words.size(); x++){
            if(words.get(x).word.equals(wordToCheck)) return true;
        }
        return false;
    }
    
    //This method will return the word if it already exists for the document we're in
    public WordsForDocumentIndex getWordObject(String wordToCheck){
        for(int x = 0; x < words.size(); x++)
            if(words.get(x).word.equals(wordToCheck)) return words.get(x); //Find the word and return the object
        
        return words.get(0); //This line is useless since we will check if the word exists before we run this method
    }   
}