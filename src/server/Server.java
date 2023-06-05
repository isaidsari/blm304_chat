package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server extends Thread
{
    private boolean running = false;

    private int port;
    private ServerSocket serverSocket;

    private List<ClientHandler> clients;

    public Server(int port)
    {
        this.port = port;
        clients = new ArrayList<>();

        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        }
        catch (Exception e)
        {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void run()
    {
        running = true;
        System.out.println("Waiting for clients...");

        try
        {
            while (running)
            {
                ClientHandler c = new ClientHandler(serverSocket.accept())
                {
                    @Override
                    public void onMessageEvent(ClientHandler source, String message)
                    {
                        super.onMessageEvent(source, message);
                        onMessage(source, message);
                    }

                    @Override
                    public void onConnectEvent(ClientHandler source)
                    {
                        super.onConnectEvent(source);
                        onConnect(source);
                    }

                    @Override
                    public void onDisconnectEvent(ClientHandler source) throws IOException
                    {
                        super.onDisconnectEvent(source);
                        onDisconnect(source);
                    }

                };

            }
        }
        catch (Exception e)
        {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                serverSocket.close();
            }
            catch (Exception e)
            {
                System.out.println("Client error: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    void onMessage(ClientHandler source, String message)
    {
        String[] parts = message.split(":");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (command)
        {
            case "msg":
                System.out.println("Client " + source.name + " sent message: '" + args[0] + "'");
                break;
            default:
                break;
        }
    }

    void onConnect(ClientHandler source)
    {
        clients.add(source);
        source.start();
    }

    void onDisconnect(ClientHandler source)
    {
        if (!clients.remove(source))
        {
            System.out.println("Disconnected client not found in list");
        }
    }

}
