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

    @Override
    public void run() {
        try {
            while (controller.isInConversation()) {
                ObjectInputStream inputStream = new ObjectInputStream(conversation.getSocket().getInputStream());
                Message message = (Message) inputStream.readObject();
                if (message.getType() == Message.HANG_OFF) {
                    controller.callingEnd();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            controller.callingEnd();
        }
    }
}
