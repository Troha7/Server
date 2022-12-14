package ua.hillelit.lms;

/**
 * @author Dmytro Trotsenko on 13.12.2022
 */
public class ServerApp {
  public static void main(String[] args) {
    MyServer server = new MyServer();
    server.start(8080);
  }

}
