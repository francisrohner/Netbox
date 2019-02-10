package main.java.network;

import main.java.data.ByteUtils;
import main.java.data.SomeData;
import main.java.io.ConfigReader;
import main.java.io.ClientConsoleThread;
import main.java.io.Logger;

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
    private InputStream in;
    private OutputStream out;
    private Logger logger;

    private String name;

    public void Send(Object obj)
    {
        try {
            out.write(ByteUtils.TransmissionObject(obj, ByteUtils.ObjectType.STRING));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

            System.out.print("Enter a name: " );
            name = stdIn.readLine();

            out = socket.getOutputStream();
            in = socket.getInputStream();

            //out.writeObject("set nick " + name);
            out.write(ByteUtils.TransmissionObject("set nick" + name, ByteUtils.ObjectType.STRING));
            out.flush();

            ClientConsoleThread kh = new ClientConsoleThread(this, stdIn);
            Thread consoleThread = new Thread(kh);
            consoleThread.start();

            Object currentObject;


            for(;;)
            {
                byte[] data = ByteUtils.GetBytesFromStream(in);
                currentObject = ByteUtils.ParseObject(data);
                if(currentObject instanceof String)
                {
                    String currentLine = (String) currentObject;
                    //logger.log("Received message [" + currentLine + "] from server.");
                    logger.log(currentLine);
                    if (currentLine.equals("<<Terminate>>"))
                        break;
                }
                else if(currentObject instanceof SomeData)
                {
                    //SomeData sd = new SomeData("a", 1);

                    logger.log("Received object [" + currentObject.toString() + "] from server");
                    //out.writeObject("SomeData object received successfully from Server");
                    out.write(ByteUtils.TransmissionObject("SomeData object received successfully from Server", ByteUtils.ObjectType.STRING));

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
        //catch (ClassNotFoundException e)
        //{
        //    e.printStackTrace();
        //}

        logger.log("--Client Execution End--");
    }
    public static void main(String args[])
    {
        IGNORE_CONFIG = args.length > 0 && args[0].equalsIgnoreCase("noconfig");
        new Client();
    }
}

