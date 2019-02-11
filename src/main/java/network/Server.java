package network;

import io.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Francis Rohner on 6/9/15.
 */

public class Server
{

    private static boolean IGNORE_CONFIG;
    private static boolean DISABLE_STDIN;
    private static final String SERVER_CONFIG_FILENAME = "Server.cfg";
    private static final String SERVER_LOG_FILENAME = "ServerLog.txt";
    private static final int    SERVER_DEFAULT_PORT = 3141;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public Logger logger;
    public ArrayList<ConnectionHandler> connectionHandlers;
    public static int numClients = 0;

    public Server()
    {
        connectionHandlers = new ArrayList<ConnectionHandler>();

        logger = new Logger(SERVER_LOG_FILENAME);
        logger.log("--Server Execution Start--");
        try
        {

            if(!DISABLE_STDIN)
            {
                ServerConsoleThread serverConsoleThread = new ServerConsoleThread(this);
                Thread thread = new Thread(serverConsoleThread);
                thread.start();
            }

            int serverPort = SERVER_DEFAULT_PORT;
            if ((new File(SERVER_CONFIG_FILENAME)).exists())
            {
                ConfigReader serverConfigReader = new ConfigReader(SERVER_CONFIG_FILENAME);
                serverPort = Integer.parseInt(serverConfigReader.getSetting("Port"));
            }
            serverSocket = new ServerSocket(serverPort);

          for(;;)
          {
              cleanup();
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

    private void cleanup()
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

    public void shareClientMessage(String msg, ConnectionHandler client)
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
        HashMap<String, String> dict_args = new HashMap<>();
        int i = 0;
        while(i < args.length)
        {
            if(i == args.length - 1 || args[i+1].startsWith("--")) //flag
            {
                dict_args.put(args[i].substring(2).toLowerCase(), "true");
                i++;
            }
            else
            {
                dict_args.put(args[i].toLowerCase(), args[i+1]);
                i += 2;
            }
        }
        IGNORE_CONFIG = dict_args.containsKey("noconfig");
        DISABLE_STDIN = dict_args.containsKey("disable_stdin");
        new Server();
    }

    public void Send(String currentLine)
    {
        for(ConnectionHandler handler : connectionHandlers)
            if(handler.isOpen())
                handler.SendMessage(currentLine);
    }
}
