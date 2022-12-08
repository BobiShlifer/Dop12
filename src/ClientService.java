import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientService {
    BufferedReader in;
    BufferedReader stdIn;
    PrintWriter out;
    Socket socket;
    String nickname;

    public void run(String hostName, int portNumber) {


        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            Listener listenerThread = new Listener();
            Sender senderThread = new Sender();
            listenerThread.start();
            senderThread.start();
            senderThread.join();
            System.exit(0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Listener extends Thread {
        public void run() {
            while (true) {
                try {
                    String inString = in.readLine();
                    if (!changeNicknameCheck(inString)) System.out.println(inString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Sender extends Thread {
        public void run() {
            boolean status = true;
            while (status) {
                String fromUser = null;
                try {
                    fromUser = stdIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fromUser != null) {
                    if (fromUser.equals("@quit")) {
                        out.println("@quit");
                        status = false;
                    } else {
                        if ((!changeNicknameCheckSimple(fromUser)) && (!sendUserCheckSimple(fromUser))) {
                            System.out.println(nickname + " : " + fromUser);
                        }
                        out.println(fromUser);
                    }
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
                this.changeNickname(sentenceChar, tagOriginal.length);
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean changeNicknameCheckSimple(String clientCommand) {
        char[] tagOriginal = {'@', 'n', 'a', 'm', 'e'};
        if (clientCommand.length() > (tagOriginal.length + 1)) {
            char[] sentenceChar = clientCommand.toCharArray();
            char[] sentenceTag = new char[tagOriginal.length];
            for (int i = 0; i <= sentenceTag.length - 1; i++) {
                sentenceTag[i] = sentenceChar[i];
            }
            return Arrays.equals(sentenceTag, tagOriginal);
        }
        return false;
    }

    public void changeNickname(char[] sentenceChar, int tagLength) {
        char[] nicknameChar = new char[sentenceChar.length - tagLength - 1];
        int j = 0;
        for (int i = tagLength + 1; i <= sentenceChar.length - 1; i++) {
            nicknameChar[j] = sentenceChar[i];
            j++;
        }
        this.nickname = new String(nicknameChar);
        System.out.println("Your nickname is changed successfully!");
    }

    public boolean sendUserCheckSimple(String clientCommand) {
        char[] tagOriginal = {'@', 's', 'e', 'n', 'd', 'u', 's', 'e', 'r', ' '};
        if (clientCommand.length() > tagOriginal.length) {
            char[] sentenceChar = clientCommand.toCharArray();
            char[] sentenceTag = new char[tagOriginal.length];
            for (int i = 0; i <= sentenceTag.length - 1; i++) {
                sentenceTag[i] = sentenceChar[i];
            }
            return Arrays.equals(sentenceTag, tagOriginal);
        }
        return false;
    }
}
