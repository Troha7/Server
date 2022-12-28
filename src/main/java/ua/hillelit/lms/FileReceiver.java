package ua.hillelit.lms;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class FileReceiver {
  private final Socket clientSocket;
  private DataInputStream fileReader;

  public FileReceiver(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void receiveFile(String pathToFile) throws Exception {
    FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);
    fileReader = new DataInputStream(clientSocket.getInputStream());

    long size = 0;
    size = fileReader.readLong();     // read file size
    System.out.println("[SERVER] >>> file size[" + size +"]");

    byte[] buffer = new byte[4 * 1024];
    int minSize = (int) Math.min(buffer.length, size);
    int bytes = fileReader.read(buffer, 0, minSize);
    while (size > 0 && bytes != -1) {
      fileOutputStream.write(buffer, 0, bytes);
      size -= bytes;
    }
    fileOutputStream.close();
  }

  public void close(){
    try {
      fileReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
