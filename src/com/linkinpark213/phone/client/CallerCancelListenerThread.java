package com.linkinpark213.phone.client;

import com.linkinpark213.phone.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by ooo on 2017/6/13 0013.
 */
public class CallerCancelListenerThread extends Thread {
    private Controller controller;
    private Socket socket;

    public CallerCancelListenerThread(Controller controller) {
        this.controller = controller;
        this.socket = controller.getConversationSocket();
    }

    @Override
    public void run() {
        while(controller.isBeingCalled()) {
            try {
                System.out.println("Listening if caller cancels");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();
                if(message.getType() == Message.CALL_CANCEL) {
                    System.out.println("Caller Canceled Calling.");
                    controller.setStatus("Caller Canceled Calling.");
                    controller.waitForCall();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
