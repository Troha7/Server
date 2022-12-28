package ua.hillelit.lms;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *  {@link ClientHandler} is a class which starts the new thread for a new client.
 *
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class ClientHandler extends Thread {

  private final Socket clientSocket;
  private Notifier notifier;
  private Observer observer;

  private final List<ClientHandler> clients;
  public ClientHandler(Socket socket, List<ClientHandler> clients) {
    this.clientSocket = socket;
    this.clients = clients;
  }

  public void send(String message) {
    notifier.write(message);
  }

  @Override
  public void run() {
    try {
      notifier = new Notifier(clientSocket);
      observer = new Observer(clientSocket);

      clients.add(this);
      String clientName = "Client-" + clients.lastIndexOf(this);

      notifier.writeToAllClients(clients, "[SERVER] >>> " + clientName + " is connected");
      observer.read(clientName);

      closeClientSocket(clientName);

      clients.remove(this);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void closeClientSocket(String clientName) throws IOException {
    notifier.close();
    observer.close();
    clientSocket.close();
    System.out.println("[SERVER] >>> " + clientName + " is disconnected");
  }
}
