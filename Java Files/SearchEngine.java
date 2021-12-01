import java.io.*;
import java.util.*;

public class SearchEngine {

    public static String PathOfDir = "";
    public static String NameOfIndexFile = "";
    public static String NameOfStopListFile = "";
    public static String QueryFile = "";
    public static String ResultsFile = "";
    public static String SearchWord = "";
    public static String SearchDoc = "";
    public static String WriteDecision = "";
    public static String Stem = "";
    public static String Snippet = "";
    public static String Output = "";
    
    public static void main(String[] args){
        
        ParseCommandLine parse = new ParseCommandLine();
        parse.parseCmdLine(args);
        
        //Check if CommandLineArgs are correct or not
        if(args[0].equals("-h")){
            parse.printManual();
            return;
        }
        if(parse.checkIfMissing(PathOfDir, NameOfIndexFile, NameOfStopListFile, QueryFile, ResultsFile, SearchWord, SearchDoc, WriteDecision, Stem, Snippet, Output))
            return;
        
        //If CommandLineArgs are correct then use the info
        File directoryPath = new File(PathOfDir);
        File filesList[] = directoryPath.listFiles();
        
        //BuildAllIndex will read the files and create the inverted index for all the documents and for every document in particular
        BuildAllIndex createIndex = new BuildAllIndex();
        createIndex.buildIndex(filesList);
        
        Print.printToIndexFile(createIndex, NameOfIndexFile); //Print the inverted index
        ReadQueryFile.readQuery(QueryFile, ResultsFile, createIndex, SearchWord, SearchDoc, WriteDecision, Stem, Snippet, Output); //Print the answers to the queries in the query file
        
    }
}  