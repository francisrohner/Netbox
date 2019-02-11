package io;

import network.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Frank on 6/16/2017.
 */
public class ServerConsoleThread extends Thread
{
    private Server server;
    private BufferedReader reader;
    public ServerConsoleThread(Server server)
    {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.server = server;
    }
    @Override
    public void run()
    {
        String currentLine = "";
        while(!currentLine.equalsIgnoreCase("end"))
        {
            try {
                currentLine = reader.readLine();

                if(currentLine.equalsIgnoreCase("exit"))
                    server.haltServer();
                else
                    server.Send(currentLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
