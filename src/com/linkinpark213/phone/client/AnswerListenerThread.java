package com.linkinpark213.phone.client;

import com.linkinpark213.phone.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by ooo on 2017/6/11 0011.
 */
public class AnswerListenerThread extends Thread {
    private Controller controller;
    private Socket socket;

    public AnswerListenerThread(Controller controller) {
        this.controller = controller;
        this.socket = controller.getConversationSocket();
    }

    @Override
    public void run() {
        while (controller.isWaitingForAnswer()) {
            /*
            Waiting for answer
             */
            try {
                System.out.println("Waiting for answer...");
                socket = controller.getConversationSocket();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();
                if (message.getType() == Message.ANSWER) {
                    System.out.println("Connection Established With " + socket.getRemoteSocketAddress());
                    System.out.println("" + socket.getRemoteSocketAddress() + " Answered.");
                    System.out.println("Public Key is: " + message.getPublicKey());
                    controller.setPeerPublicKey(message.getPublicKey());
                    controller.setRemoteDatagramPort(message.getDatagramPort());
                    controller.startConversation();
                } else if (message.getType() == Message.CALL_REFUSE) {
                    System.out.println("" + socket.getRemoteSocketAddress() + " Refused to Answer.");
                    controller.setStatus("" + socket.getRemoteSocketAddress() + " Refused to Answer.");
                    socket.close();
                    controller.callingRefused();
                }
            } catch (IOException e) {
                System.out.println("Socket Closed.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
