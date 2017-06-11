package com.linkinpark213.phone.client;

import com.linkinpark213.phone.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class ConversationControlThread extends Thread {
    private Controller controller;
    private Conversation conversation;
    private int remoteDatagramPort;

    public ConversationControlThread(Conversation conversation, Controller controller, int remoteDatagramPort) {
        this.conversation = conversation;
        this.controller = controller;
        this.remoteDatagramPort = remoteDatagramPort;
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
        while (controller.isInConversation()) {
            conversation.recordAndSend();
        }
        try {
            while (controller.isInConversation()) {
                ObjectInputStream inputStream = new ObjectInputStream(conversation.getSocket().getInputStream());
                Message message = (Message) inputStream.readObject();
                messageIncoming(message);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }
}
