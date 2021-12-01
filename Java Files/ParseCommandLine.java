public class ParseCommandLine {
  
    //This function just parses the command line and gives each variable its value given in the command line
    public static void parseCmdLine(String[] args){
        
        int empty = 0;
        String arg = "";
        
        if(empty == args.length){
            System.out.println("Args is empty");
            return;
        }
        
        for(int x = 0; x < args.length; x++){
                
            arg = args[x++]; 
            
            if(arg.equals("-CorpusDir")){ 
                SearchEngine.PathOfDir = args[x];
            }else if(arg.equals("-InvertedIndex")){
                SearchEngine.NameOfIndexFile = args[x];
            }else if(arg.equals("-StopList")){
                SearchEngine.NameOfStopListFile = args[x];
            }else if(arg.equals("-Queries")){
                SearchEngine.QueryFile = args[x];
            }else if(arg.equals("-Results")){
                SearchEngine.ResultsFile = args[x];
            }else if(arg.equals("-SearchWord")){
                SearchEngine.SearchWord = args[x];
            }else if(arg.equals("-SearchDoc")){
                SearchEngine.SearchDoc = args[x];
            }else if(arg.equals("-WriteDecision")){
                SearchEngine.WriteDecision = args[x];
            }else if(arg.equals("-Stem")){
                SearchEngine.Stem = args[x];
            }else if(arg.equals("-Snippet")){
                SearchEngine.Snippet = args[x];
            }else if(arg.equals("-Output")){
                SearchEngine.Output = args[x];
            }      
        }
    }
    
    //This function will print the manual if -h is entered
    public static void printManual(){
        System.out.println("Usage of Search Engine :\n-CorpusDir <PathOfDir> -InvertedIndex <NameOfIndexFile> -CorpusDir <NameOfStopListFile> -Queries <QueryFile> -Results <ResultsFile -SearchWord <Word> -SearchDoc <DocName> -WriteDecision <yes/no> -Stem <yes/no> -Snippet <Integer Amount> -Output <gui/file/both>");        
    }
    
    //If some part of the command line is missing, it will tell you which part and it will not allow the program to run until the command line is correct
    public static boolean checkIfMissing(String PathOfDir, String NameOfIndexFile, String NameOfStopListFile, String QueryFile, String ResultsFile, String SearchWord, String SearchDoc, String WriteDecision, String Stem, String Snippet, String Output){
        
        if(PathOfDir.equals("")){
            System.out.println("Missing -CorpusDir <PathOfDir>");
        }
        
        if(NameOfIndexFile.equals("")){
            System.out.println("Missing -InvertedIndex <NameOfIndexFile>");
        }
        
        if(NameOfStopListFile.equals("")){
            System.out.println("Missing -StopList <NameOfStopListFile>");
        }
        
        if(QueryFile.equals("")){
            System.out.println("Missing -Queries <QueryFile>");
        }
        
        if(ResultsFile.equals("")){
            System.out.println("Missing -Results <ResultsFile>");
        }
        
        if(SearchWord.equals("")){
            System.out.println("Missing -SearchWord <Word>");
        }
        
        if(SearchDoc.equals("")){
            System.out.println("Missing -SearchDoc <DocName>");
        }
        
        if(WriteDecision.equals("")){
            System.out.println("Missing -WriteDecision <yes/no>");
        }
    
        if(Stem.equals("")){
            System.out.println("Missing -Stem <yes/no>");
        }
        
        if(Snippet.equals("")){
            System.out.println("Missing -Snippet <integer value>");
        }else if(Integer.parseInt(Snippet) <= 0){
            System.out.println("Invalid Snippet value");
        }
        
        if(Output.equals("")){
            System.out.println("Missing -Output <gui/file/both>");
        }
        
        if(PathOfDir.equals("") || NameOfIndexFile.equals("") || NameOfStopListFile.equals("") || QueryFile.equals("") || ResultsFile.equals("") || SearchWord.equals("") || SearchDoc.equals("") || WriteDecision.equals("") || Stem.equals("") || Snippet.equals("") || Integer.parseInt(Snippet) <= 0 || Output.equals(""))
            return true;
        else
            return false;
    }     
}