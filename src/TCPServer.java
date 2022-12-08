import java.io.IOException;
import java.util.Scanner;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        String portString = "9876";
//        String portString = null;
//        if (args.length != 0) {
//            if (args[0] != null) {
//                portString = (String) args[0];
//            } else {
//                System.out.print("Error! No port found in arguments");
//                System.exit(0);
//            }
//        } else {
//            System.out.print("Error! No port found in arguments");
//            System.exit(0);
//        }

        int port = 0;
        Scanner scanner = new Scanner(portString);
        if (scanner.hasNextInt()) {
            port = scanner.nextInt();
            if ((port < 0) || (port > 9999)) {
                System.out.println("Error! Invalid port.");
                System.exit(1);
            }
        } else {
            System.out.println("Error! Invalid port.");
            System.exit(1);
        }
        ServerService server = new ServerService();
        server.run(port);
    }
}

