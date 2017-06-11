package com.linkinpark213.phone.client.receiver;

import com.linkinpark213.phone.client.Controller;
import com.linkinpark213.phone.client.Conversation;
import com.linkinpark213.phone.common.Message;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;

/**
 * Created by ooo on 2017/4/30 0030.
 */
public class ReceiverThread extends Thread {
    private Conversation conversation;
    private Controller controller;
    private Socket socket;

    public ReceiverThread(Conversation conversation, Controller controller) {
        this.conversation = conversation;
        this.controller = controller;
        this.socket = conversation.getSocket();
        System.out.println("Receiver Listening...");
    }

    public void messageIncoming(Message message) {
        /*
         * Receive the message and play the sound according to the data.
         */
        switch (message.getType()) {
            case Message.HANG_OFF:
            default:
                controller.callingEnd();
                System.out.println("Phone Call Was Hung Off.");
        }
    }

    @Override
    public void run() {
        try {
            while (controller.isInConversation()) {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) inputStream.readObject();
                messageIncoming(message);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

}
