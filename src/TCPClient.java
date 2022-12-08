import java.io.IOException;
import java.util.Scanner;

public class TCPClient {
    public static void main(String[] args) throws IOException {
        String addressString = "127.0.0.1:9876";
//        String addressString = null;
//        if (args.length != 0) {
//            if (args[0] != null) {
//                addressString = (String) args[0];
//            } else {
//                System.out.print("Error! No ip and port found in arguments");
//                System.exit(0);
//            }
//        } else {
//            System.out.print("Error! No ip and port found in arguments");
//            System.exit(0);
//        }
//
//        if (!checkAddressValidity(addressString)) {
//            System.out.println("Error! Invalid IP or Port.");
//            System.exit(1);
//        }

        int colonIndex = 0;
        char[] addressCharArray = addressString.toCharArray();
        for (int i = 0; i < addressString.length(); i++) {
            if (addressCharArray[i] == ':') colonIndex = i;
        }
        String ipString = addressString.substring(0, colonIndex);
        String portString = addressString.substring(colonIndex + 1);
        Scanner scanner = new Scanner(portString);
        int port = scanner.nextInt();
        ClientService client = new ClientService();
        client.run(ipString, port);
    }

    public static boolean checkAddressValidity(String address) {
        int dotCounter = 0;
        int colonCounter = 0;
        char[] addressChars = address.toCharArray();

        if (address.length() > 20) return false;
        if (address.length() < 9) return false;

        for (int i = 0; i < address.length(); i++) {
            if (addressChars[i] == '.') dotCounter++;
            if (addressChars[i] == ':') colonCounter++;
            if (!checkChar(addressChars[i])) return false;
        }
        if (dotCounter != 3) return false;
        if (colonCounter != 1) return false;

        int colonIndex = 0;
        for (int i = 0; i < addressChars.length; i++) {
            if (addressChars[i] == ':') colonIndex = i;
        }
        String ipString = address.substring(0, colonIndex);
        String portString = address.substring(colonIndex + 1);
        Scanner scanner = new Scanner(portString);
        if (!scanner.hasNextInt()) return false;

        char[] ipCharArray = ipString.toCharArray();
        for (int i = 0; i < ipCharArray.length; i++) {
            if (ipCharArray[i] == '.') ipCharArray[i] = '\n';
        }
        char[] ipCharArrayCopy = new char[ipCharArray.length + 1];
        System.arraycopy(ipCharArray, 0, ipCharArrayCopy, 0, ipCharArray.length);
        ipCharArrayCopy[ipCharArray.length] = '\n';
        Scanner ipScanner = new Scanner(String.valueOf(ipCharArrayCopy));
        int tmp;
        for (int i = 0; i < 4; i++) {
            if (!ipScanner.hasNextInt()) return false;
            tmp = ipScanner.nextInt();
            if ((tmp > 255) || (tmp < 0)) return false;
        }

        return true;
    }

    public static boolean checkChar(char c) {
        char[] allowedChars = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', ':'};
        for (char allowedChar : allowedChars) {
            if (c == allowedChar) return true;
        }
        return false;
    }

}
