import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class ServerService {
    String botNickname = "Bot";
    LinkedList<ClientThread> clientThreads = new LinkedList<>();

    public void run(int port) throws IOException {
        System.out.println("Server is working!");
        ServerSocket serverSocket = new ServerSocket(port);
        BotService bot = new BotService();
        bot.run(serverSocket.getInetAddress(), port);
        try {
            Socket clientSocket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(clientSocket, botNickname);
            clientThread.start();
            clientThreads.add(clientThread);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            ioe.printStackTrace();

        }
        System.out.println("Bot joined!");

        int i = 1;
        String generatedNickname;
        while (true) {
            generatedNickname = "User_" + i;
            i++;
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket, generatedNickname);
                clientThread.start();
                clientThreads.add(clientThread);
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                ioe.printStackTrace();

            }
            System.out.println("User joined!");
        }

    }

    class ClientThread extends Thread {
        String nickname;
        Socket socket;
        String clientCommand = null;
        boolean status = true;
        BufferedReader in;
        PrintWriter out;

        ClientThread(Socket s, String nickname) {
            this.nickname = nickname;
            socket = s;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            out.println("@name " + this.nickname);
            out.println("Welcome to the chat! Use '@name YOUR_NICKNAME' to change your nickname, " +
                    "send a message to everyone by typing it and pressing ENTER, or use '@quit' to exit. " +
                    "You can also use @senduser USER_NICKNAME MESSAGE to send a message to a single user");
            clientCommand = null;
            while (status) {
                try {
                    clientCommand = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (byeCheck(clientCommand)) {
                    status = false;
                } else {
                    if (!changeNicknameCheck(clientCommand)) {
                        if (!sendUserCheck(this.nickname, clientCommand)) sendMessageAll(nickname, clientCommand);
                    }
                }
            }
        }


        public void sendMessageAllThread(String nickname, String message) {
            if (!Objects.equals(this.nickname, nickname)) {
                if (!Objects.equals(botNickname, this.nickname)) {
                    out.println(nickname + " : " + message);
                }
            }
        }

        public void sendMessageThread(String senderNickname, String nickname, String message) {
            if (Objects.equals(this.nickname, nickname)) {
                if (!Objects.equals(botNickname, this.nickname)) {
                    out.println(senderNickname + " : " + message);
                } else {
                    out.println(message);
                }
            }
        }

        public boolean byeCheck(String message) {
            if (Objects.equals(message, "@quit")) {
                sendMessageAll(nickname, "Bye everyone!");
                return true;
            } else {
                return false;
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

        public void changeNickname(char[] sentenceChar, int tagLength) {
            char[] nicknameChar = new char[sentenceChar.length - tagLength - 1];
            int j = 0;
            for (int i = tagLength + 1; i <= sentenceChar.length - 1; i++) {
                nicknameChar[j] = sentenceChar[i];
                j++;
            }

            for (int i = 0; i < nicknameChar.length; i++) {
                if (nicknameChar[i] == '@') {
                    out.println("You are not allowed to use '@' in your nickname! Think of another one.");
                    return;
                }
            }
            this.nickname = new String(nicknameChar);
            out.println("@name " + this.nickname);
        }

    }


    public void sendMessage(String senderNickname, String nickname, String message) {
        for (int i = 0; i < clientThreads.size(); i++) {
            if (clientThreads.get(i).isAlive()) {
                clientThreads.get(i).sendMessageThread(senderNickname, nickname, message);
            }
        }
    }

    public void sendMessageAll(String nickname, String message) {
        for (int i = 0; i < clientThreads.size(); i++) {
            if (clientThreads.get(i).isAlive()) {
                clientThreads.get(i).sendMessageAllThread(nickname, message);
            }
        }
    }

    public boolean sendUserCheck(String senderNickname, String clientCommand) {
        char[] tagOriginal = {'@', 's', 'e', 'n', 'd', 'u', 's', 'e', 'r', ' '};
        if (clientCommand.length() > tagOriginal.length) {
            char[] sentenceChar = clientCommand.toCharArray();
            char[] sentenceTag = new char[tagOriginal.length];
            for (int i = 0; i <= sentenceTag.length - 1; i++) {
                sentenceTag[i] = sentenceChar[i];
            }
            int spaceIndex = 0;
            if (Arrays.equals(sentenceTag, tagOriginal)) {
                for (int i = sentenceChar.length - 1; i >= tagOriginal.length; i--) {
                    if (sentenceChar[i] == ' ') spaceIndex = i;
                }
                char[] nicknameChar = new char[sentenceChar.length - tagOriginal.length - (sentenceChar.length - spaceIndex)];
                int j = 0;
                for (int i = tagOriginal.length; i <= spaceIndex - 1; i++) {
                    nicknameChar[j] = sentenceChar[i];
                    j++;
                }
                char[] messageChars = new char[sentenceChar.length - spaceIndex - 1];
                int g = 0;
                for (int i = spaceIndex + 1; i < sentenceChar.length; i++) {
                    messageChars[g] = sentenceChar[i];
                    g++;
                }
                sendMessage(senderNickname, new String(nicknameChar), new String(messageChars));
                return true;
            }
            return false;
        }
        return false;
    }
}

