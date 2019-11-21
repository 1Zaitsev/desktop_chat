package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    Server server;
    Socket client;
    DataOutputStream out;
    DataInputStream in;

    String nick;
    List<String> blackList;

    public String getNick() {
        return nick;
    }

    public boolean checkBlackList(String nick){
        return blackList.contains(nick);
    }

    public ClientHandler(Server server, Socket client) {
        this.server = server;
        this.client = client;
        blackList = new ArrayList<>();

        try {
            this.in = new DataInputStream(client.getInputStream());
            this.out = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        String str = in.readUTF();
                        if(str.startsWith("/auth")){
                            String[] tokens = str.split(" ");
                            String tempNick = AuthService.getNickByLoginAndPassword(tokens[1], tokens[2]);
                            if(tempNick != null){
                                if(server.checkNick(tempNick)){
                                    sendMsg("/authFail: nick: " + tempNick +" is busy.");
                                }else {
                                    sendMsg("/authOK");
                                    nick = tempNick;
                                    server.subscribe(ClientHandler.this);
                                    break;
                                }
                            }
                        }else{
                            sendMsg("/authFail: Wrong login or password");
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if(str.startsWith("/")){
                            if (str.equals("/end")){
                                break;
                            }
                            if(str.startsWith("/w")) {
                                String[] tokens = str.split(" ", 3);
                                if (tokens.length >= 3) {
                                    server.privateMsg(ClientHandler.this, tokens[1], tokens[2]);
                                }
                            }
                            if(str.startsWith("/ignore")){
                                String[] tokens = str.split(" ");
                                blackList.add(tokens[1]);
                                sendMsg(tokens[1] + " added to black list.");
                            }
                            if(str.startsWith("/history")){
                                StringBuilder stringBuilder = AuthService.getChatHistory();
                                out.writeUTF(stringBuilder.toString());
                            }
                        }else {
                            server.broadCastMsg(ClientHandler.this,nick + ": " + str);
                            AuthService.saveHistory(nick, str);
                        }
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        server.unsubscribe(ClientHandler.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMsg(String msg) throws IOException {
        out.writeUTF(msg);
    }
}
