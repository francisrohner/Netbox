package network;

import data.SomeData;
import io.ConfigReader;
import io.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Created by Francis Rohner on 6/9/15.
 */

public class Client
{
    static boolean IGNORE_CONFIG = false;
    static final String CLIENT_CONFIG_FILENAME = "Client.cfg";
    static final String CLIENT_LOG_FILENAME = "ClientLog.txt";
    static final int CLIENT_DEFAULT_PORT = 3141;
    static final String CLIENT_DEFAULT_HOST = "127.0.0.1";

    private Socket socket;
    private BufferedReader stdIn;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Logger logger;

    public Client()
    {
        logger = new Logger(CLIENT_LOG_FILENAME);
        logger.log("--Client Execution Start--");
        try
        {
            int clientPort = CLIENT_DEFAULT_PORT;
            String clientHost = CLIENT_DEFAULT_HOST;
            if(!IGNORE_CONFIG && (new File(CLIENT_CONFIG_FILENAME)).exists())
            {
                ConfigReader clientConfigReader = new ConfigReader(CLIENT_CONFIG_FILENAME);
                clientPort = Integer.parseInt(clientConfigReader.getSetting("Port"));
                clientHost = clientConfigReader.getSetting("Host");
            }
            socket = new Socket(clientHost, clientPort);
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Object currentObject;
            while((currentObject = in.readObject()) != null)
            {
                if(currentObject instanceof String)
                {
                    String currentLine = (String) currentObject;
                    logger.log("Received message [" + currentLine + "] from server.");
                    if (currentLine.equals("<<Terminate>>"))
                    {
                        break;
                    }
                    else
                    {
                        String userInput = stdIn.readLine();
                        if(!userInput.equals("SomeData"))
                        {
                            out.writeObject(userInput);
                            out.flush();
                        }
                        else
                        {
                            out.writeObject(new SomeData("ClientTest", -1));
                            out.flush();
                        }
                    }
                }
                else if(currentObject instanceof SomeData)
                {
                    logger.log("Received object [" + currentObject.toString() + "] from server");
                    out.writeObject("SomeData object received successfully from Server");
                    out.flush();
                }
                else
                    logger.log("Unknown object received");
            }
            in.close();
            out.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        logger.log("--Client Execution End--");
    }
    public static void main(String args[])
    {
        IGNORE_CONFIG = args.length > 0 && args[0].equalsIgnoreCase("noconfig");
        new Client();
    }
}
