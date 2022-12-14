package ua.hillelit.lms;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
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
    private static BufferedReader textReader;
    private static PrintWriter textWriter;
    private static DataInputStream fileReader;
    private static int id;

    public ClientHandler(Socket socket) {
      this.clientSocket = socket;
    }

    public void run() {
      try {
        textReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        textWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        fileReader = new DataInputStream(clientSocket.getInputStream());

        clients.add(textWriter);
        id++;

        writeToAllClients();

        readFromClient();

        closeClientSocket();

        System.out.println("[SERVER] Client-" + id + " is exit");
        clients.remove(textWriter);
        id--;

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private static void writeToAllClients() {
      for (PrintWriter client : clients) {
        client.println("[SERVER] Client-" + id + " is connected");
      }
    }

    private void readFromClient() throws Exception {
      String inputLine;
      while ((inputLine = textReader.readLine()) != null) {

        if (inputLine.startsWith("exit")) {
          break;
        }

        String pathToFile;
        if (inputLine.startsWith("file")) {
          pathToFile = inputLine.replaceFirst("file", "").trim();
          receiveFile(pathToFile);
        }

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String socket = clientSocket.getRemoteSocketAddress().toString();

        System.out.println("[Client-" + id + "] " + time + socket + " - " + inputLine);
      }
    }

    private static void receiveFile(String pathToFile) throws Exception {
      FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);

      long size = fileReader.readLong();     // read file size
      byte[] buffer = new byte[4 * 1024];
      int minSize = (int) Math.min(buffer.length, size);
      int bytes = fileReader.read(buffer, 0, minSize);
      while (size > 0 && bytes != -1) {
        fileOutputStream.write(buffer, 0, bytes);
        size -= bytes;      // read up to file size
      }
      fileOutputStream.close();
    }

    private void closeClientSocket() throws IOException {
      textWriter.close();
      textReader.close();
      fileReader.close();
      clientSocket.close();
    }

  }

}
