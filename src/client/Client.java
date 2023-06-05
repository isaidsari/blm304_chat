package client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Client extends Thread
{
    private String host;
    private int port;

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private final String name;

    private static final int BUFFER_SIZE = 1024;
    private byte[] buffer;

    public Client(String host, int port, String name)
    {
        this.name = name;
        this.buffer = new byte[BUFFER_SIZE];

        try
        {
            this.host = host;
            this.port = port;
            socket = new Socket(host, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();

            System.out.println("Connected to server " + host + ":" + port);

            // Send client name to server
            out.write(("name:" + name).getBytes());
            System.out.println("Sent name '"+this.name+"' to server");

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

                if (size == -1) {
                    // Server disconnected
                    System.out.println("Server " + host + ":" + port + " disconnected");
                    break;
                }
                String data = new String(buffer, 0, size);
                System.out.println("Server " + host + ":" + port + " sent: " + data);
                parseMessage(data);
            }
        }
        catch (Exception e)
        {
            if (e.getMessage().equals("Connection reset"))
            {
                System.out.println("Server " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()
                        + " connection reset");
            }
            else
            {
                e.printStackTrace();
            }
        }
        finally
        {
            try
            {
                onDisconnect();
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
            System.out.println("Sent to server " + host + ":" + port + ": " + data);
        }
        catch (Exception e)
        {
            System.out.println("Client send error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void parseMessage(String message)
    {
        String[] parts = message.split(":");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (command)
        {
            case "message":
                System.out.println("Server " + host + ":" + port + " sent message: " + args[0]);
                break;

            case "clientlist":
                System.out.println("Server " + host + ":" + port + " sent clientlist: " + args[0]);
                List<String> clients = Arrays.asList(args[0].split(","));
                break;

            case "groupList":
                System.out.println("Server " + host + ":" + port + " sent groupList: " + args[0]);
                List<String> groups = Arrays.asList(args[0].split(","));
                break;

            default:
                System.out.println("Server " + host + ":" + port + " sent unknown command: " + command);
                break;
        }
    }

    public void onDisconnect()
    {
        try
        {
            if (in != null) in.close();
            if (out != null) out.close();

            socket.close();
            System.out.println("Disconnected from server " + host + ":" + port);
        }
        catch (Exception e)
        {
            System.out.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
