
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeMap;
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
public class Tokenizer {
    
    private final ArrayList<String> stopwords;
    
    //Charsets for comparing
    private final ArrayList<String> capitalLetter = new ArrayList<>();
    private final ArrayList<String> lowercaseLetter = new ArrayList<>();
    private final ArrayList<String> delimiter = new ArrayList<>();
    private final ArrayList<String> potentialDilimiter = new ArrayList<>();
    private final ArrayList<String> numberAndOthers = new ArrayList<>();
    
    private String stopwordLocation;
    
    
    public Tokenizer(String stopwordFile)      
    {
        stopwordLocation = stopwordFile;
        stopwords = getStopwordList();
        for(char i = 'A'; i <= 'Z'; i++)
        {
            capitalLetter.add(String.valueOf(i));
        }
        
        for(char i = 'a';i <='z';i++)
        {
            lowercaseLetter.add(String.valueOf(i));
        }
        
        for(char i='0'; i<='9';i++)
        {
            numberAndOthers.add(String.valueOf(i));
        }
        numberAndOthers.add("@");
        numberAndOthers.add("'");
         
        delimiter.add("{");
        delimiter.add("}");
        delimiter.add("(");
        delimiter.add(")");
        delimiter.add(":");
        delimiter.add(";");
        delimiter.add("?");
        delimiter.add("!");
        delimiter.add("&");
        delimiter.add("[");
        delimiter.add("]");
        delimiter.add("<");
        delimiter.add(">");
        delimiter.add(",");
        delimiter.add("\"");
        delimiter.add("\n");
        delimiter.add("/");
        
        
        potentialDilimiter.add(".");
        potentialDilimiter.add("-");
        potentialDilimiter.add(" ");
    }
    
    /**
     * Tokenize a string (file content), keep the tokens into a ArrayList.
     * 
     * @param fileContent 
     * 
     * @return tokenized terms list
     */
    public ArrayList<String> tokenize(String fileContent)
    {
        ArrayList<String> terms = new ArrayList<>();
        StringBuilder tokenBuilder = new StringBuilder();

        
        char[] content = fileContent.toCharArray();
        for(int i = 0; i< content.length;i++)
        {
            //Belongs to normal characters, letters, numbers
            if (capitalLetter.contains(String.valueOf(content[i])) || lowercaseLetter.contains(String.valueOf(content[i])) || numberAndOthers.contains(String.valueOf(content[i])))
            {
                tokenBuilder.append(content[i]);
            }
            //belongs to delimiter, will token the term
            else if(delimiter.contains(String.valueOf(content[i])))
            {
                if(tokenBuilder.length() != 0 )
                {
                    if(tokenBuilder.toString().toLowerCase().trim().length()!=0) {
                        terms.add(tokenBuilder.toString().toLowerCase().trim());
                        tokenBuilder = new StringBuilder();
                    }
                }
            }
            //Belongs to potentialdilimiters, will consider various situation
            //Assignment 7.d
            else if(potentialDilimiter.contains(String.valueOf(content[i])))
            {
                
                if(i+1 < content.length)
                {
                    //Assignment Spec -> 7.b
                    if( content[i] =='.')
                    {
                        if(capitalLetter.contains(String.valueOf(content[i+1])) || lowercaseLetter.contains(String.valueOf(content[i+1])) || numberAndOthers.contains(String.valueOf(content[i+1])))
                        {
                            tokenBuilder.append(content[i]);
                        }
                        else
                        {
                            if(tokenBuilder.toString().toLowerCase().trim().length()!=0) {
                                terms.add(tokenBuilder.toString().toLowerCase().trim());
                                tokenBuilder = new StringBuilder();
                            }
                        }
                    }   

                    //Assignment Spec -> 7.c
                    if( content[i] ==' ')
                    {
                        if(tokenBuilder.length() !=0)
                        {
                            
                            if(capitalLetter.contains(String.valueOf(tokenBuilder.charAt(0))) && capitalLetter.contains(String.valueOf(content[i+1])))
                            {
                                tokenBuilder.append(String.valueOf(content[i]));
                            }
                            else
                            {
                                if(tokenBuilder.toString().toLowerCase().trim().length()!=0) {
                                    terms.add(tokenBuilder.toString().toLowerCase().trim());
                                    tokenBuilder = new StringBuilder();
                                }
                            }
                            
                        }
                    }
                    //Assgignment Spec -> 7.a
                    if( content[i] == '-')
                    {
                        if(capitalLetter.contains(String.valueOf(content[i+1])) || lowercaseLetter.contains(String.valueOf(content[i+1])) || numberAndOthers.contains(String.valueOf(content[i+1])))
                        {
                            tokenBuilder.append(content[i]);
                        }
                        else if(i+3 <content.length)
                        {
                            if(capitalLetter.contains(String.valueOf(content[i+3])) || lowercaseLetter.contains(String.valueOf(content[i+3])) || numberAndOthers.contains(String.valueOf(content[i+3])))
                                i = i+2;
                        }
                    }
                }            
            }
        }
        
        return terms;
    }
    
