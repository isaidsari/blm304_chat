package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler extends Thread implements ClientEventHandler
{

    Socket socket;
    private InputStream in;
    private OutputStream out;

    private static int id = 0;
    public String name;

    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
        this.buffer = new byte[BUFFER_SIZE];
        this.name = ("Anon" + id++);

        try
        {
            in = socket.getInputStream();
            out = socket.getOutputStream();

            this.onConnectEvent(this);
        }
        catch (Exception e)
        {
            System.out.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                int size = in.read(buffer);

                if (size == -1)
                {
                    System.out.println("Client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
                            + " disconnected");
                    break;
                }
                String data = new String(buffer, 0, size);

                onMessageEvent(this, data);
            }
        }
        catch (Exception e)
        {
            if (e instanceof IOException)
            {
                if (e.getMessage().equals("Connection reset"))
                    System.out.println("Client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
                            + " connection reset");
            }
            else
            {
                System.out.println("Client error: " + e.getMessage());
                e.printStackTrace();
            }

        }
        finally
        {
            try
            {
                onDisconnectEvent(this);
            }
            catch (Exception e)
            {
                System.out.println("Client error: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public void send(String data)
    {
        try
        {
            out.write(data.getBytes());
        }
        catch (Exception e)
        {
            System.out.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseMessage(String message)
    {
        String[] parts = message.split(":");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (command)
        {
            case "name":
                System.out.println("Client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
                        + " set name to '" + name + "' -> '" + args[0] + "'" );
                name = args[0];
                break;
            default:
                break;
        }

    }

    @Override
    public void onMessageEvent(ClientHandler source, String message)
    {
        System.out.println("[onMessageEvent] Client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
                + " sent: " + message);
        parseMessage(message);
    }

    @Override
    public void onConnectEvent(ClientHandler source)
    {
        System.out.println("[onConnectEvent] Client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
                + " connected");
    }

    @Override
    public void onDisconnectEvent(ClientHandler source) throws IOException
    {
        System.out.println("[onDisconnectEvent] Closing client " + socket.getInetAddress().getHostAddress() + ":"
                + socket.getPort());
        socket.close();
    }

}
