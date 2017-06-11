package com.linkinpark213.phone.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.net.Socket;
import java.util.regex.Pattern;

public class Main extends Application {
    private Conversation conversation;
    private ConversationControlThread conversationControlThread;
    private Controller controller;
    private Socket socket;
    private Text[] statusTexts;
    private Text localStatusText;
    private TextArea ipEdit;
    private TextArea portEdit;
    private VBox waitingState;
    private Scene waitingScene;
    //    private VBox conversationState;
//    private VBox waitingForAnswerState;
//    private VBox callIncomingState;
//    private Scene conversationScene;
//    private Scene waitingForAnswerScene;
//    private Scene callIncomingScene;

    public Main() {
        Font stateFont = new Font(15);
        ipEdit = new TextArea();
        portEdit = new TextArea();
        ipEdit.setFont(stateFont);
        portEdit.setFont(stateFont);
        statusTexts = new Text[3];
        for (int i = 0; i < statusTexts.length; i++) {
            statusTexts[i] = new Text();
            statusTexts[i].setFont(stateFont);
        }
        localStatusText = new Text();
        localStatusText.setFont(stateFont);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        waitingState = new VBox();
        waitingState.setAlignment(Pos.CENTER);
//        conversationState = new VBox();
//        waitingForAnswerState = new VBox();
//        conversationState.setAlignment(Pos.CENTER);
//        waitingForAnswerState.setAlignment(Pos.CENTER);

        ipEdit.setPrefRowCount(0);
        ipEdit.setPromptText("Enter the IP address");
        ipEdit.setText("127.0.0.1");
        portEdit.setPrefRowCount(0);
        portEdit.setPromptText("Port");

        Image dialImage = new Image(new FileInputStream("icon\\call.png"));
        Image hangImage = new Image(new FileInputStream("icon\\hang.png"));

        GridPane gridPane = new GridPane();
        Button callButton = new Button("", new ImageView(dialImage));
        Button hangButton = new Button("", new ImageView(hangImage));
//        Button answerButton = new Button("Answer", new ImageView(dialImage));
//        Button cancelButton = new Button("Cancel", new ImageView(hangImage));

        Font buttonFont = new Font(25);

        callButton.setFont(buttonFont);
        hangButton.setFont(buttonFont);
//        answerButton.setFont(buttonFont);
//        cancelButton.setFont(buttonFont);

        callButton.setPrefSize(200, 50);
        hangButton.setPrefSize(200, 50);
        hangButton.setDisable(true);
        gridPane.add(callButton, 0, 0);
        gridPane.add(hangButton, 1, 0);
//        answerButton.setPrefSize(200, 50);
//        answerButton.setDisable(true);
//        gridPane.add(answerButton, 1, 0);
//        cancelButton.setPrefSize(400, 80);

        waitingState.getChildren().add(ipEdit);
        waitingState.getChildren().add(portEdit);
        waitingState.getChildren().add(gridPane);
        waitingState.getChildren().add(statusTexts[0]);
        waitingState.getChildren().add(localStatusText);

//        conversationState.getChildren().add(statusTexts[1]);
//        conversationState.getChildren().add(hangButton);
//
//        waitingForAnswerState.getChildren().add(statusTexts[2]);
//        waitingForAnswerState.getChildren().add(cancelButton);

        waitingScene = new Scene(waitingState, 400, 300);
//        conversationScene = new Scene(conversationState, 400, 300);
//        waitingForAnswerScene = new Scene(waitingForAnswerState, 400, 300);

        this.controller = new Controller(
                statusTexts,
                localStatusText,
                callButton,
                hangButton);

        callButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(controller.isListening()) {
                    String ipString = ipEdit.getText();
                    String portString = portEdit.getText();
                    if (Pattern.matches("\\d{1,3}\\.\\d{1,3}.\\d{1,3}.\\d{1,3}", ipString)
                            && Pattern.matches("\\d{4,5}", portString)) {
                        boolean isWaitingForAnswer = controller.dial(ipEdit.getText(), Integer.parseInt(portEdit.getText()));
                        if (isWaitingForAnswer) {
                            System.out.println("Dialing...");
//                            primaryStage.setScene(waitingForAnswerScene);
                        }
                    } else System.out.println("Invalid IP or Port.");
                } else if(controller.isBeingCalled()) {
//                    primaryStage.setScene(conversationScene);
                    for (int i = 0; i < statusTexts.length; i++) {
                        statusTexts[i].setText("Answering Call.");
                    }
                    controller.answerCall();
                }
            }
        });

        hangButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(controller.isInConversation()) {
                    primaryStage.setScene(waitingScene);
                    for (int i = 0; i < statusTexts.length; i++) {
                        statusTexts[i].setText("You Hung Off.");
                    }
                    controller.hangOff();
                } else if (controller.isWaitingForAnswer()) {
                    primaryStage.setScene(waitingScene);
                    controller.cancelDialing();
                }
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
