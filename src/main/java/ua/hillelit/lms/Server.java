package ua.hillelit.lms;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Server} is a class simple multiple client's server,
 * which has the capacity to service many clients and many requests simultaneously.
 *
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class Server {

  private ServerSocket serverSocket;
  private final List<ClientHandler> clients = new ArrayList<>();

  /**
   * Start the server on port {@param port}.
   *
   * @param port server socket
   */
  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("SERVER is started at socket[" + port + "]");

      while (!serverSocket.isClosed()) {
        // Create and start new thread for a new client
        ClientHandler clientHandler = new ClientHandler(serverSocket.accept(),clients);
        clientHandler.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      stop();
    }
  }

  /**
   * Stop the server
   */
  public void stop() {
    try {
      serverSocket.close();
      System.out.println("SERVER is stopped");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
