
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ray
 */
public class Configuration {
    private double minIDF;
    private double docProportion;
    private boolean implicitFeedback;
    private boolean championSwitch;
    
    public Configuration()
    {
        loadParameters();
    }
    
    public double getMinIdf()
    {
       
        return minIDF;
    }
    
    public double getProportion()
    {
        return docProportion;
    }
    
    public boolean getImplicitFeedbade()
    {
        return implicitFeedback;
    }
    
    public boolean getChampionSwitch()
    {
        return championSwitch;
    }
    
    public void loadParameters()
    {
        try(BufferedReader br = new BufferedReader(new FileReader("config")))
        {
            String dfp = br.readLine();
            if(dfp.equals("0"))
                minIDF = 99999.99;
            else 
                minIDF =  Math.log(1/(Double.parseDouble(dfp)/100));
            docProportion = Double.parseDouble(br.readLine())/100;
            
            String bool = br.readLine();
            if(bool.equals("true"))
                implicitFeedback = true;
            else if(bool.equals("false"))
                implicitFeedback = false;
            
            String champ = br.readLine();
            if(champ.equals("true"))
                championSwitch = true;
            else if(champ.equals("false"))
                championSwitch = false;
            
      
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
}
