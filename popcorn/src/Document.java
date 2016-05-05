
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
public class Document {
    private TreeMap<String,Double> terms;
    private String name;
    private DecimalFormat decimalFormat;
    private Double rankScore;
    
    public Document(String docName)
    {
        rankScore = 0.0;
        decimalFormat = new DecimalFormat("#.###");
        terms = new TreeMap<>();
        name = docName;
    }
    
    public void addTerm(String termName, double weight)
    {
        terms.put(termName,weight);
    }
    
    public boolean hasTerm(String termName)
    {
        if(terms.containsKey(termName))
            return true;
        else
            return false;
    }
    
    public Double getTermWeight(String termName)
    {
        return terms.get(termName);
    }
    
    public TreeMap<String,Double> getTerms()
    {
        return terms;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getTermNumber()
    {
        return terms.size();
    }
    
    public double proportion(ArrayList<String> keywords)
    {
      
        int contentAmount = 0;
        for (int i = 0; i < keywords.size();i++)
        {
            if(terms.containsKey(keywords.get(i)))
                    contentAmount ++;
        }     
 
        return (double)contentAmount/keywords.size();
       
    }
    
    public void print()
    {
        System.out.println("Doc Name: " + name);
        Iterator iter = terms.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            System.out.print("Term: " + entry.getKey().toString() + " Weight: " + entry.getValue().toString() + "\n"); 
        }
        System.out.println("EuclideanLength: " + getEuclideanLength() + "\n");
    }
    
    public Double getEuclideanLength()
    {
        double unSquared = 0;
        Iterator iter = terms.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            unSquared += Double.parseDouble(entry.getValue().toString()) * Double.parseDouble(entry.getValue().toString()) ;
        }
        return Double.parseDouble(decimalFormat.format(Math.sqrt(unSquared)));
    }
    
    public void setScore(double score)
    {
        rankScore = score;
    }
    
    public double getScore()
    {
        return rankScore;
    }
    
}
