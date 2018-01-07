package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.Controller;
import com.linkinpark213.phone.client.Conversation;
import com.linkinpark213.phone.client.receiver.Player;
import com.linkinpark213.phone.common.EncryptException;
import com.linkinpark213.phone.common.RSAUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.security.KeyPair;

/**
 * Created by ooo on 2017/6/11 0011.
 */
public class DatagramReceiverThread extends Thread {
    private Conversation conversation;
    private Controller controller;
    private Socket socket;
    private DatagramSocket datagramSocket;
    private Player player;
    private KeyPair keyPair;

    public DatagramReceiverThread(Conversation conversation, Controller controller, Socket socket, DatagramSocket datagramSocket, KeyPair keyPair) {
        this.conversation = conversation;
        this.controller = controller;
        this.socket = socket;
        this.datagramSocket = datagramSocket;
        this.player = new Player();
        this.keyPair = keyPair;
        System.out.println("Datagram Receiver Listening...");
    }

    @Override
    public void run() {
        while (controller.isInConversation()) {
            byte[] buffer = new byte[1280];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                datagramSocket.receive(datagramPacket);
                System.out.println("Datagram Received from " + datagramPacket.getSocketAddress());

                byte[] message = datagramPacket.getData();

                try {
                    byte[] decrypted = RSAUtil.decrypt(keyPair.getPrivate(), message);
                    System.out.println("Decrypted message. Length: " + decrypted.length);

                    byte[] record = new byte[1024];

                    for (int i = 0; i < 1024; i++) {
                        record[i] = decrypted[i];
                    }

                    player.play(record);

                } catch (EncryptException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        player.close();
    }
}
