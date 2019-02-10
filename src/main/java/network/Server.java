package network;

import io.*;

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
    public ArrayList<Thread> parallelThreads;

    public Server()
    {
        connectionHandlers = new ArrayList<ConnectionHandler>();
        parallelThreads = new ArrayList<Thread>();

        logger = new Logger(SERVER_LOG_FILENAME);
        logger.log("--Server Execution Start--");
        try
        {
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
              Cleanup();
              clientSocket = null;
              clientSocket = serverSocket.accept();
              ConnectionHandler connectionHandler = new ConnectionHandler(this, clientSocket);
              connectionHandlers.add(connectionHandler);
              (new Thread(connectionHandler)).start();


              //parallelThreads.add(new Thread(connectionHandler));

          }

        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    void Cleanup()
    {
        ArrayList<Integer> removal = new ArrayList<Integer>();
        for(int i = 0; i < connectionHandlers.size(); i++)
            if(!connectionHandlers.get(i).isOpen())
                removal.add(i);
        for(int i = removal.size() - 1; i >= 0; i--)
        {
            System.out.println("Removing handler for " + connectionHandlers.get(removal.get(i)).nick + " at index " + removal.get(i));
            connectionHandlers.remove(removal.get(i));

        }
    }

    public void ShareClientMessage(String msg, ConnectionHandler client)
    {
        for(int i = 0; i < connectionHandlers.size(); i++)
            if(connectionHandlers.get(i).getId() != client.getId() && connectionHandlers.get(i).isOpen())
                connectionHandlers.get(i).SendMessage(client.nick + ": " +  msg);
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
