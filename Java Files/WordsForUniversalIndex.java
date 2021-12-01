import java.util.*;

//This class is for words that will be in our hashmap with our inverted index, it will tell us the specific word and which documents this specific word is in

public class WordsForUniversalIndex {
    
    public String word = "";
    public ArrayList<String> documentList = new ArrayList<>();
    
    public WordsForUniversalIndex(String nameOfWord){ //Constructor
        word = nameOfWord;
    }
    
    public void addToDocumentList(String DocumentName){ //Add a document to the list of documents for that word
        documentList.add(DocumentName);
    }
}