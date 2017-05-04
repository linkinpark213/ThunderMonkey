package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.Conversation;
import com.linkinpark213.phone.client.Listener;
import com.linkinpark213.phone.common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class ConversationControlThread extends Thread {
    private Conversation conversation;

    public ConversationControlThread(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public void run() {
        while (conversation.isKeepAlive()) {
            conversation.startRecording();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                conversation.stopRecordingAndSend();
            }
            if (conversation.isKeepAlive())
                conversation.stopRecordingAndSend();
        }
    }
}
