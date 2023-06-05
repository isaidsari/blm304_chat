package client;

public class Main
{
    final static boolean DEBUG = true;

    final static String HOST = DEBUG ? "localhost" : "";
    final static int PORT = 5050;

    public static void main(String[] args)
    {
        // TODO: Add ui, connect screen, etc

        Client client = new Client(HOST, PORT, "Test");
        client.start();

        // shutdown hook to disconnect from server
        Runtime.getRuntime().addShutdownHook(new Thread(client::onDisconnect));

    }
}
