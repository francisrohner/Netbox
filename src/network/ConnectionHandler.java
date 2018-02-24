package network;

import data.ByteUtils;
import data.SomeData;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Francis Rohner on 6/10/15.
 */
public class ConnectionHandler implements Runnable
{
    private Server server;
    private Socket clientSocket;
    private InputStream in;
    private OutputStream out;
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
            out.write(ByteUtils.TransmissionObject(msg, ByteUtils.ObjectType.STRING));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() { return open; }
    public int getId() { return clientHandled; }

    public void kill()
    {
        try
        {
            out.write(ByteUtils.TransmissionObject("<<Terminate>>", ByteUtils.ObjectType.STRING));
            out.flush();
            //out.writeObject("<<Terminate>>");
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
            out = clientSocket.getOutputStream();
            in = clientSocket.getInputStream();
            Object currentObject;

            out.write(ByteUtils.TransmissionObject("<<Initiate>>", ByteUtils.ObjectType.STRING));
            out.flush();


            while(open)
            {

                byte[] streamBytes = ByteUtils.GetBytesFromStream(in);
                currentObject = ByteUtils.ParseObject(streamBytes);
                //ByteUtils.
                if(currentObject instanceof String)
                {
                    String currentLine = (String)currentObject;
                    server.ShareClientMessage(currentLine, this);
                    server.logger.log("Received message [" + currentLine + "] from client #" + clientHandled);
                    if (currentLine.toLowerCase().equals("a"))
                    {
                        out.write(ByteUtils.TransmissionObject("Response A", ByteUtils.ObjectType.STRING));
                        out.flush();
                    } else if (currentLine.toLowerCase().equals("b"))
                    {
                        out.write(ByteUtils.TransmissionObject("Response B", ByteUtils.ObjectType.STRING));
                        out.flush();
                    } else if(currentLine.toLowerCase().equals("exit"))
                    {
                        System.out.println("Client " + clientHandled + " disconnected.");
                        server.logger.log("Server sending terminate message");
                        out.write(ByteUtils.TransmissionObject("<<Terminate>>", ByteUtils.ObjectType.STRING));
                        out.flush();
                        open = false;
                        //break;
                    }
                    else if(currentLine.toLowerCase().contains("list users"))
                    {
                        StringBuilder users = new StringBuilder();
                        users.append("<<Users>>\r\n");
                        for(ConnectionHandler handler: server.connectionHandlers)
                            if(handler.isOpen())
                                users.append(handler.nick + "\r\n");
                        out.write(ByteUtils.TransmissionObject(users.toString(), ByteUtils.ObjectType.STRING));
                        out.flush();
                    }
                    else if(currentLine.toLowerCase().contains(("set nick")))
                    {
                        nick = currentLine.replace("set nick", "").trim();
                        server.ShareClientMessage(nick + " joined the chat.", this);
                        out.write(ByteUtils.TransmissionObject("", ByteUtils.ObjectType.STRING));
                        out.flush();
                    }
                    else
                    {
                        out.write(ByteUtils.TransmissionObject(nick + ": " + currentLine, ByteUtils.ObjectType.STRING));
                        out.flush();
                        //???
                    }
                }
                else
                {
                    server.logger.log("Unknown object received from Client");
                    out.write(ByteUtils.TransmissionObject("Unknown object received from Client", ByteUtils.ObjectType.STRING));
                    //out.writeObject("Unknown object received from Client");
                    out.flush();
                }
            }
            in.close();
            out.close();
        }
        catch(SocketException sex)
        {
            try
            {
                System.out.println("Client " + clientHandled + " disconnected.");
                open = false;
                out.flush();
                clientSocket.close();
            }
            catch(Exception ex) {}
        }
        catch(IOException ex)
        {
            System.out.println("Client " + clientHandled + " disconnected.");
            open = false;
            ex.printStackTrace();
        }
    }
}
