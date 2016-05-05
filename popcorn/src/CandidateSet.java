
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
public class CandidateSet {
    Configuration config;
    private int topN;
    //set the minimum idf. lower idf terms will be ignored
    private double minIDF;
    //consider the documents with high proportion of terms.
    private double docProportion;
    //champion list for documents with higer idf
    private ArrayList<String> championList;
    private TreeMap<String,Term> termList;
    
    private String indexDirectory;
    private Tokenizer tokenizer;
    
    private ArrayList<String> keywordsList;
    
    private DecimalFormat decimalFormat;
    private boolean implcitFeedback;
    private boolean championListSwitch;

    private TreeMap<String,Document> documentList;
    private Document query;
    private HashMap<String,Double> queryTermList;
    
    public CandidateSet(int returnNumber,String indexLocation,ArrayList<String> keywords)
    {
        config = new Configuration();
        decimalFormat = new DecimalFormat("#.###");
        topN = returnNumber;
        documentList = new TreeMap<>();
        //if more 60% documents contains the term, ignore the term
        minIDF = config.getMinIdf();//Math.log(1/0.6);
        System.out.println("min idf: " + minIDF);
        //set term proportion of documents as 20%
        docProportion = config.getProportion();
        System.out.println("Doc Proportion: " + docProportion);
        //feedback
        implcitFeedback = config.getImplicitFeedbade();
        championListSwitch = config.getChampionSwitch();
        
       
        //Champion List
        championList = new ArrayList<>();
        
        indexDirectory = indexLocation;
        
        tokenizer = new Tokenizer("stopwords.txt");

        keywordsList = tokenizer.getTerms(keywords);
        queryTermList = indexQuery();

        termList = new TreeMap<>();
        query = new Document("QUERY");
        //readTerms will generate the Document List and query
        readTerms(indexDirectory + "/index.txt");     
        //Test
       
        
        TreeMap tempList = documentList;
        ArrayList<String> toRemove = new ArrayList();
        Iterator iter = tempList.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Document temp = (Document) entry.getValue();
            
           
            temp.setScore(calculateScore(query,temp));
            documentList.put(entry.getKey().toString(),temp);
            if(temp.proportion(keywordsList) < docProportion)
            {
                toRemove.add(temp.getName());
            }
        }
        
        for(int i = 0;i<toRemove.size();i++)
        {
            documentList.remove(toRemove.get(i));
        }

