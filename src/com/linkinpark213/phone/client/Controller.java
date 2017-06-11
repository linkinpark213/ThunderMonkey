package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.receiver.ReceiverThread;
import com.linkinpark213.phone.common.Message;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Created by ooo on 2017/5/2 0002.
 */
public class Controller {
    private Text statusText;
    private Text localStatusText;
    private Button dialButton;
    private Button answerButton;
    private CallInListenerThread callInListenerThread;
    private Dialer dialer;
    private Conversation conversation;
    private Socket conversationSocket;
    private int currentState;
    public static final int IN_CONVERSATION = 0;
    public static final int WAITING_FOR_CALL = 1;
    public static final int WAITING_FOR_ANSWER = 2;

    public Controller(Text statusText,
                      Text localStatusText,
                      Button dialButton,
                      Button answerButton) {
        this.statusText = statusText;
        this.localStatusText = localStatusText;
        this.dialButton = dialButton;
        this.answerButton = answerButton;
        this.callInListenerThread = new CallInListenerThread(this);
        currentState = WAITING_FOR_CALL;
        callInListenerThread.start();
        this.dialer = new Dialer();
    }

    public void callIncoming(Socket socket) {
        statusText.setText("Call incoming from " + socket.getRemoteSocketAddress() + socket.getPort());
        conversationSocket = socket;
        dialButton.setDisable(true);
        answerButton.setDisable(false);
    }

    public void answerCall() {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(conversationSocket.getOutputStream());
            objectOutputStream.writeObject(new Message(Message.ANSWER, ""));
            startConversation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelCalling() {

    }

    public void hangOff() {
        currentState = WAITING_FOR_CALL;
        System.out.println("You hung off.");
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(conversationSocket.getOutputStream());
            objectOutputStream.writeObject(new Message(Message.HANG_OFF, ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void callingEnd() {
        currentState = WAITING_FOR_CALL;
    }


    public boolean isListening() {
        return currentState == WAITING_FOR_CALL;
    }

    public boolean isWaitingForAnswer() {
        return currentState == WAITING_FOR_ANSWER;
    }

    public boolean isInConversation() {
        return currentState == IN_CONVERSATION;
    }

    public void stopListening() {
        this.currentState = IN_CONVERSATION;
    }

    public void keepListening() {
        this.currentState = WAITING_FOR_CALL;
    }

    public void waitForAnswer(Socket socket) {
        this.currentState = WAITING_FOR_ANSWER;
        this.conversationSocket = socket;
        AnswerListenerThread answerListenerThread = new AnswerListenerThread(this);
        answerListenerThread.start();
    }

    public void startConversation() {
        conversation = new Conversation(conversationSocket);
        ConversationControlThread conversationControlThread = new ConversationControlThread(conversation, this);
        ReceiverThread receiverThread = new ReceiverThread(conversation, this);
        conversationControlThread.start();
        receiverThread.start();
        stopListening();
    }

    public void closeConversation() {

    }

    public Socket getConversationSocket() {
        return conversationSocket;
    }

    public int getCurrentState() {
        return currentState;
    }

    public Text getStatusText() {
        return statusText;
    }

    public Text getLocalStatusText() {
        return localStatusText;
    }

    public void setLocalStatus(String localStatus) {
        localStatusText.setText(localStatus);
    }

    public boolean dial(String address, int port) {
        Socket socket = dialer.dial(address, port);
        if (socket != null) {
            this.waitForAnswer(socket);
            return true;
        } else {
            System.out.println("Dialing Error: No Answerer.");
            statusText.setText("Dialing Error: No Answerer.");
            return false;
        }
    }
}
