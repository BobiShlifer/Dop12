import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class BotService {
    long period = 20000;

    BufferedReader in;
    BufferedReader stdIn;
    PrintWriter out;
    Socket socket;

    public void run(InetAddress hostName, int portNumber) {

        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            Listener listenerThread = new Listener();
            Sender senderThread = new Sender();
            listenerThread.start();
            senderThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Listener extends Thread {
        public void run() {
            while (true) {
                try {
                    String inString = in.readLine();
                    if (!changeNicknameCheck(inString)) out.println(inString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Sender extends Thread {
        public void run() {
            boolean status = true;
            long currentTime;
            long startTime = System.currentTimeMillis();
            while (status) {
                currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= period) {
                    LocalDateTime datetime = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                    out.println("Current time: " + datetime);
                    startTime = System.currentTimeMillis();
                }
            }
        }

    }

    public boolean changeNicknameCheck(String clientCommand) {
        char[] tagOriginal = {'@', 'n', 'a', 'm', 'e'};
        if (clientCommand.length() > (tagOriginal.length + 1)) {
            char[] sentenceChar = clientCommand.toCharArray();
            char[] sentenceTag = new char[tagOriginal.length];
            for (int i = 0; i <= sentenceTag.length - 1; i++) {
                sentenceTag[i] = sentenceChar[i];
            }
            if (Arrays.equals(sentenceTag, tagOriginal)) {
                return true;
            }
            return false;
        }
        return false;
    }

}
