package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;


public class Server {
    Vector<ClientHandler> clients;

    public Server() {
        this.clients = new Vector<>();

        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connection();
            if(AuthService.getNickByLoginAndPassword("login1","pass1") != null){
                System.out.println("Data base has connected.");
            }
            server = new ServerSocket(8080);
            System.out.println("Server is running...");

            while (true){
                socket = server.accept();
                new ClientHandler(this, socket);
            }

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                AuthService.disconnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribe(ClientHandler client) throws IOException {
        clients.add(client);
        System.out.println("Client's connected");
        broadCastClientList();
    }

    public void unsubscribe(ClientHandler client) throws IOException {
        clients.remove(client);
        System.out.println("Client's disconnected");
        broadCastClientList();
    }

    public void broadCastMsg(ClientHandler from, String msg) throws IOException {
        for (ClientHandler c: clients) {
            if(!c.checkBlackList(from.getNick()))
            c.sendMsg(msg);
        }
    }

    public boolean checkNick(String nick){
        for(ClientHandler c: clients){
            if(c.getNick().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public void privateMsg(ClientHandler client, String nick, String msg) throws IOException {

        for (ClientHandler c : clients) {
            if (c.getNick().equals(nick)) {
                c.sendMsg("Private message from " + client.getNick() + ": " + msg);
                client.sendMsg("Private message to " + nick + ": " + msg);
                return;
            }
        }
        client.sendMsg("/wFail: There is't " + nick + " in this chat");
    }

    public void broadCastClientList() throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("/clientlist ");
        for (ClientHandler c: clients) {
            sb.append(c.getNick() + " ");
        }
        String out = sb.toString();

        for (ClientHandler c: clients) {
            c.sendMsg(out);
        }
    }
}
