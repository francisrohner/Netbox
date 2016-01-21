package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Francis Rohner on 6/12/15.
 */
public class ServerConsole implements Runnable
{
    private Server server;
    public ServerConsole(Server server) { this.server = server; }
    @Override
    public void run()
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while(!bufferedReader.readLine().equals("exit"));
            server.haltServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
