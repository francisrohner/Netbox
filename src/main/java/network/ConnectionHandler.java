package network;

import data.ByteUtils;

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
        }
        catch(SocketException zex)
        {
            open = false;
            server.shareClientMessage("<<Left>>", this);
        }
        catch (IOException e) {
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
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void send(Object o) throws IOException {
        if(o instanceof String)
        {
            out.write(ByteUtils.TransmissionObject(o, ByteUtils.ObjectType.STRING));
        }
        else if(o instanceof byte[])
        {
            byte[] file = (byte[])o;
            //First 1530 bytes are UTF-8 string


            //Add header
        }
        out.flush();
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

            send("<<Initiate>>");

            while(open)
            {

                if(clientSocket.isClosed())
                {
                    server.shareClientMessage("<<Left>>", this);
                    open = false;
                    out.flush();
                    clientSocket.close();
                    continue;
                }
                byte[] streamBytes = ByteUtils.GetBytesFromStream(in);
                if(streamBytes == null || streamBytes.length < 1) continue;
                currentObject = ByteUtils.ParseObject(streamBytes);
                ByteUtils.ObjectType objectType = ByteUtils.GetObjectType(streamBytes);
                if(objectType == ByteUtils.ObjectType.STRING)
                {
                    String currentLine = (String)currentObject;

                    server.logger.log("Received message [" + currentLine + "] from client #" + clientHandled);
                    if(currentLine.toLowerCase().equals("exit"))
                    {
                        System.out.println("Client " + clientHandled + " disconnected.");
                        server.logger.log("Server sending terminate message");
                        out.write(ByteUtils.TransmissionObject("<<Terminate>>", ByteUtils.ObjectType.STRING));
                        out.flush();
                        open = false;
                    }
                    else if(currentLine.toLowerCase().equals("terminate"))
                    {
                        send("<<Terminate>>");
                        server.haltServer();
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
                        server.shareClientMessage("<<Join>>", this);
                        out.write(ByteUtils.TransmissionObject("", ByteUtils.ObjectType.STRING));
                        out.flush();
                    }
                    else
                    {
                        server.shareClientMessage(currentLine, this);
                        out.write(ByteUtils.TransmissionObject(nick + ": " + currentLine, ByteUtils.ObjectType.STRING));
                        out.flush();
                    }
                }
                else if(objectType == ByteUtils.ObjectType.FILE)
                {

                }
                else
                {
                    server.logger.log("Unknown object received from Client");
                    out.write(ByteUtils.TransmissionObject("Unknown object received from Client", ByteUtils.ObjectType.STRING));
                    out.flush();
                }
            }
            in.close();
            out.close();
        }
        catch(SocketException zex)
        {
            try
            {
                System.out.println("Client " + clientHandled + " disconnected.");
                server.shareClientMessage("<<Left>>", this);
                open = false;
                out.flush();
                clientSocket.close();
            }
            catch(Exception ex) {}
        }
        catch(IOException ex)
        {
            System.out.println("Client " + clientHandled + " disconnected.");
            server.shareClientMessage("<<Left>>", this);
            open = false;
            ex.printStackTrace();
        }
    }
}
