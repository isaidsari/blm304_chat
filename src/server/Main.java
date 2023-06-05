package server;

public class Main
{
    final static int PORT = 5050;

    public static void main(String[] args)
    {
        Server server = new Server(PORT);
        server.start();
    }

}