package ua.hillelit.lms;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * {@link FileReceiver} is a class which receive the file from client.
 *
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class FileReceiver {
  private final Socket clientSocket;
  private DataInputStream fileReader;

  public FileReceiver(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  /**
   * Receive the file from client.
   * First we create {@link FileOutputStream} on the path {@code pathToFile},
   * then get file size from the client.
   * Create a new buffer of size 4 Kbs.
   * Send the file of 4 Kbs in a loop.
   *
   * @param pathToFile path to file
   * @throws Exception
   */
  public void receiveFile(String pathToFile) throws Exception {
    FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);
    fileReader = new DataInputStream(clientSocket.getInputStream());

    int bytes = 0;
    long size = 0;
    size = fileReader.readLong();     // read file size
    System.out.println("[SERVER] >>> file size[" + size +"]");

    byte[] buffer = new byte[4 * 1024]; // buffer size 4 Kbs
    int minSize = (int) Math.min(buffer.length, size);
    bytes = fileReader.read(buffer, 0, minSize);
    while (size > 0 && bytes != -1) {
      fileOutputStream.write(buffer, 0, bytes);
      size -= bytes; // read up to file size
    }
    fileOutputStream.close();
  }

  /**
   * Close DataInputStream
   */
  public void close(){
    try {
      fileReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
