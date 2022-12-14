package ua.hillelit.lms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * @author Dmytro Trotsenko on 12.12.2022
 */
public class MyServer {

  private ServerSocket serverSocket;
  private static final ArrayList<PrintWriter> clients = new ArrayList<>();

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("SERVER is started at socket[" + port + "]");
      while (true) {
        new ClientHandler(serverSocket.accept()).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      stop();
    }
  }

  public void stop() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static class ClientHandler extends Thread {

    private final Socket clientSocket;
    private static int id;

    public ClientHandler(Socket socket) {
      this.clientSocket = socket;
    }

    public void run() {
      try {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        clients.add(out);
        id++;

        writeToAllClients();

        readFromClient(in);

        closeClientSocket(in, out);

        System.out.println("[SERVER] Client-" + id + " is exit");
        clients.remove(out);
        id--;

      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private static void writeToAllClients() {
      for (PrintWriter client : clients) {
        client.println("[SERVER] Client-" + id + " is connected");
      }
    }

    private void readFromClient(BufferedReader in) throws IOException {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String socket = clientSocket.getRemoteSocketAddress().toString();

        System.out.println("[Client-" + id + "] " + time + socket + " - " + inputLine);

        if (inputLine.equalsIgnoreCase("exit")) {
          break;
        }
      }
    }

    private void closeClientSocket(BufferedReader in, PrintWriter out) throws IOException {
      out.close();
      in.close();
      clientSocket.close();
    }

  }

}
