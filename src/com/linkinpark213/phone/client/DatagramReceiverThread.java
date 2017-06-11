package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.Controller;
import com.linkinpark213.phone.client.Conversation;
import com.linkinpark213.phone.client.receiver.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by ooo on 2017/6/11 0011.
 */
public class DatagramReceiverThread extends Thread {
    private Conversation conversation;
    private Controller controller;
    private Socket socket;
    private DatagramSocket datagramSocket;
    private Player player;

    public DatagramReceiverThread(Conversation conversation, Controller controller, Socket socket, DatagramSocket datagramSocket) {
        this.conversation = conversation;
        this.controller = controller;
        this.socket = socket;
        this.datagramSocket = datagramSocket;
        this.player = new Player();
        System.out.println("Datagram Receiver Listening...");
    }

    @Override
    public void run() {
        while (controller.isInConversation()) {
            byte[] buffer = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                datagramSocket.receive(datagramPacket);
                player.play(datagramPacket.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        player.close();
    }
}
