package ua.hillelit.lms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link Observer} is a class which receive the message and data from the client.
 *
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

  /**
   * Listen the client and read data from the client.
   * Print text message from the client.
   * When client send {@code FILE} then server receive the file at the class {@link FileReceiver}.
   * When client send {@code EXIT} then this thread is closed,
   * and client disconnects from the server.
   *
   * @param clientName Client name
   */
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

  /**
   * Get the client message and parse file path.
   *
   * @param message text message
   * @return path to file
   */
  private String getPathToFile(String message) {
    if (message.startsWith(FILE)) {
      return message.replaceFirst(FILE, "").trim();
    }
    return null;
  }

  /**
   * Add to client message client name, current time, socket data,
   * and print this message in console.
   *
   * @param clientName client name
   * @param message message from the client
   */
  private void print(String clientName, String message) {
    String socket = clientSocket.getRemoteSocketAddress().toString();
    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    System.out.println("[" + clientName + "] " + time + socket + " <<< " + message);
  }

  /**
   * Close BufferedReader and FileReceiver.
   */
  public void close() {
    try {
      textReader.close();
      fileReceiver.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
