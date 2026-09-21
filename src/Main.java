import ui.ConsoleMenu;

public class Main {
  public static void main(String[] args) {
    try {
      new ConsoleMenu().start();
    } catch (Exception e) {
      System.out.println("Fatal error: " + e.getMessage());
    }
  }
}
