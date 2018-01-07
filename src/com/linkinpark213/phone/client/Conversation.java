package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.speaker.Recorder;
import com.linkinpark213.phone.common.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Conversation {
    private Socket socket;
    private Recorder recorder;
    private int remoteDatagramPort;
    private DatagramSocket datagramSocket;
    private KeyPair keyPair;
    private PublicKey peerPublicKey;
    private ConversationControlThread conversationControlThread;

    public Conversation(Socket socket, int remoteDatagramPort, KeyPair keyPair, PublicKey peerPublicKey) {
        this.socket = socket;
        this.remoteDatagramPort = remoteDatagramPort;
        this.keyPair = keyPair;
        this.peerPublicKey = peerPublicKey;
        try {
            this.datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        recorder = new Recorder();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void hangOff() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(this.getSocket().getOutputStream());
            outputStream.writeObject(new Message(Message.HANG_OFF, ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recordAndSend() {
        try {
            byte[] record = recorder.record();
            InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            System.out.println("Remote Address: " + socketAddress.getAddress());

            byte[] digest = MD5Util.md5(record);
            try {
                byte[] signature = RSAUtil.encrypt(keyPair.getPrivate(), digest); // 128 for MD5

                byte[] message = ByteArrayUtil.cat(record, signature); // 1024 + 128 = 1152

                byte[] encrypted = RSAUtil.encrypt(peerPublicKey, message); // 1280 ???

                DatagramPacket datagramPacket = new DatagramPacket(encrypted, encrypted.length, socketAddress.getAddress(), remoteDatagramPort);

                try {
                    datagramSocket.send(datagramPacket);
                    System.out.println("Datagram Sent to " + socketAddress.getAddress() + " : " + remoteDatagramPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (EncryptException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        recorder.close();
    }

}
