package com.linkinpark213.phone.client;

import com.linkinpark213.phone.common.Message;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by ooo on 2017/5/2 0002.
 */
public class Controller {
    private Text statusText;
    private Text localStatusText;
    private Button callButton;
    private Button hangButton;
    private Dialer dialer;
    private Conversation conversation;
    private Socket conversationSocket;
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private AnswerListenerThread answerListenerThread;
    private int currentState;
    private int remoteDatagramPort;
    public static final int IN_CONVERSATION = 0;
    public static final int WAITING_FOR_CALL = 1;
    public static final int WAITING_FOR_ANSWER = 2;
    public static final int CALL_INCOMING = 3;

    public Controller(Text statusText,
                      Text localStatusText,
                      Button callButton,
                      Button hangButton) {
        this.statusText = statusText;
        this.localStatusText = localStatusText;
        this.callButton = callButton;
        this.hangButton = hangButton;
        CallInListenerThread callInListenerThread = new CallInListenerThread(this);
        currentState = WAITING_FOR_CALL;
        callInListenerThread.start();
        this.dialer = new Dialer();
    }

    public void callIncoming(Socket socket, int datagramPort) {
        remoteDatagramPort = datagramPort;
        statusText.setText("Call incoming from " + socket.getRemoteSocketAddress()
                + "\nRemote Datagram Socket is " + datagramPort);
        this.currentState = CALL_INCOMING;
        conversationSocket = socket;
        CallerCancelListenerThread callerCancelListenerThread = new CallerCancelListenerThread(this);
        callerCancelListenerThread.start();
        hangButton.setDisable(false);
    }

    public void answerCall() {
        statusText.setText("You Answered the Phone.");
        System.out.println("You Answered the Phone.");
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(conversationSocket.getOutputStream());
            objectOutputStream.writeObject(new Message(Message.ANSWER, datagramSocket.getLocalPort()));
            startConversation();
            callButton.setDisable(true);
            hangButton.setDisable(false);
        } catch (IOException e) {
            statusText.setText("The Other User Canceled Calling.");
            System.out.println("The Other User Canceled Calling.");
            currentState = WAITING_FOR_CALL;
            callButton.setDisable(false);
            hangButton.setDisable(true);
        }
    }

    public void cancelDialing() {
        if (this.currentState == IN_CONVERSATION) {
            hangOff();
        } else {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(conversationSocket.getOutputStream());
                objectOutputStream.writeObject(new Message(Message.CALL_CANCEL, ""));
                conversationSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.currentState = WAITING_FOR_CALL;
            statusText.setText("You Canceled Calling.");
            System.out.println("You Canceled Calling.");
            callButton.setDisable(false);
            hangButton.setDisable(true);
        }
    }

    public void hangOff() {
        currentState = WAITING_FOR_CALL;
        statusText.setText("You Hung Off.");
        System.out.println("You Hung Off.");
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(conversationSocket.getOutputStream());
            objectOutputStream.writeObject(new Message(Message.HANG_OFF, ""));
            conversationSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callButton.setDisable(false);
        hangButton.setDisable(true);
    }

    public void callingEnd() {
        currentState = WAITING_FOR_CALL;
        statusText.setText("The Other User Hung Off.");
        System.out.println("The Other User Hung Off.");
        try {
            conversationSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callButton.setDisable(false);
        hangButton.setDisable(true);
    }

    public void refuseToAnswer() {
        statusText.setText("You Refused to Answer.");
        System.out.println("You Refused to Answer.");
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(conversationSocket.getOutputStream());
            objectOutputStream.writeObject(new Message(Message.CALL_REFUSE, ""));
            conversationSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentState = WAITING_FOR_CALL;
        hangButton.setDisable(true);
    }

    public void waitForCall() {
        this.currentState = WAITING_FOR_CALL;
        callButton.setDisable(false);
        hangButton.setDisable(true);
    }

    public void callingRefused() {
        waitForCall();
    }

    public void waitForAnswer(Socket socket) {
        this.currentState = WAITING_FOR_ANSWER;
        this.conversationSocket = socket;
        answerListenerThread = new AnswerListenerThread(this);
        answerListenerThread.start();
        callButton.setDisable(true);
        hangButton.setDisable(false);
    }

    public void startConversation() {
        this.currentState = IN_CONVERSATION;
        conversation = new Conversation(conversationSocket, remoteDatagramPort);
        ConversationControlThread conversationControlThread = new ConversationControlThread(conversation, this, remoteDatagramPort);
        conversationControlThread.start();
        DatagramReceiverThread datagramReceiverThread = new DatagramReceiverThread(conversation, this, conversationSocket, datagramSocket);
        datagramReceiverThread.start();
        DatagramSenderThread datagramSenderThread = new DatagramSenderThread(conversation, this, remoteDatagramPort);
        datagramSenderThread.start();

        statusText.setText("Conversation Established with " + conversationSocket.getRemoteSocketAddress());
        System.out.println("Conversation Established with " + conversationSocket.getRemoteSocketAddress());
        callButton.setDisable(true);
        hangButton.setDisable(false);
    }

    public void setStatus(String status) {
        statusText.setText(status);
    }

    public Socket getConversationSocket() {
        return conversationSocket;
    }

    public void setLocalStatus(String localStatus) {
        localStatusText.setText(localStatus);
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
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

    public int getRemoteDatagramPort() {
        return remoteDatagramPort;
    }

    public void setRemoteDatagramPort(int remoteDatagramPort) {
        this.remoteDatagramPort = remoteDatagramPort;
    }

    public boolean dial(String address, int port) {
        Socket socket = dialer.dial(address, port, datagramSocket.getLocalPort());
        if (socket != null) {
            System.out.println("Local Address & Port: " + socket.getLocalAddress() + ":" + socket.getLocalPort());
            this.waitForAnswer(socket);
            return true;
        } else {
            System.out.println("Dialing Error: Remote Answerer Address Not Found.");
            statusText.setText("Dialing Error: Remote Answerer Address Not Found.");
            return false;
        }
    }
}
