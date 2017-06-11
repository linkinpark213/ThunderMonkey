package com.linkinpark213.phone.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.Socket;
import java.util.regex.Pattern;

public class Main extends Application {
    private Conversation conversation;
    private ConversationControlThread conversationControlThread;
    private Controller controller;
    private Socket socket;
    private Text statusText;
    private Text localStatusText;
    private TextArea ipEdit;
    private TextArea portEdit;
    private VBox waitingState;
    private VBox conversationState;
    private VBox waitingForAnswerState;
    private VBox callIncomingState;
    private Scene waitingScene;
    private Scene conversationScene;
    private Scene waitingForAnswerScene;
    private Scene callIncomingScene;

    public Main() {
        ipEdit = new TextArea();
        portEdit = new TextArea();
        statusText = new Text();
        localStatusText = new Text();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        waitingState = new VBox();
        conversationState = new VBox();
        waitingForAnswerState = new VBox();
        waitingState.setAlignment(Pos.CENTER);
        conversationState.setAlignment(Pos.CENTER);
        waitingForAnswerState.setAlignment(Pos.CENTER);

        ipEdit.setPrefRowCount(0);
        ipEdit.setPromptText("Enter the IP address");
        ipEdit.setText("127.0.0.1");
        portEdit.setPrefRowCount(0);
        portEdit.setPromptText("Port");

        GridPane gridPane = new GridPane();
        Button dialButton = new Button("Dial");
        Button answerButton = new Button("Answer");
        Button hangOffButton = new Button("Hang Off");
        Font buttonFont = new Font(30);
        dialButton.setFont(buttonFont);
        answerButton.setFont(buttonFont);
        hangOffButton.setFont(buttonFont);
        dialButton.setPrefSize(200, 50);
        answerButton.setPrefSize(200, 50);
        answerButton.setDisable(true);
        hangOffButton.setPrefSize(400,80);
        gridPane.add(dialButton, 0, 0);
        gridPane.add(answerButton, 1, 0);

        waitingState.getChildren().add(ipEdit);
        waitingState.getChildren().add(portEdit);
        waitingState.getChildren().add(gridPane);
        waitingState.getChildren().add(statusText);
        waitingState.getChildren().add(localStatusText);

        conversationState.getChildren().add(statusText);
        conversationState.getChildren().add(hangOffButton);

        waitingForAnswerState.getChildren().add(statusText);
        waitingForAnswerState.getChildren().add(hangOffButton);

        waitingScene = new Scene(waitingState, 400, 300);
        conversationScene = new Scene(conversationState, 400, 300);
        waitingForAnswerScene = new Scene(waitingForAnswerState, 400, 300);

        this.controller = new Controller(
                statusText,
                localStatusText,
                dialButton,
                answerButton);

        dialButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String ipString = ipEdit.getText();
                String portString = portEdit.getText();
                if (Pattern.matches("\\d{1,3}\\.\\d{1,3}.\\d{1,3}.\\d{1,3}", ipString)
                        && Pattern.matches("\\d{4,5}", portString)) {
                    boolean isWaitingForAnswer = controller.dial(ipEdit.getText(), Integer.parseInt(portEdit.getText()));
                    if (isWaitingForAnswer) {
                        System.out.println("Dialing...");
                        primaryStage.setScene(waitingForAnswerScene);
                    }
                } else System.out.println("Invalid IP or port.");
            }
        });

        hangOffButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(waitingScene);
                statusText.setText("You Hung off.");
                controller.hangOff();
            }
        });

        answerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(conversationScene);
                statusText.setText("Answering call.");
                controller.answerCall();
            }
        });

        primaryStage.setTitle("Thunder Monkey");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        primaryStage.setScene(waitingScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
