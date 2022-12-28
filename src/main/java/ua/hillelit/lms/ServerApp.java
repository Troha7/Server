package ua.hillelit.lms;

/**
 * @author Dmytro Trotsenko on 13.12.2022
 */
public class ServerApp {
  public static void main(String[] args) {
    Server server = new Server();
    server.start(8080);
  }

}
