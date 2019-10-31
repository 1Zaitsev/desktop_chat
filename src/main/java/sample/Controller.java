package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller{
    @FXML
    TextArea textArea;

    @FXML
    TextField textField;

    @FXML
    HBox authPanel;

    @FXML
    HBox bottomPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> clientList;

    private boolean isAuthorized;

    public void setAuthorized(boolean isAuthorized){
        this.isAuthorized = isAuthorized;

        if(isAuthorized){
            authPanel.setManaged(false);
            authPanel.setVisible(false);
            bottomPanel.setManaged(true);
            bottomPanel.setVisible(true);
            clientList.setManaged(true);
            clientList.setVisible(true);
        }else{
            authPanel.setManaged(true);
            authPanel.setVisible(true);
            bottomPanel.setManaged(false);
            bottomPanel.setVisible(false);
            clientList.setManaged(false);
            clientList.setVisible(false);
        }
    }

    Socket socket;
    DataOutputStream out;
    DataInputStream in;

    final private String IP = "localhost";
    final private int PORT = 8080;

    public void connect() throws IOException {

        socket = new Socket(IP, PORT);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authOK")) {
                            setAuthorized(true);
                            break;
                        } else {
                            textArea.appendText(str + "\n");
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/serverclose")) break;
                        if(str.startsWith("/clientlist")){
                            String[] tokens = str.split(" ");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientList.getItems().add(tokens[i]);
                                    }
                                }
                            });
                        }else {
                            textArea.appendText(str + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setAuthorized(false);
                }
            }
        }).start();

    }

    public void sendMessage(ActionEvent actionEvent)throws IOException{

        out.writeUTF(textField.getText());

        textField.clear();
        textField.requestFocus();
    }

    public void tryToAuth(ActionEvent actionEvent) throws IOException{
        if(socket == null || socket.isClosed()){
            connect();
        }
        out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());

        loginField.clear();
        passwordField.clear();
    }


    public void dispose() throws IOException {
        if(out != null) {
            out.writeUTF("/end");
        }
    }
}
