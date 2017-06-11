package com.linkinpark213.phone.client;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class ConversationControlThread extends Thread {
    private Controller controller;
    private Conversation conversation;

    public ConversationControlThread(Conversation conversation, Controller controller) {
        this.conversation = conversation;
        this.controller = controller;
    }

    @Override
    public void run() {
        while (controller.isInConversation()) {
            conversation.startRecording();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                conversation.stopRecordingAndSend();
            }
            if (controller.isInConversation())
                conversation.stopRecordingAndSend();
        }
    }
}
