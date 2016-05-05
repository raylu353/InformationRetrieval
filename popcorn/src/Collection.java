
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ray
 */
public class Collection {
    
    private String path;
    private File folder;
    private File[] documentList;
    private Tokenizer tokenizer;
    private TreeMap<String,Term> termList;
    private HashMap<String,Double> termListForSort;
    private int bigN;
    private LinkedHashMap<String,Double> sortedTermList;
    
    private String indexDir;

    
    
    public Collection(String collection_dir,String index_dir,String stopwords)
    {
        folder = new File(collection_dir);
        documentList = folder.listFiles();
        sortedTermList = new LinkedHashMap<String,Double>();
        termListForSort = new HashMap<>();
        
        tokenizer = new Tokenizer(stopwords);
        termList = new TreeMap<>();
        bigN = documentList.length;
 
        indexDir = index_dir;
        
    }
    
    public void indexing()
    {
        for(int i = 1;i <= bigN;i++)
        {
            TreeMap<String,Integer> index = tokenizer.getTerms(documentList[i-1].getPath());
            
            //raw index file to term list
            Iterator iter = index.entrySet().iterator();
            while(iter.hasNext()) 
            {
                Map.Entry entry = (Map.Entry) iter.next();
                
                
                if(termList.containsKey(entry.getKey().toString()))
                {
                    Term tempTerm = termList.get(entry.getKey());
                    tempTerm.addDocument(documentList[i-1].getName(),(int) entry.getValue() );
                    termList.put(entry.getKey().toString(),tempTerm);
                    termListForSort.put(tempTerm.getTerm(),tempTerm.getIDF());
                }
                else
                {
                    Term tempTerm = new Term(entry.getKey().toString(),bigN);
                    tempTerm.addDocument(documentList[i-1].getName(),(int) entry.getValue());
                    termList.put(entry.getKey().toString(), tempTerm);
                    termListForSort.put(tempTerm.getTerm(),tempTerm.getIDF());
                }
            }
        }

        System.out.println("Totally, there are " + bigN + " files tokenized.\n And there are " + termList.size() + " terms indexed." );
        //Heuristic 1, index the terms by idf descent order.
        sort();
        writeFile();
        
        
        
    }
    
    public void writeFile()
    {
        
        try {
            File file = new File(indexDir + "/index.txt");
            System.out.println("Index saved :" + file.getPath());
            try (PrintWriter writer = new PrintWriter(file,"UTF-8")) {
                Iterator iter = sortedTermList.entrySet().iterator();
                while(iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Term temp = termList.get(entry.getKey().toString());
                    writer.println(temp.toString());
                }
           }
        }
       catch(Exception e)
       {
           System.out.println("Error when write index to file: " + e.toString());
       }
       
    }
    
    private void sort()
    {
        
        int count = termListForSort.size();
        for (int i = 0; i < count;i++)
        {
            double max = 0;
            String term = "";
            Iterator iter = termListForSort.entrySet().iterator();
            while(iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                if ((double)entry.getValue() >= max)
                {    max = (double)entry.getValue();
                    
                    term = entry.getKey().toString();
                }
            }
            sortedTermList.put(term, termListForSort.get(term));
            
            termListForSort.remove(term);
        }
    }
}
    
    
    
    
    
    

