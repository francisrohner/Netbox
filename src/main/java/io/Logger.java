package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Francis Rohner on 6/10/15.
 */
public class Logger
{
    private String fileName;
    public Logger(String fileName)
    {
        this.fileName = fileName;
    }
    public void log(String line)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));
            System.out.println(line);
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
