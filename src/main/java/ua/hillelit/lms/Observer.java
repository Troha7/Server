package ua.hillelit.lms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class Observer {

  private BufferedReader textReader;
  private final Socket clientSocket;
  private FileReceiver fileReceiver;

  private final String EXIT = "-exit";
  private final String FILE = "-file";

  public Observer(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void read(String clientName) {
    try {
      textReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      fileReceiver = new FileReceiver(clientSocket);

      String inputLine;
      while ((inputLine = textReader.readLine()) != null) {
        print(clientName, inputLine);

        String pathToFile = getPathToFile(inputLine);
        if (pathToFile != null) {
          fileReceiver.receiveFile(pathToFile);
          System.out.println("[SERVER] >>> file:{" + pathToFile + "} was received from " + clientName );
        }

        if (inputLine.startsWith(EXIT)) {
          System.out.println("[SERVER] >>> " + clientName + " exit");
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getPathToFile(String message) {
    if (message.startsWith(FILE)) {
      return message.replaceFirst(FILE, "").trim();
    }
    return null;
  }

  private void print(String clientName, String message) {
    String socket = clientSocket.getRemoteSocketAddress().toString();
    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    System.out.println("[" + clientName + "] " + time + socket + " <<< " + message);
  }

  public void close() {
    try {
      textReader.close();
      fileReceiver.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
