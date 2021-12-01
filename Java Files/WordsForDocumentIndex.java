import java.util.*;

//This class keeps track of what positions a specific word in a document is at, so if we're in document "q2.d6" and hello appears 6 times
//we will keep track of the position of where the word appears in occurrences and if we want the frequency we'll just get the size of occurrences

public class WordsForDocumentIndex{
    
    public String word = "";
    public ArrayList<Integer> occurrences = new ArrayList<>();
    
    public WordsForDocumentIndex(String someWord){ //Constructor
        word = someWord;
    }
}