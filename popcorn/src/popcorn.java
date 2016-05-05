
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ray
 */
public class popcorn {
   
    private String command;
    private String collectionDir;
    private String indexDir;
    private String stopwordsList;
    private int numDocs;
    private ArrayList<String> keywordList;
    
    
    public static void main(String args[])
    {   
        //Task1: Check the number of arguments
        if(Array.getLength(args) < 4)
        {
            System.out.println("Illegal Arguments, should be more than 4 arguments");
            System.exit(-1);
        }
        
        popcorn ppc = new popcorn(args);
        
    }
    
    //Constructor with arguments passed by main, handle the arguments
    public popcorn(String args[])
    {
        command = args[0];
        keywordList = new ArrayList<>();
            
        if(command.equals("index"))
        {
            if(Array.getLength(args) == 4)
            {
                collectionDir = args[1];
                indexDir = args[2];
                stopwordsList = args[3];
                
                //validate the files/Dirs
                File collection_dir = new File(collectionDir);
                File index_dir = new File(indexDir);
                File stopwords = new File(stopwordsList);
                if(!collection_dir.isDirectory())
                {
                    System.out.print(args[1] + " is not a valid directory.\nPlease give a valid collection path" );
                    System.exit(-1);
                }
                else if(!index_dir.isDirectory())
                {
                    System.out.print(args[2] +  " is not a valid directory.\nPlease give a valid indexing path");
                    System.exit(-1);
                }
                else if(!stopwords.isFile())
                {
                    System.out.print(args[3] + " can't be found as a stopword list file.\nPlease give a valid stopword list");
                    System.exit(-1);
                }
                else
                {
                    Collection docDir = new Collection(collectionDir,indexDir,stopwordsList);
                    docDir.indexing();
                    System.exit(1);
                }
            }
            else
            {
                System.out.println("Illegal Arguments! Should be other 3 arguments after index.");
                System.exit(-1);
            }
        }
        else if (command.equals("search"))
        {
            indexDir = args[1];
            File indexFile = new File(indexDir + "/index.txt");
            if(!indexFile.isFile())
            {
                System.out.println(args[1] + " is not a valid directory.\nPlease give a valid indexing path");
                System.exit(-1);
            }
            char[] numbers = args[2].toCharArray();
            for(int i = 0 ; i < numbers.length;i++)
            {
                if(numbers[i] > '9' || numbers[i] < '0')
                {
                    System.out.println(args[2] + " is not a number.\nPlease give a number indicates documents that you want to retrieval");
                    System.exit(-1);
                }
            }
            numDocs = Integer.valueOf(args[2]);
            for(int i = 3; i < Array.getLength(args);i++)
            {
                keywordList.add(args[i]);
            }   
            
            CandidateSet cs= new CandidateSet(numDocs,indexDir,keywordList);
        }         
        else if(command.equals("set"))
        {
            
            char[] numbers = args[1].toCharArray();
            for(int i = 0 ; i < numbers.length;i++)
            {
                if(numbers[i] > '9' || numbers[i] < '0')
                {
                    System.out.println(args[1] + " is not a number.\nPlease give a number.");
                    System.exit(-1);
                }
            }
            
            char[] numbers1 = args[2].toCharArray();
            for(int i = 0 ; i < numbers1.length;i++)
            {
                if(numbers1[i] > '9' || numbers1[i] < '0')
                {
                    System.out.println(args[2] + " is not a number.\nPlease give a number.");
                    System.exit(-1);
                }
            }
            
            String idfPre = args[1];        
            String termPre = args[2];
            String bool = args[3];
            String champion = args[4];
            
            if(args[3].equals("true"))
            {
                
            }
            else if(args[3].equals("false"))
            {
            
            }
            else
            {
                 System.out.println("Argument 3 " + args[3] + " is not a boolean value.\nPlease give a true or false.");
                 System.exit(-1);
            }
            
            if(args[4].equals("true") )
            {
                
            }
            else if(args[4].equals("false"))
            {
            
            }
            else
            {
                 System.out.println("Argument 4 " + args[4] + " is not a boolean value.\nPlease give a true or false.");
                 System.exit(-1);
            }
            
            try {
                File file = new File("config");
                try (PrintWriter writer = new PrintWriter(file,"UTF-8")) {

                    writer.println(idfPre);
                    writer.println(termPre);
                    writer.println(bool);
                    writer.println(champion);

                }
            }
            catch(IOException e)
            {
                System.out.println("Error when write index to file: " + e.toString());
            }  
        }
        else 
        {
            System.out.println("Illegal Arguments, unknown command");
            System.exit(-1);
        }

        
        
    }
    
   
    
}
