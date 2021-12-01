import java.awt.FlowLayout;
import java.io.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Print {
    
    //This method will print the inverted index to a file and will
    public static void printToIndexFile(BuildAllIndex index, String NameOfIndexFile){
        
        Document documentToPrint = new Document(""); //This is just a temporary document just so we can use this to print the documents information
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex(""); //This is a temporary word so we can print the words in each document
        
        try{
            
            FileWriter indexFile = new FileWriter(NameOfIndexFile); //Create a file writer so we can write to a file
            indexFile.write("There are " + index.myMap.size() + " unique words in these documents\n"); //Print how many unique words there are
            
            for(Map.Entry<String, WordsForUniversalIndex> entry : index.myMap.entrySet()){
                String key = entry.getKey();
                indexFile.write("'" + key + "' appears in " + index.myMap.get(key).documentList.size() + " documents\n"); //Prints how many documents the word appears in
                for(int x = 0; x < index.myMap.get(key).documentList.size(); x++){ //For loop for printing the information of the key
                    String document = index.myMap.get(key).documentList.get(x); //Get the document name from the documentList of the key
                    documentToPrint = index.getDocument(document); //Get the document so we can get the information for the word in a specific document
                    documentWord = documentToPrint.getWordObject(key); //Get the word from the document that contains the information in the specific document
                    indexFile.write("'" + key + "' - " + document + " (" + documentWord.occurrences.size() + ") : "); //Print what document the key appears in and in what positions in that document
                    for(int z = 0; z < documentWord.occurrences.size() - 1; z++){ //This for loop will print the positions, it will make it so the positions appears like 1, 5, 7, 8, basically with commas in between
                        indexFile.write(documentWord.occurrences.get(z) + ", ");
                    }
                    indexFile.write(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n"); //We didn't print the last term since it would leave an extra comma at the end so here we do that
                }
            }
            indexFile.close(); //Close file writer once we're done writing
        }catch(IOException e){
            System.out.println("Error has occured");
        }
    }
    
    //This method will print the Query files queries output and the output for the SearchWord and SearchDoc depending on whether or not the user decides to print to this file or not those two things
    public static void printToResultsFile(String ResultsFile, ArrayList<String> wordsToQuery, ArrayList<String> stopWords, ArrayList<String> notInAnyDocument, ArrayList<String> stemmedWordsToQuery, ArrayList<String> stemmedWordsStopWords, ArrayList<String> stemmedWordsNotInAnyDocument, ArrayList<String> stemmedWordsThatStayedTheSame, BuildAllIndex index, String SearchWord, String SearchDoc, String WriteDecision, String Stem, String Snippet){
        
        Document documentToPrint = new Document(""); //This is just a temporary document just so we can use this to print the documents information
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex(""); //This is a temporary word so we can print the words in each document
        String currentWord = ""; //This will be current word in the query file we're querying
        PrecisionRecall query;
        ArrayList<PrecisionRecall> queries;
        double retrievedDocuments = 0;
        
        queries = createPrecisionRecall();
        
        try{
            
            FileWriter results = new FileWriter(ResultsFile); //Create file writer so we can write to a file
            
            if(WriteDecision.equals("yes")){ //If the user decides they want to print SearchWord and SearchDoc to results file this will handle it
                printSearchWordToFile(results, SearchWord, index);
                printSearchDocToFile(results, SearchDoc, index);
            }else if(WriteDecision.equals("no")){ //If the say no it will just print to console
                printSearchWordToConsole(SearchWord, index);
                printSearchDocToConsole(SearchDoc, index);
            }else
                System.out.println("Write decision typed in wrong, either 'yes' or 'no'"); //If it's neither yes or no then they will be told it's wrong here
                
                
            for(int s = 0; s < stopWords.size(); s++){ //This for loop will tell the user if the query they entered was a stopword
                currentWord = stopWords.get(s);
                results.write("The query '" + currentWord + "' is a stop word.\n");
            }
            
            for(int n = 0; n < notInAnyDocument.size(); n++){ //This for loop will tell the user if the query they entered didn't exist in any file
                currentWord = notInAnyDocument.get(n);
                results.write("'" + currentWord + "' doesn't exist in any document.\n");
            }
                
            for(int x = 0; x < wordsToQuery.size(); x++){ //This for loop will handle the queries that exists
                
                currentWord = wordsToQuery.get(x); //Set current word equal to the word we're gonna query
                
                if(queriesContainWord(currentWord, queries)){ //If the query term being search is important
                    query = queriesGetWord(currentWord,queries); //Save the word
                    for(int a = 0; a < index.myMap.get(currentWord).documentList.size(); a++){ //Iterate through the words documents and see which documents are actually related to the query
                        if(query.documents.contains(index.myMap.get(currentWord).documentList.get(a))) //This solves relevant documents intersect retrieved documents
                            retrievedDocuments++; //Add to this if the document is a valid document
                    }
                    double sizeOfRetrieved = index.myMap.get(currentWord).documentList.size(); //Turning size into a double from an intt
                    double sizeOfRelevant = query.documents.size(); //Turning size into double from an int
                    double precision = retrievedDocuments / sizeOfRetrieved; //This is formula to solve precision
                    double recall = retrievedDocuments / sizeOfRelevant; //Formula to solve recall
                    results.write("The query '" + currentWord + "' has " + query.documents.size() + " relevant documents and is found in " + index.myMap.get(currentWord).documentList.size() + " documents, " + (int)retrievedDocuments + " were documents that were relevant and retrieved from the corpus");
                    results.write("\nThe precision for '" + currentWord + "' is : " + precision + "\n");  
                    results.write("The recall for '" + currentWord + "' is : " + recall + "\n");
                }    
                
                retrievedDocuments = 0;
                
                    results.write("The query '" + currentWord + "' appears in " + index.myMap.get(currentWord).documentList.size() + " documents\n"); //Print to the file the information about how many files it's in
                    for(int y = 0; y < index.myMap.get(currentWord).documentList.size(); y++){ //Iterate through the documents that the word is in
                        String document = index.myMap.get(currentWord).documentList.get(y); //Make this string equal to the name of the document that the word is in
                        documentToPrint = index.getDocument(document); //Find the document by using it's name and make the documentToPrint have all the documents information
                        documentWord = documentToPrint.getWordObject(currentWord); //This will get the word in the document and the word contains the positions and frequencies of the word in the specific document
                        results.write("'" + currentWord + "' - " + document + " (" + documentWord.occurrences.size() + ") : ");
                        for(int z = 0; z < documentWord.occurrences.size() - 1; z++){ //This for loop will make it so the positions appears like 1, 5, 7, 8, basically with commas in between
                            results.write(documentWord.occurrences.get(z) + ", ");
                        }
                        results.write(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n"); //We didn't print the last term since it would leave an extra comma at the end so here we do that
                        printSnippet(results, documentToPrint, documentWord.occurrences, currentWord, Integer.parseInt(Snippet));
                    }
            } 
            
            if(Stem.equals("yes")) //If the decision to stem was yes then we will call a method that will print the stemmed words
                printStemmedWords(results, stemmedWordsToQuery, stemmedWordsStopWords, stemmedWordsNotInAnyDocument, stemmedWordsThatStayedTheSame, index, Snippet); //This method handles the printing
            else if(!Stem.equals("no"))
                System.out.println("Stem decision was typed in wrong, either 'yes' or 'no'"); //If the decision was typed in wrong not yes or no we will tell the user

            results.close(); //Since we're done writing to the file we will close the writer
        }catch(IOException e){
            System.out.println("Error has occured");
        }
    }
    
    public static void printToGUI(ArrayList<String> wordsToQuery, ArrayList<String> stopWords, ArrayList<String> notInAnyDocument, ArrayList<String> stemmedWordsToQuery, ArrayList<String> stemmedWordsStopWords, ArrayList<String> stemmedWordsNotInAnyDocument, ArrayList<String> stemmedWordsThatStayedTheSame, BuildAllIndex index, String SearchWord, String SearchDoc, String Stem, String Snippet){
        
        final JFrame frame = new JFrame("Query Output");
        JTextArea gui = new JTextArea(60,100);
        String currentWord = "";
        Document documentToPrint;
        WordsForDocumentIndex documentWord;
        PrecisionRecall query;
        ArrayList<PrecisionRecall> queries;
        double retrievedDocuments = 0;
        
        queries = createPrecisionRecall();
        
        
        for(int s = 0; s < stopWords.size(); s++){ //This for loop will tell the user if the query they entered was a stopword
            currentWord = stopWords.get(s);
            gui.append("The query '" + currentWord + "' is a stop word.\n");
        }
            
        for(int n = 0; n < notInAnyDocument.size(); n++){ //This for loop will tell the user if the query they entered didn't exist in any file
            currentWord = notInAnyDocument.get(n);
            gui.append("'" + currentWord + "' doesn't exist in any document.\n");
        }
                
        for(int x = 0; x < wordsToQuery.size(); x++){ //This for loop will handle the queries that exists
                
            currentWord = wordsToQuery.get(x); //Set current word equal to the word we're gonna query
            
            if(queriesContainWord(currentWord, queries)){ //If the query term being search is important
                
                query = queriesGetWord(currentWord, queries); //Save the word
                
                for (int a = 0; a < index.myMap.get(currentWord).documentList.size(); a++){ //Iterate through the words documents and see which documents are actually related to the query
                    if (query.documents.contains(index.myMap.get(currentWord).documentList.get(a))){ //This solves relevant documents intersect retrieved documents
                        retrievedDocuments++; //Add to this if the document is a valid document
                    }
                }
                double sizeOfRetrieved = index.myMap.get(currentWord).documentList.size(); //Turning size into a double from an intt
                double sizeOfRelevant = query.documents.size(); //Turning size into double from an int
                double precision = retrievedDocuments / sizeOfRetrieved; //This is formula to solve precision
                double recall = retrievedDocuments / sizeOfRelevant; //Formula to solve recall
                gui.append("The query '" + currentWord + "' has " + query.documents.size() + " relevant documents and is found in " + index.myMap.get(currentWord).documentList.size() + " documents, " + (int) retrievedDocuments + " were documents that were relevant and retrieved from the corpus");
                gui.append("\nThe precision for '" + currentWord + "' is : " + precision + "\n");
                gui.append("The recall for '" + currentWord + "' is : " + recall + "\n");
            }
                
                retrievedDocuments = 0;
                    
            gui.append("The query '" + currentWord + "' appears in " + index.myMap.get(currentWord).documentList.size() + " documents\n"); //Print to the file the information about how many files it's in
            for(int y = 0; y < index.myMap.get(currentWord).documentList.size(); y++){ //Iterate through the documents that the word is in
                String document = index.myMap.get(currentWord).documentList.get(y); //Make this string equal to the name of the document that the word is in
                documentToPrint = index.getDocument(document); //Find the document by using it's name and make the documentToPrint have all the documents information
                documentWord = documentToPrint.getWordObject(currentWord); //This will get the word in the document and the word contains the positions and frequencies of the word in the specific document
                gui.append("'" + currentWord + "' - " + document + " (" + documentWord.occurrences.size() + ") : ");
                for(int z = 0; z < documentWord.occurrences.size() - 1; z++){ //This for loop will make it so the positions appears like 1, 5, 7, 8, basically with commas in between
                    gui.append(documentWord.occurrences.get(z) + ", ");
                }
                    gui.append(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n"); //We didn't print the last term since it would leave an extra comma at the end so here we do that
                    printSnippetToGUI(gui, documentToPrint, documentWord.occurrences, currentWord, Integer.parseInt(Snippet));
            }      
        } 
            
        if(Stem.equals("yes")) //If the decision to stem was yes then we will call a method that will print the stemmed words
            printStemmedWordsToGUI(gui, stemmedWordsToQuery, stemmedWordsStopWords, stemmedWordsNotInAnyDocument, stemmedWordsThatStayedTheSame, index, Snippet); //This method handles the printing
        else if(!Stem.equals("no"))
            System.out.println("Stem decision was typed in wrong, either 'yes' or 'no'"); //If the decision was typed in wrong not yes or no we will tell the user
        
        JScrollPane sp = new JScrollPane(gui);
 
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.getContentPane().add(sp);
 
        frame.setVisible(true);
    }
    
    //This will handle the SearchWords query if the write decision is yes, it's basically same code as printToResultsFile method
    public static void printSearchWordToFile(FileWriter results, String SearchWord, BuildAllIndex index){
        
        Document documentToPrint = new Document("");
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex("");
        
        try{
            
            if(index.myMap.containsKey(SearchWord)){
                    
                results.write("The SearchWord '" + SearchWord + "' appears in " + index.myMap.get(SearchWord).documentList.size() + " documents\n");
                    
                for(int x = 0; x < index.myMap.get(SearchWord).documentList.size(); x++){
                    String document = index.myMap.get(SearchWord).documentList.get(x);
                    documentToPrint = index.getDocument(document);
                    documentWord = documentToPrint.getWordObject(SearchWord);
                    results.write("'" + SearchWord + "' - " + document + " (" + documentWord.occurrences.size() + ") : ");    
                    for(int z = 0; z < documentWord.occurrences.size() - 1; z++){
                        results.write(documentWord.occurrences.get(z) + ", ");
                    }
                    results.write(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n");
                }
            }else if(index.allStopWords.isStopWord(SearchWord)){
                results.write("'" + SearchWord + "' is a stop word.\n");
            }else{
                results.write("'" + SearchWord + "' doesn't exist in any document.\n");
            }    
        }catch(IOException e){
            System.out.println("Error");
        }
    }
    
    //This will handle the SearchDoc query if the writedecision is yes, basically the same code as for printSearchWordToFile just tweaked a bit  
    public static void printSearchDocToFile(FileWriter results, String SearchDoc, BuildAllIndex index){
        
        Document documentToPrint = new Document("");
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex("");
        
        try{
            
            if(index.checkIfDocumentExists(SearchDoc)){ //First we will see if the document name entered is actually a valid document that we read
                documentToPrint = index.getDocument(SearchDoc);
            }else{
                results.write("Document : '" + SearchDoc + "' doesn't exist (Maybe add .txt at the end) \n"); //If it's not a valid document we will inform the user
                return;
            }
            
            results.write(SearchDoc + " contains " + documentToPrint.words.size() + " unique words, those words are :\n"); //If it's a valid document then we will print the information for the document
            for(int x = 0; x < documentToPrint.words.size(); x++){ //This will iterate through all the words that the document contains
                documentWord = documentToPrint.words.get(x); //Get the words object so we can have the words information
                results.write("'" + documentWord.word + "' (" + documentWord.occurrences.size() + ") : ");
                for(int z = 0; z < documentWord.occurrences.size() - 1; z++){
                    results.write(documentWord.occurrences.get(z) + ", ");
                }
                    results.write(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n");
            }
        }catch(IOException e){
            System.out.println("Error");
        }
    }
      
    //This will handle the SearchWords query if the writedecision is no, it's literally the same code as printSearchWordToFile but with System.out.print() instead of writing to the file, this will print to console
    public static void printSearchWordToConsole(String SearchWord, BuildAllIndex index){
        
        Document documentToPrint = new Document("");
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex("");
        
        if(index.myMap.containsKey(SearchWord)){
            
            System.out.print("The SearchWord '" + SearchWord + "' appears in " + index.myMap.get(SearchWord).documentList.size() + " documents\n");
            
            for(int x = 0; x < index.myMap.get(SearchWord).documentList.size(); x++){
                String document = index.myMap.get(SearchWord).documentList.get(x);
                documentToPrint = index.getDocument(document);
                documentWord = documentToPrint.getWordObject(SearchWord);
                System.out.print("'" + SearchWord + "' - " + document + " (" + documentWord.occurrences.size() + ") : ");    
                for(int z = 0; z < documentWord.occurrences.size() - 1; z++){
                    System.out.print(documentWord.occurrences.get(z) + ", ");
                }
                System.out.print(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n");
            }
        }else if(index.allStopWords.isStopWord(SearchWord)){
            System.out.print("'" + SearchWord + "' is a stop word.\n");
        }else{
            System.out.print("'" + SearchWord + "' doesn't exist in any document.\n");
        }    
    }
    
    //This will handle the SearchDoc query if the write decision is no, it's literally the same code as printSearchDocToFile but with System.out.print() instead of writing to the file, this will print to console
    public static void printSearchDocToConsole(String SearchDoc, BuildAllIndex index){
    
        Document documentToPrint = new Document("");
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex("");
        
        if(index.checkIfDocumentExists(SearchDoc)){
            documentToPrint = index.getDocument(SearchDoc);
        }else{
            System.out.print("Document : " + SearchDoc + " doesn't exist (Maybe add .html at the end)");
            return;
        }
            
        System.out.print(SearchDoc + " contains " + documentToPrint.words.size() + " unique words, those words are :\n");
        for(int x = 0; x < documentToPrint.words.size(); x++){
            documentWord = documentToPrint.words.get(x);
            System.out.print("'" + documentWord.word + "' (" + documentWord.occurrences.size() + ") : ");
            for(int z = 0; z < documentWord.occurrences.size() - 1; z++){
                System.out.print(documentWord.occurrences.get(z) + ", ");
            }
            System.out.print(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n");
        }
    }
    
     public static void printStemmedWords(FileWriter results, ArrayList<String> stemmedWordsToQuery, ArrayList<String> stemmedWordsStopWords, ArrayList<String> stemmedWordsNotInAnyDocument, ArrayList<String> stemmedWordsThatStayedTheSame, BuildAllIndex index, String Snippet){
         
        String currentWord = "";
        Document documentToPrint = new Document("");
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex("");
         
        try{
            
            for(int s = 0; s < stemmedWordsStopWords.size(); s++){ //This for loop will tell the user if the query they entered was a stopword
                currentWord = stemmedWordsStopWords.get(s);
                results.write("The stemmed word '" + currentWord + "' is a stop word.\n");
            }
            
            for(int z = 0; z < stemmedWordsThatStayedTheSame.size(); z++){ //This for loop will tell the user if the query they entered cannot be stemmed
                currentWord = stemmedWordsThatStayedTheSame.get(z);
                results.write("The stemmed word '" + currentWord + "' is the same word after being stemmed.\n");
            }
            
            for(int n = 0; n < stemmedWordsNotInAnyDocument.size(); n++){ //This for loop will tell the user if the query they entered didn't exist in any file
                currentWord = stemmedWordsNotInAnyDocument.get(n);
                results.write("The stemmed word '" + currentWord + "' doesn't exist in any document.\n");
            }
            
            for(int x = 0; x < stemmedWordsToQuery.size(); x++){ //This for loop will handle the queries that exists
                
                currentWord = stemmedWordsToQuery.get(x); //Set current word equal to the word we're gonna query
                    
                    results.write("The stemmed word '" + currentWord + "' appears in " + index.myMap.get(currentWord).documentList.size() + " documents\n"); //Print to the file the information about how many files it's in
                    for(int y = 0; y < index.myMap.get(currentWord).documentList.size(); y++){ //Iterate through the documents that the word is in
                        String document = index.myMap.get(currentWord).documentList.get(y); //Make this string equal to the name of the document that the word is in
                        documentToPrint = index.getDocument(document); //Find the document by using it's name and make the documentToPrint have all the documents information
                        documentWord = documentToPrint.getWordObject(currentWord); //This will get the word in the document and the word contains the positions and frequencies of the word in the specific document
                        results.write("'" + currentWord + "' - " + document + " (" + documentWord.occurrences.size() + ") : ");
                        for(int z = 0; z < documentWord.occurrences.size() - 1; z++){ //This for loop will make it so the positions appears like 1, 5, 7, 8, basically with commas in between
                            results.write(documentWord.occurrences.get(z) + ", ");
                        }
                        results.write(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n"); //We didn't print the last term since it would leave an extra comma at the end so here we do that
                        printSnippet(results, documentToPrint, documentWord.occurrences, currentWord, Integer.parseInt(Snippet));  
                    }      
            } 
            
        }catch(IOException e){
             System.out.println("Error");
        }
     }
     
     public static void printStemmedWordsToGUI(JTextArea gui, ArrayList<String> stemmedWordsToQuery, ArrayList<String> stemmedWordsStopWords, ArrayList<String> stemmedWordsNotInAnyDocument, ArrayList<String> stemmedWordsThatStayedTheSame, BuildAllIndex index, String Snippet){
         
        String currentWord = "";
        Document documentToPrint = new Document("");
        WordsForDocumentIndex documentWord = new WordsForDocumentIndex("");
         
        
            
            for(int s = 0; s < stemmedWordsStopWords.size(); s++){ //This for loop will tell the user if the query they entered was a stopword
                currentWord = stemmedWordsStopWords.get(s);
                gui.append("The stemmed word '" + currentWord + "' is a stop word.\n");
            }
            
            for(int z = 0; z < stemmedWordsThatStayedTheSame.size(); z++){ //This for loop will tell the user if the query they entered cannot be stemmed
                currentWord = stemmedWordsThatStayedTheSame.get(z);
                gui.append("The stemmed word '" + currentWord + "' is the same word after being stemmed.\n");
            }
            
            for(int n = 0; n < stemmedWordsNotInAnyDocument.size(); n++){ //This for loop will tell the user if the query they entered didn't exist in any file
                currentWord = stemmedWordsNotInAnyDocument.get(n);
                gui.append("The stemmed word '" + currentWord + "' doesn't exist in any document.\n");
            }
            
            for(int x = 0; x < stemmedWordsToQuery.size(); x++){ //This for loop will handle the queries that exists
                
                currentWord = stemmedWordsToQuery.get(x); //Set current word equal to the word we're gonna query
                    
                    gui.append("The stemmed word '" + currentWord + "' appears in " + index.myMap.get(currentWord).documentList.size() + " documents\n"); //Print to the file the information about how many files it's in
                    for(int y = 0; y < index.myMap.get(currentWord).documentList.size(); y++){ //Iterate through the documents that the word is in
                        String document = index.myMap.get(currentWord).documentList.get(y); //Make this string equal to the name of the document that the word is in
                        documentToPrint = index.getDocument(document); //Find the document by using it's name and make the documentToPrint have all the documents information
                        documentWord = documentToPrint.getWordObject(currentWord); //This will get the word in the document and the word contains the positions and frequencies of the word in the specific document
                        gui.append("'" + currentWord + "' - " + document + " (" + documentWord.occurrences.size() + ") : ");
                        for(int z = 0; z < documentWord.occurrences.size() - 1; z++){ //This for loop will make it so the positions appears like 1, 5, 7, 8, basically with commas in between
                            gui.append(documentWord.occurrences.get(z) + ", ");
                        }
                        gui.append(documentWord.occurrences.get(documentWord.occurrences.size() - 1) + "\n"); //We didn't print the last term since it would leave an extra comma at the end so here we do that
                        printSnippetToGUI(gui, documentToPrint, documentWord.occurrences, currentWord, Integer.parseInt(Snippet));  
                    }      
            } 
     }
     
    public static void printSnippet(FileWriter results, Document document, ArrayList<Integer> occurrences, String currentWord, int snippetLength){
         
         int currentSpot = 0; //Temporary variable to hold the current spot
         WordsForDocumentIndex wordToCheck; //Temporary variable to hold the word we're checkings occurrences array
         ArrayList<String> snippet = new ArrayList<>();  //The array that will hold the snippet

         try{
             
            for(int x = 0; x < occurrences.size(); x++){ //Save the word we're at
                currentSpot = occurrences.get(x);        //This is the words position
                        
                        for(int y = snippetLength; y > 0; y--) //Find the words before the word we're at
                            snippet.add(getSnippetBefore(currentSpot, document, y)); //This method will find the words
                        
                        snippet.add(currentWord); //Add the word we're looking for in the middle
                        
                        for(int z = 1; z <= snippetLength; z++) //Find the words after the word we're at
                            snippet.add(getSnippetAfter(currentSpot, document, z)); //This method will find the words
                        
                results.write("The word '" + currentWord + "' in " + document.documentName + " at position " + currentSpot + " has a snippet : "); //Print to results the snippet
                for(int a = 0; a < snippet.size(); a++)
                    if(!snippet.get(a).equals("")) //If the snippet has a blank skip it since that means the word before the current spot was a stop word
                        results.write(snippet.get(a) + " "); //Write the word if the word is valid
                results.write("\n"); //Skip a line
                snippet.clear(); //Clear snippet since we have to go back to another occurrence for the word
            }
         }
         catch(IOException e){
             System.out.println("Error");
        }

    }
    
    public static void printSnippetToGUI(JTextArea gui, Document document, ArrayList<Integer> occurrences, String currentWord, int snippetLength){
         
        int currentSpot = 0; //Temporary variable to hold the current spot
        WordsForDocumentIndex wordToCheck; //Temporary variable to hold the word we're checkings occurrences array
        ArrayList<String> snippet = new ArrayList<>();  //The array that will hold the snippet

        for(int x = 0; x < occurrences.size(); x++){ //Save the word we're at
            currentSpot = occurrences.get(x);        //This is the words position
                        
            for(int y = snippetLength; y > 0; y--) //Find the words before the word we're at
                snippet.add(getSnippetBefore(currentSpot, document, y)); //This method will find the words
                        
            snippet.add(currentWord); //Add the word we're looking for in the middle
                        
            for(int z = 1; z <= snippetLength; z++) //Find the words after the word we're at
                snippet.add(getSnippetAfter(currentSpot, document, z)); //This method will find the words
                        
            gui.append("The word '" + currentWord + "' in " + document.documentName + " at position " + currentSpot + " has a snippet : "); //Print to results the snippet
            for(int a = 0; a < snippet.size(); a++)
                if(!snippet.get(a).equals("")) //If the snippet has a blank skip it since that means the word before the current spot was a stop word
                    gui.append(snippet.get(a) + " "); //Write the word if the word is valid
            gui.append("\n"); //Skip a line
            snippet.clear(); //Clear snippet since we have to go back to another occurrence for the word
        }
    }
    
    
    public static String getSnippetBefore(int currentSpot, Document document, int snippetLength){
       
        WordsForDocumentIndex wordToCheck;
        
        for(int y = 0; y < document.words.size(); y++){ //Run through all the words in the document
            wordToCheck = document.words.get(y); //Save the current word we're at
            for(int z = 0; z < wordToCheck.occurrences.size(); z++){ //Run through the current words occurrences list since we want to find the position before the current position
                        
                if(wordToCheck.occurrences.get(z) == currentSpot - snippetLength){ //If the word we're at has an occurrence at the spot of currentspot - snippet length, return that word
                    return wordToCheck.word;
                }          
            }    
        }
       
        return ""; //If there is no word before the word at a certain length return the empty string, this just means the word was a stopword
    }
    
    public static String getSnippetAfter(int currentSpot, Document document, int snippetLength){
       
        WordsForDocumentIndex wordToCheck;
        
        for(int y = 0; y < document.words.size(); y++){ //Run through all the words in the document
            wordToCheck = document.words.get(y); //Save the current word we're at
            for(int z = 0; z < wordToCheck.occurrences.size(); z++){ //Run through the current words occurrences list since we want to find the position before the current position
                        
                if(wordToCheck.occurrences.get(z) == currentSpot + snippetLength){ //If the word we're at has an occurrence at the spot of currentspot + snippet length, return that word
                    return wordToCheck.word;
                }          
            }    
        }
       
        return ""; //If there is no word before the word at a certain length return the empty string, this just means the word was a stopword
    }
    
    public static ArrayList<PrecisionRecall> createPrecisionRecall(){
        
        ArrayList<PrecisionRecall> queries = new ArrayList<>();
        String[] q1 = {"q1d1.txt", "q1d2.txt", "q1d3.txt", "q1d4.txt", "q1d5.txt", "q1d6.txt", "q1d7.txt", "q1d8.txt", "q1d9.txt", "q1d10.txt", "q1d11.txt", "q1d12.txt", "q1d13.txt", "q1d14.txt", "q1d15.txt", "q1d16.txt", "q1d17.txt", "q1d18.txt", "q1d19.txt", "q1d20.txt"};  
        String[] q2 = {"q2d1.txt", "q2d2.txt", "q2d3.txt", "q2d4.txt", "q2d5.txt", "q2d6.txt", "q2d7.txt", "q2d8.txt", "q2d9.txt", "q2d10.txt", "q2d11.txt", "q2d12.txt", "q2d13.txt", "q2d14.txt", "q2d15.txt", "q2d16.txt", "q2d17.txt", "q2d18.txt", "q2d19.txt", "q2d20.txt"};
        String[] q3 = {"q3d1.txt", "q3d2.txt", "q3d3.txt", "q3d4.txt", "q3d5.txt", "q3d6.txt", "q3d7.txt", "q3d8.txt", "q3d9.txt", "q3d10.txt", "q3d11.txt", "q3d12.txt", "q3d13.txt", "q3d14.txt", "q3d15.txt", "q3d16.txt", "q3d17.txt", "q3d18.txt", "q3d19.txt", "q3d20.txt"};
        String[] q4 = {"q4d1.txt", "q4d2.txt", "q4d3.txt", "q4d4.txt", "q4d5.txt", "q4d6.txt", "q4d7.txt", "q4d8.txt", "q4d9.txt", "q4d10.txt", "q4d11.txt", "q4d12.txt", "q4d13.txt", "q4d14.txt", "q4d15.txt", "q4d16.txt", "q4d17.txt", "q4d18.txt", "q4d19.txt", "q4d20.txt"};
        String[] q5 = {"q5d1.txt", "q5d2.txt", "q5d3.txt", "q5d4.txt", "q5d5.txt", "q5d6.txt", "q5d7.txt", "q5d8.txt", "q5d9.txt", "q5d10.txt", "q5d11.txt", "q5d12.txt", "q5d13.txt", "q5d14.txt", "q5d15.txt", "q5d16.txt", "q5d17.txt", "q5d18.txt", "q5d19.txt", "q5d20.txt"};
        String[] q6 = {"q6d1.txt", "q6d2.txt", "q6d3.txt", "q6d4.txt", "q6d5.txt", "q6d6.txt", "q6d7.txt", "q6d8.txt", "q6d9.txt", "q6d10.txt", "q6d11.txt", "q6d12.txt", "q6d13.txt", "q6d14.txt", "q6d15.txt", "q6d16.txt", "q6d17.txt", "q6d18.txt", "q6d19.txt", "q6d20.txt"};
        String[] q7 = {"q7d1.txt", "q7d2.txt", "q7d3.txt", "q7d4.txt", "q7d5.txt", "q7d6.txt", "q7d7.txt", "q7d8.txt", "q7d9.txt", "q7d10.txt", "q7d11.txt", "q7d12.txt", "q7d13.txt", "q7d14.txt", "q7d15.txt", "q7d16.txt", "q7d17.txt", "q7d18.txt", "q7d19.txt", "q7d20.txt"};
        String[] q8 = {"q8d1.txt", "q8d2.txt", "q8d3.txt", "q8d4.txt", "q8d5.txt", "q8d6.txt", "q8d7.txt", "q8d8.txt", "q8d9.txt", "q8d10.txt", "q8d11.txt", "q8d12.txt", "q8d13.txt", "q8d14.txt", "q8d15.txt", "q8d16.txt", "q8d17.txt", "q8d18.txt", "q8d19.txt", "q8d20.txt"};
        String[] q9 = {"q9d1.txt", "q9d2.txt", "q9d3.txt", "q9d4.txt", "q9d5.txt", "q9d6.txt", "q9d7.txt", "q9d8.txt", "q9d9.txt", "q9d10.txt", "q9d11.txt", "q9d12.txt", "q9d13.txt", "q9d14.txt", "q9d15.txt", "q9d16.txt", "q9d17.txt", "q9d18.txt", "q9d19.txt", "q9d20.txt"};
        String[] q10 = {"q10d1.txt", "q10d2.txt", "q10d3.txt", "q10d4.txt", "q10d5.txt", "q10d6.txt", "q10d7.txt", "q10d8.txt", "q10d9.txt", "q10d10.txt", "q10d11.txt", "q10d12.txt", "q10d13.txt", "q10d14.txt", "q10d15.txt", "q10d16.txt", "q10d17.txt", "q10d18.txt", "q10d19.txt", "q10d20.txt"};
        
        PrecisionRecall quarter = new PrecisionRecall("quarter");
        for(int x = 0; x < q1.length; x++)
            quarter.documents.add(q1[x]);
       
        queries.add(quarter);
        
        PrecisionRecall microsoft = new PrecisionRecall("microsoft");
        for(int x = 0; x < q2.length; x++)
            microsoft.documents.add(q2[x]);
        
        queries.add(microsoft);
        
        PrecisionRecall debt = new PrecisionRecall("debt");
        for(int x = 0; x < q3.length; x++)
            debt.documents.add(q3[x]);
        
        queries.add(debt);
        
        PrecisionRecall america = new PrecisionRecall("america");
        for(int x = 0; x < q3.length; x++)
           america.documents.add(q3[x]);
        for(int x = 0; x < q7.length; x++)
           america.documents.add(q7[x]);
        for(int x = 0; x < q10.length; x++)
           america.documents.add(q10[x]);
        
        queries.add(america);
        
        PrecisionRecall york = new PrecisionRecall("york");
        for(int x = 0; x < q4.length; x++)
            york.documents.add(q4[x]);
        
        queries.add(york);
        
        PrecisionRecall city = new PrecisionRecall("city");
        for(int x = 0; x < q4.length; x++)
            city.documents.add(q4[x]);
        for(int x = 0; x < q6.length; x++)
            city.documents.add(q6[x]);
        
        queries.add(city);
        
        PrecisionRecall internet = new PrecisionRecall("internet");
        for(int x = 0; x < q5.length; x++)
            internet.documents.add(q5[x]);
        
        queries.add(internet);
        
        PrecisionRecall dollar = new PrecisionRecall("dollar");
        for(int x = 0; x < q8.length; x++)
            dollar.documents.add(q8[x]);
        for(int x = 0; x < q9.length; x++)
            dollar.documents.add(q9[x]);
        
        queries.add(dollar);
        
        PrecisionRecall president = new PrecisionRecall("president");
        for(int x = 0; x < q10.length; x++)
            president.documents.add(q10[x]);
        
        queries.add(president);
        
        return queries;
    }
    
    public static boolean queriesContainWord(String word, ArrayList<PrecisionRecall> queries){
        
        for(int x = 0; x < queries.size(); x++){
            if(word.equals(queries.get(x).query))
                return true;
        }
        
        return false;    
    }
    
    public static PrecisionRecall queriesGetWord(String word, ArrayList<PrecisionRecall> queries){
        
        for(int x = 0; x < queries.size(); x++){
            if(word.equals(queries.get(x).query))
                return queries.get(x);
        }
        
        return queries.get(0);
    }
}