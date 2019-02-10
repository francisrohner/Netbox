package main.java.io;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Francis Rohner on 6/10/15.
 */
public class ConfigReader
{
    private Map<String, String> configMap;
    public ConfigReader(String fileName)
    {
        try
        {
            configMap = new HashMap<String, String>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String currentLine;
            while((currentLine = bufferedReader.readLine()) != null)
            {
                if(!currentLine.isEmpty())
                 configMap.put(currentLine.split("=")[0], currentLine.split("=")[1]);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public String getSetting(String key) { return configMap.get(key); }
}
