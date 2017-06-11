package com.linkinpark213.phone.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.Socket;

public class Main extends Application {
    private Conversation conversation;
    private ConversationControlThread conversationControlThread;
    private Controller controller;
    private Socket socket;
    private Text statusText;
    private Text localStatusText;
    private TextArea ipEdit;
    private TextArea portEdit;

    public Main() {
        ipEdit = new TextArea();
        portEdit = new TextArea();
        statusText = new Text();
        localStatusText = new Text();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox waitingState = new VBox();
        VBox conversationState = new VBox();

        ipEdit.setPrefRowCount(0);
        ipEdit.setPromptText("Enter the IP address");
        ipEdit.setText("127.0.0.1");
        portEdit.setPrefRowCount(0);
        portEdit.setPromptText("Port");

        Button dialButton = new Button("Dial");
        Button answerButton = new Button("Answer");
        Button hangOffButton = new Button("Hang Off");

        waitingState.getChildren().add(statusText);
        waitingState.getChildren().add(localStatusText);
        waitingState.getChildren().add(ipEdit);
        waitingState.getChildren().add(portEdit);
        waitingState.getChildren().add(dialButton);

        conversationState.getChildren().add(statusText);
        conversationState.getChildren().add(hangOffButton);

        Scene waitingScene = new Scene(waitingState, 400, 300);
        Scene conversationScene = new Scene(conversationState, 400, 300);


        this.controller = new Controller(primaryStage,
                waitingScene,
                conversationScene,
                statusText,
                localStatusText);

        dialButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String ipString = ipEdit.getText();
                String portString = portEdit.getText();
                if(portEdit.getText().length() > 1)
                controller.dial(ipEdit.getText(), Integer.parseInt(portEdit.getText()));
            }
        });

        hangOffButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                conversation.hangOff();
//                listener.setKeepListening(true);
                primaryStage.setScene(waitingScene);
            }
        });

        answerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                listener.setKeepListening(false);
                primaryStage.setScene(waitingScene);
            }
        });

        primaryStage.setTitle("Phone Chatting");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
//                listener.stop();
                System.exit(0);
            }
        });
        primaryStage.setScene(waitingScene);
        primaryStage.show();

//        listener.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
