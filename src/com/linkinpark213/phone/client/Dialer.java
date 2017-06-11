package com.linkinpark213.phone.client;

import com.linkinpark213.phone.common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by ooo on 2017/5/1 0001.
 */
/*
* You can call someone else with a phone.
* A dialer helps you do that.
* */
public class Dialer extends Thread {
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket dial(String address, int port, int datagramPort) {
        try {
            socket = new Socket(address, port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(new Message(Message.CALL_REQUEST, datagramPort));
            return socket;
        } catch (IOException e) {
            return null;
        }
    }

}
