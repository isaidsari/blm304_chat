package server;

import java.io.IOException;

public interface ClientEventHandler
{

    public void onMessageEvent(ClientHandler source, String message);

    public void onConnectEvent(ClientHandler source);

    public void onDisconnectEvent(ClientHandler source) throws IOException;
}
