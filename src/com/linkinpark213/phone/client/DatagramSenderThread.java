package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.Controller;
import com.linkinpark213.phone.client.Conversation;
import com.linkinpark213.phone.common.Message;

import java.io.ObjectInputStream;

/**
 * Created by ooo on 2017/6/11 0011.
 */
public class DatagramSenderThread extends Thread {
    private Controller controller;
    private Conversation conversation;
    private int remoteDatagramPort;
    public DatagramSenderThread (Conversation conversation, Controller controller, int remoteDatagramPort) {
        this.conversation = conversation;
        this.controller = controller;
        this.remoteDatagramPort = remoteDatagramPort;
    }

    @Override
    public void run() {
        while (controller.isInConversation()) {
            conversation.recordAndSend();
        }
    }
}
