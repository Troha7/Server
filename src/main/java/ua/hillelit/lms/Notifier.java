package ua.hillelit.lms;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class Notifier {

  private Socket clientSocket;
  private PrintWriter textWriter;

  public Notifier(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void write(String message){
    try {
      textWriter = new PrintWriter(clientSocket.getOutputStream(), true);
      textWriter.println(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
    public void writeToAllClients(List<ClientHandler> clients, String message) {
    for (ClientHandler client : clients) {
     client.send(message);
    }
  }

  public void close(){
    textWriter.close();
  }
}
