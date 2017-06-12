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
    private Text statusText;
    private Text localStatusText;
    private TextArea ipEdit;
    private TextArea portEdit;
    private VBox waitingState;
    private Scene waitingScene;

    public Main() {
        Font stateFont = new Font(15);
        ipEdit = new TextArea();
        portEdit = new TextArea();
        ipEdit.setFont(stateFont);
        portEdit.setFont(stateFont);
        statusText = new Text();
        statusText.setFont(stateFont);
        localStatusText = new Text();
        localStatusText.setFont(stateFont);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        waitingState = new VBox();
        waitingState.setAlignment(Pos.CENTER);

        ipEdit.setPrefRowCount(0);
        ipEdit.setPromptText("Enter the IP address of the Callee");
        ipEdit.setText("127.0.0.1");
        portEdit.setPrefRowCount(0);
        portEdit.setPromptText("Enter the Control Port of the Callee");

        Image callImage = new Image(new FileInputStream("icon\\call.png"));
        Image hangImage = new Image(new FileInputStream("icon\\hang.png"));
        ImageView dialImageView = new ImageView(callImage);
        ImageView hangImageView = new ImageView(hangImage);
        dialImageView.setFitWidth(80);
        dialImageView.setFitHeight(80);
        hangImageView.setFitWidth(80);
        hangImageView.setFitHeight(80);

        GridPane gridPane = new GridPane();
        Button callButton = new Button("", dialImageView);
        Button hangButton = new Button("", hangImageView);

        Font buttonFont = new Font(25);

        callButton.setFont(buttonFont);
        hangButton.setFont(buttonFont);

        callButton.setPrefSize(200, 50);
        hangButton.setPrefSize(200, 50);
        hangButton.setDisable(true);
        gridPane.add(callButton, 0, 0);
        gridPane.add(hangButton, 1, 0);
        waitingState.getChildren().add(ipEdit);
        waitingState.getChildren().add(portEdit);
        waitingState.getChildren().add(gridPane);
        waitingState.getChildren().add(statusText);
        waitingState.getChildren().add(localStatusText);

        waitingScene = new Scene(waitingState, 400, 300);

        this.controller = new Controller(
                statusText,
                localStatusText,
                callButton,
                hangButton);

        callButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (controller.isListening()) {
                    String ipString = ipEdit.getText();
                    String portString = portEdit.getText();
                    if (Pattern.matches("\\d{1,3}\\.\\d{1,3}.\\d{1,3}.\\d{1,3}", ipString)
                            && Pattern.matches("\\d{4,5}", portString)) {
                        if (ipString.equals("127.0.0.1") && controller.getServerSocket().getLocalPort() == Integer.parseInt(portString)) {
                            statusText.setText("Please Do Not Try to Call Yourself.");
                        } else {
                            boolean isWaitingForAnswer = controller.dial(ipEdit.getText(), Integer.parseInt(portEdit.getText()));
                            if (isWaitingForAnswer) {
                                statusText.setText("Dialing..." + ipString + ":" + portString);
                                System.out.println("Dialing..." + ipString + ":" + portString);
                            }
                        }
                    } else {
                        statusText.setText("Invalid IP or Port.");
                        System.out.println("Invalid IP or Port.");
                    }
                } else if (controller.isBeingCalled()) {
                    statusText.setText("Answering Call.");
                    controller.answerCall();
                }
            }
        });

        hangButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (controller.isInConversation()) {
                    controller.hangOff();
                } else if (controller.isWaitingForAnswer()) {
                    controller.cancelDialing();
                } else if (controller.isBeingCalled()) {
                    controller.refuseToAnswer();
                }
            }
        });

        primaryStage.setTitle("Thunder Monkey");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (controller.isBeingCalled()) {
                    controller.refuseToAnswer();
                } else if (controller.isWaitingForAnswer()) {
                    controller.cancelDialing();
                } else if (controller.isInConversation()) {
                    controller.hangOff();
                }
                System.exit(0);
            }
        });
        primaryStage.getIcons().add(callImage);
        primaryStage.setScene(waitingScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
