
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
public class Term {
    private String term;
    private HashMap<String,String> docsTf;
    private double idf;
    private int df;
    private int bigN;
    private DecimalFormat decimalFormat;
    
    private double maxtf;
    
    public Term(String word,int docsNum)
    {
        decimalFormat = new DecimalFormat("#.###");
        docsTf = new HashMap<>();
        term = word;
        df = 0;
        bigN = docsNum;

    }
    
    public Term(String termLine)
    {
        docsTf = new HashMap<>();
        
        String[] unit = termLine.split(",");
        
        term = unit[0];
        
        for(int i = 1; i<= ((unit.length/2) -1);i++)
        {
            String docName = unit[i*2 -1];
            String tf = unit[i*2];
            docsTf.put(docName,tf);
        }
        idf = Double.valueOf(unit[unit.length -1]);
        
        df = 0;
        bigN = 0;
        decimalFormat = new DecimalFormat("#.###");
    }
    
    public void addDocument(String docName,int tf)
    {
        docsTf.put(docName,setWF(tf));
        df++;
        idf = Math.log((double)bigN /df);
    }
    
    public String getTerm()
    {
        return term;
    }
    
    public String toString()
    {
        
        StringBuilder sb = new StringBuilder();
        sb.append(term + ",");
       
        Iterator iter = docsTf.entrySet().iterator();
        
        while(iter.hasNext()) 
        {
            Map.Entry entry = (Map.Entry) iter.next();
            sb.append(entry.getKey() + ",");
            sb.append(entry.getValue() + ",");
        }
        
        
        
        sb.append(decimalFormat.format(idf));
        
        return sb.toString();
    }
    
    
    private String setWF(int tf)
    {
        double wf = 1 + Math.log((double) tf);
        return decimalFormat.format(wf);
    }
    
    public double getIDF()
    {
        return idf;
    }

    public HashMap<String,String> getDocs()
    {
        return docsTf;
    }
    
    public ArrayList<String> getChampion()
    {
        ArrayList<String> champion = new ArrayList<>();
        double maxTf = 0;
        Iterator iter = docsTf.entrySet().iterator();
        while(iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            if(Double.valueOf(entry.getValue().toString()) > maxTf)
            {
                maxTf = Double.valueOf(entry.getValue().toString());
            }
        }
        
        Iterator iter2 = docsTf.entrySet().iterator();
         while(iter2.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter2.next();
            if(Double.valueOf(entry.getValue().toString()) == maxTf)
            {
                champion.add(entry.getKey().toString());
            }
        }
        
        
        return champion;
    }
    
    
}