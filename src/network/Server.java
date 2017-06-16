package network;

import io.ConfigReader;
import io.Logger;
import io.ServerConsoleThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Francis Rohner on 6/9/15.
 */

public class Server
{

    private static boolean IGNORE_CONFIG;
    public static int numClients = 0;
    private static final String SERVER_CONFIG_FILENAME = "Server.cfg";
    private static final String SERVER_LOG_FILENAME = "ServerLog.txt";
    private static final int    SERVER_DEFAULT_PORT = 3141;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    public Logger logger;
    public ArrayList<ConnectionHandler> connectionHandlers;


    public Server()
    {
        connectionHandlers = new ArrayList<ConnectionHandler>();
        logger = new Logger(SERVER_LOG_FILENAME);
        logger.log("--Server Execution Start--");
        try
        {
            //(new Thread(new OldShit(this))).start();
            ServerConsoleThread serverConsoleThread = new ServerConsoleThread(this);
            Thread thread = new Thread(serverConsoleThread);
            thread.start();

            int serverPort = SERVER_DEFAULT_PORT;
            if ((new File(SERVER_CONFIG_FILENAME)).exists())
            {
                ConfigReader serverConfigReader = new ConfigReader(SERVER_CONFIG_FILENAME);
                serverPort = Integer.parseInt(serverConfigReader.getSetting("Port"));
            }
            serverSocket = new ServerSocket(serverPort);

          for(;;)
          {
              clientSocket = null;
              clientSocket = serverSocket.accept();
              ConnectionHandler connectionHandler = new ConnectionHandler(this, clientSocket);
              connectionHandlers.add(connectionHandler);
              (new Thread(connectionHandler)).start();
          }

        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    public void ShareClientMessage(String msg, int client)
    {
        for(int i = 0; i < connectionHandlers.size(); i++)
            if((i + 1) != client)
                connectionHandlers.get(i).SendMessage(connectionHandlers.get(client - 1).nick + ": " +  msg);
    }

    public void haltServer()
    {
        logger.log("Killing connection handlers");
        for(ConnectionHandler ch: connectionHandlers)
            if(ch.isOpen())
                ch.kill();
        logger.log("--Server Execution End--");
        System.exit(0);
    }
    public static void main(String args[])
    {
        IGNORE_CONFIG = args.length > 0 && args[0].equalsIgnoreCase("noconfig");
        new Server();
    }

    public void Send(String currentLine)
    {
        for(ConnectionHandler handler : connectionHandlers)
            if(handler.isOpen())
                handler.SendMessage(currentLine);
    }
}
