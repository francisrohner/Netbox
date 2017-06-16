package network;

import data.SomeData;

import java.io.*;
import java.net.Socket;

/**
 * Created by Francis Rohner on 6/10/15.
 */
public class ConnectionHandler implements Runnable
{
    private Server server;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int clientHandled;
    private boolean open;
    public String nick;

    public ConnectionHandler(Server server, Socket clientSocket)
    {
        this.server = server;
        this.clientSocket = clientSocket;
        open = true;
    }

    public void SendMessage(String msg)
    {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() { return open; }
    public void kill()
    {
        try
        {
            out.writeObject("<<Terminate>>");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        clientHandled =  ++server.numClients;
        nick = "Client #" + clientHandled;
        server.logger.log("New connection handler running handling client #" + clientHandled);
        try
        {

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            //Send initial message to client
            SomeData someData = new SomeData("Test", 7);
            Object currentObject;
            out.writeObject("<<Initiate>>");
            out.flush();



            while ((currentObject = in.readObject()) != null) {
                if(currentObject instanceof String)
                {
                    String currentLine = (String)currentObject;
                    server.ShareClientMessage(currentLine, clientHandled);
                    server.logger.log("Received message [" + currentLine + "] from client #" + clientHandled);
                    if (currentLine.toLowerCase().equals("a"))
                    {
                        out.writeObject("Response A");
                        out.flush();
                    } else if (currentLine.toLowerCase().equals("b"))
                    {
                        out.writeObject("Response B");
                        out.flush();
                    } else if(currentLine.toLowerCase().equals("exit"))
                    {
                        server.logger.log("Server sending terminate message");
                        out.writeObject("<<Terminate>>");
                        out.flush();
                        open = false;
                        break;
                    }
                    else if(currentLine.toLowerCase().contains(("set nick")))
                    {
                        nick = currentLine.replace("set nick", "").trim();
                    }
                    else
                    {
                        //???
                    }
                }
                else if(currentObject instanceof SomeData)
                {
                    server.logger.log("Received object [" + currentObject.toString() + "] from client #" + clientHandled);
                    out.writeObject("SomeData object received successfully from Client");
                    out.flush();
                }
                else
                {
                    server.logger.log("Unknown object received from Client");
                    out.writeObject("Unknown object received from Client");
                    out.flush();
                }
            }
            in.close();
            out.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