    public ArrayList<String> eliminateStopwords(ArrayList<String> rawTerms)
    {
        ArrayList<String> terms = rawTerms;
        int i = 0;
        
        while(i < terms.size())
        {
            if (stopwords.contains(terms.get(i)))
            {
                terms.remove(i);
                
            }
            else
            {
                i++;
            }
        }

        return terms;
    }
    
    public ArrayList<String> stem(ArrayList<String> rawTerms)
    {
        ArrayList<String> stemmed = new ArrayList<>();
        
        for(int i = 0; i < rawTerms.size();i++)
        {
            boolean stemSwitch = false;
            char[] terms = rawTerms.get(i).toCharArray();
            for(int j =0; j< rawTerms.get(i).length();j++)
            {
                if(!lowercaseLetter.contains(String.valueOf(terms[j])))
                {
                    stemSwitch = false;
                    break;
                }      
                stemSwitch = true;
            }
            if(stemSwitch)
            {
                Stemmer s = new Stemmer();
                stemmed.add(s.getStem(rawTerms.get(i)));
            }
            else
            {
                stemmed.add(rawTerms.get(i));
            }
        }
        
        return stemmed;
    }
    
    private ArrayList<String> getStopwordList()
    {
        ArrayList<String> stopword = new ArrayList<String>();
        String temp = readFile(stopwordLocation);
        String[] words = temp.split("\n");
        for(int i = 0; i < Array.getLength(words);i++)
        {
            stopword.add(words[i].trim());
        }
        return stopword;
    }
    
    /**
     * Read file and return as a string
     * 
     * @param filename
     * 
     * @return the file content as a String
     */
    public String readFile(String filename)
    {
        String fileContent = "";
        try(BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
           
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                fileContent = sb.toString();
        }
        catch(IOException e) {
            System.out.println("IO Exception : " + e);
        }
        
        return fileContent;
    }
    

    
    public TreeMap<String,Integer> getFrequence(ArrayList<String> terms)
    {
        TreeMap<String,Integer> termsWithFre = new TreeMap<>();
        for (String term : terms) {
            if (!termsWithFre.containsKey(term)) {
                termsWithFre.put(term, 1);
            } else {
                int oldFrequence = termsWithFre.get(term);
                termsWithFre.put(term,oldFrequence+1);
            }
        }
        return termsWithFre;
    }
    
    public TreeMap<String,Integer> getTerms(String fileName)
    {
        return getFrequence(stem((eliminateStopwords(tokenize(readFile(fileName))))));
    }
    
    public ArrayList<String> getTerms(ArrayList<String> keywords)
    {
        StringBuilder sb = new StringBuilder();
    
        for(int i = 0; i < keywords.size();i++)
        {
            sb.append(keywords.get(i));
            sb.append(" ");
        }
        sb.append("!");
        return stem((eliminateStopwords(tokenize(sb.toString().trim()))));
    }
}
