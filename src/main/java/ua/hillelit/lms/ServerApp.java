package ua.hillelit.lms;

/**
 * {@link ServerApp} is a main class.
 *
 * @author Dmytro Trotsenko on 26.12.2022
 */
public class ServerApp {
  public static void main(String[] args) {
    Server server = new Server();
    server.start(8080);
  }

}