        ranking();
    }

    private void readTerms(String filename)
    {
        int totalTerms = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String termLine = br.readLine();
                while (termLine  != null) {
                    Term term = new Term(termLine);
                    
                    if(term.getIDF() > minIDF)
                    {
                        
                        //generate the Document query
                        if(queryTermList.containsKey(term.getTerm()))
                        {
                            query.addTerm(term.getTerm(),getWeight(queryTermList.get(term.getTerm()),term.getIDF()));
                        }
                        termList.put(term.getTerm(),term);
                        
                        //get Champion List
                        
                        for(String champion : term.getChampion())
                        {
                            
                            if(championListSwitch)
                                if(!championList.contains(champion))
                                {
                                    championList.add(champion);
                                }
                        }
                        
                        Iterator iter = term.getDocs().entrySet().iterator();

                        while(iter.hasNext())
                        {
                            Map.Entry entry = (Map.Entry) iter.next();           

                            if(documentList.containsKey(entry.getKey().toString()))
                            {
                                Document doc = documentList.get(entry.getKey().toString());
                                doc.addTerm(term.getTerm(),getWeight(entry.getValue().toString(),term.getIDF()));
                                if(term.getTerm().equals("cat"))
                              
                                documentList.put(doc.getName(),doc);

                               
                            }
                            else
                            {
                                Document doc = new Document(entry.getKey().toString());
                                doc.addTerm(term.getTerm(),getWeight(entry.getValue().toString(),term.getIDF()));
                                if(championListSwitch)
                                {

                                    if (championList.contains(doc.getName()))
                                    {
                                        documentList.put(doc.getName(),doc);
                                        
                                    }       
                                }
                                else
                                {
                                        documentList.put(doc.getName(),doc);
                                }
                                        
                            }
                        }
 
                    }
                    totalTerms ++;
                    termLine = br.readLine();
                }               
        }
        catch(IOException e) {
            System.out.println("IO Exception : " + e);
        }
        
      
        
    }
    
    private double getWeight(String tf, double idf)
    {
        
        return Double.parseDouble(decimalFormat.format(Double.parseDouble(tf)*idf));
    }
    
    private double getWeight(double tf,double idf)
    {
        return Double.parseDouble(decimalFormat.format(tf*idf));
    }
    
    private HashMap<String,Double> indexQuery()
    {
        
        HashMap<String,Integer> queryMap = new HashMap<>();
        for(int i = 0; i < keywordsList.size();i++)
        {
            if(queryMap.containsKey(keywordsList.get(i)))
            {
                queryMap.put(keywordsList.get(i),queryMap.get(keywordsList.get(i)) + 1);
            }
            else
            {
                queryMap.put(keywordsList.get(i),1);
            }
        }
        
        HashMap<String,Double> queryMapWithTf = new HashMap<>();
        Iterator iter = queryMap.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            queryMapWithTf.put(entry.getKey().toString(),Double.parseDouble((decimalFormat.format((double) 1 + Math.log((int)entry.getValue())))));
        }
        return queryMapWithTf;
    }

    
    private double calculateScore(Document queryD,Document target)
    {
        double numerator = 0.0;
        double denominator =0.0;
        Iterator iter = queryD.getTerms().entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            if(target.hasTerm(entry.getKey().toString()))
            {
                numerator += Double.parseDouble(entry.getValue().toString()) * target.getTermWeight(entry.getKey().toString());
            }
        }
        
        denominator = queryD.getEuclideanLength() * target.getEuclideanLength();
        return Double.parseDouble(decimalFormat.format(numerator/denominator));
    }
    
    private void ranking()
    {
        int topK = 0;
        LinkedHashMap<String,Document> rawRanked = new LinkedHashMap<>();

        TreeMap<String,Document> tempList = new TreeMap<>();
       
        Iterator iterx = documentList.entrySet().iterator();
        while(iterx.hasNext())
        {
            Map.Entry entry = (Map.Entry) iterx.next();
            tempList.put(entry.getKey().toString(),(Document)entry.getValue());
        }

        
        if(documentList.size() < topN)
            topK = documentList.size();
        else
            topK = topN;
        
        for(int i = 0; i < topK; i++)
        {
            double scoreMax = 0.0;
            String docScoreMax="";
            Iterator iter = tempList.entrySet().iterator();
            while(iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
             
                if(((Document)entry.getValue()).getScore() > scoreMax)
                {
                    scoreMax = ((Document)entry.getValue()).getScore();
                    docScoreMax = entry.getKey().toString();
                }
            }
            tempList.remove(docScoreMax);
            if(documentList.get(docScoreMax) != null)
                rawRanked.put(docScoreMax,documentList.get(docScoreMax));
           
        }        
        
        if(implcitFeedback)
        {
            LinkedHashMap<String,Document> reScored = new LinkedHashMap<>();
           
            Document newQuery = getNewQuery(rawRanked);
 
            Iterator iterl = documentList.entrySet().iterator();
            while(iterl.hasNext())
            {
                Map.Entry entry = (Map.Entry) iterl.next();
                Document temp = (Document) entry.getValue();
                temp.setScore(calculateScore(newQuery,temp));
                
                reScored.put(temp.getName(),temp); 
            }

            int rank = 1;
            for(int i = 0; i < topK; i++)
            {
                double scoreMax = 0.0;
                String docScoreMax="";
                Iterator iter = reScored.entrySet().iterator();
                while(iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry) iter.next();

                    if(((Document)entry.getValue()).getScore() > scoreMax)
                    {
                        scoreMax = ((Document)entry.getValue()).getScore();
                        docScoreMax = entry.getKey().toString();
                    }
      
                }
                
                System.out.println("Rank " + rank++ + ": " + docScoreMax + " Score: " + scoreMax);
                reScored.remove(docScoreMax);
            }
        }
        else
        {
            Iterator iter = rawRanked.entrySet().iterator();
        
            int i = 1;
            while(iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                System.out.println("Rank " + i++ + ": " + entry.getKey().toString() + " Score: " + ((Document)entry.getValue()).getScore());
            }
        }
    }
    
    
    private Document getNewQuery(LinkedHashMap<String,Document> list)
    {
        //calculate the centroid of the TopK relevence document
        
        Document newQuery = new Document("centriod");
        TreeMap<String,Double> terms = new TreeMap<>();
        Iterator iter = list.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            TreeMap<String,Double> termTemp = ((Document)entry.getValue()).getTerms();
            
            Iterator iter1 = termTemp.entrySet().iterator();
            while(iter1.hasNext())
            {
                Map.Entry entry1 = (Map.Entry) iter1.next();
                String t = entry1.getKey().toString();
                double w = (double) entry1.getValue();
                if(terms.containsKey(t))
                {
                    terms.put(t,(double)terms.get(t) + w);
                }
                else
                {
                    terms.put(t,w);
                }
            }
        }
        Iterator iter2 = query.getTerms().entrySet().iterator();
        while(iter2.hasNext())
        {
            Map.Entry entry2 = (Map.Entry) iter2.next();
            String t = entry2.getKey().toString();
            double w = (double) entry2.getValue();
            if(terms.containsKey(t))
            {
                newQuery.addTerm(t,w/list.size() + query.getTermWeight(t));
            }
            else
            {
                newQuery.addTerm(t,w/list.size());
            }
        }
        return newQuery;    
    }

}