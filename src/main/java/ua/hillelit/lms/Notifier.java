package ua.hillelit.lms;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * {@link Notifier} is a class which send the message to the client
 * and can notify all clients.
 *
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class Notifier {

  private final Socket clientSocket;
  private PrintWriter textWriter;

  public Notifier(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  /**
   * Sends a message to the client
   *
   * @param message text message
   */
  public void write(String message){
    try {
      textWriter = new PrintWriter(clientSocket.getOutputStream(), true);
      textWriter.println(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends the message to all connected clients
   *
   * @param clients the list {@link List<ClientHandler> clients}
   * @param message text message
   */
    public void writeToAllClients(List<ClientHandler> clients, String message) {
    for (ClientHandler client : clients) {
     client.send(message);
    }
  }

  /**
   * Close the PrintWriter
   */
  public void close(){
    textWriter.close();
  }
}
