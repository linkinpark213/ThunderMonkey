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
    private Text[] statusTexts;
    private Text localStatusText;
    private Button callButton;
    private Button hangButton;
    private CallInListenerThread callInListenerThread;
    private Dialer dialer;
    private Conversation conversation;
    private Socket conversationSocket;
    private int currentState;
    public static final int IN_CONVERSATION = 0;
    public static final int WAITING_FOR_CALL = 1;
    public static final int WAITING_FOR_ANSWER = 2;
    public static final int CALL_INCOMING = 3;

    public Controller(Text[] statusTexts,
                      Text localStatusText,
                      Button callButton,
                      Button hangButton) {
        this.statusTexts = statusTexts;
        this.localStatusText = localStatusText;
        this.callButton = callButton;
        this.hangButton = hangButton;
        this.callInListenerThread = new CallInListenerThread(this);
        currentState = WAITING_FOR_CALL;
        callInListenerThread.start();
        this.dialer = new Dialer();
    }

    public void callIncoming(Socket socket) {
        for (int i = 0; i < statusTexts.length; i++) {
            statusTexts[i].setText("Call incoming from " + socket.getRemoteSocketAddress() + socket.getPort());
        }
        this.currentState = CALL_INCOMING;
        conversationSocket = socket;
    }

    public void answerCall() {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(conversationSocket.getOutputStream());
            objectOutputStream.writeObject(new Message(Message.ANSWER, ""));
            startConversation();
            callButton.setDisable(true);
            hangButton.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelDialing() {
        if (this.currentState == IN_CONVERSATION) {
            hangOff();
        } else
            this.currentState = WAITING_FOR_CALL;
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
        callButton.setDisable(false);
        hangButton.setDisable(true);
    }

    public void callingEnd() {
        currentState = WAITING_FOR_CALL;
        callButton.setDisable(false);
        hangButton.setDisable(true);
    }


    public boolean isListening() {
        return currentState == WAITING_FOR_CALL;
    }

    public boolean isBeingCalled() {
        return currentState == CALL_INCOMING;
    }

    public boolean isWaitingForAnswer() {
        return currentState == WAITING_FOR_ANSWER;
    }

    public boolean isInConversation() {
        return currentState == IN_CONVERSATION;
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
        this.currentState = IN_CONVERSATION;
        callButton.setDisable(true);
        hangButton.setDisable(false);
    }

    public Socket getConversationSocket() {
        return conversationSocket;
    }

    public int getCurrentState() {
        return currentState;
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
            for (int i = 0; i < statusTexts.length; i++) {
                statusTexts[i].setText("Dialing Error: No Answerer.");
            }
            return false;
        }
    }
}
