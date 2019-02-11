package io;

import network.Client;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Frank on 6/16/2017.
 */
public class ClientConsoleThread extends Thread
{
    private Client client;
    private BufferedReader reader;
    public ClientConsoleThread(Client client, BufferedReader reader)
    {
        this.reader = reader;
        this.client = client;

    }
    @Override
    public void run()
    {
        String currentLine = "";
        while(!currentLine.equalsIgnoreCase("<<Terminate>>"))
        {
            try {
                currentLine = reader.readLine();
                client.Send(currentLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
