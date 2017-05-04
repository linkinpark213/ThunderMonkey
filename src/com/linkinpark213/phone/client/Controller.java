package com.linkinpark213.phone.client;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.Socket;


/**
 * Created by ooo on 2017/5/2 0002.
 */
public class Controller {
    private Stage primaryStage;
    private Scene waitingScene;
    private Scene conversationScene;
    private Text statusText;
    private Text localStatusText;
    private Listener listener;
    private Dialer dialer;
    private Conversation conversation;

    public Controller(Stage primaryStage,
                      Scene waitingScene,
                      Scene conversationScene,
                      Text localStatusText,
                      Text statusText) {
        this.primaryStage = primaryStage;
        this.waitingScene = waitingScene;
        this.conversationScene = conversationScene;
        this.statusText = statusText;
        this.localStatusText = localStatusText;
        this.listener = new Listener(this);
        this.dialer = new Dialer();
    }

    public void answerCall() {

    }

    public Text getStatusText() {
        return statusText;
    }

    public Text getLocalStatusText() {
        return localStatusText;
    }

    public void dial(String address, int port) {
        Socket socket = dialer.dial(address, port);
        if(socket != null) {
            listener.setKeepListening(false);
            primaryStage.setScene(conversationScene);
            conversation = new Conversation(socket, listener);
        } else {
            System.out.println("Dialing Error: No Answerer.");
            statusText.setText("Dialing Error: No Answerer.");
        }
    }
}
