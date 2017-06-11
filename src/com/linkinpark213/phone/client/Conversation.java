package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.speaker.Recorder;
import com.linkinpark213.phone.common.Message;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Conversation {
    private Socket socket;
    private Recorder recorder;
    private int remoteDatagramPort;
    private DatagramSocket datagramSocket;
    private ConversationControlThread conversationControlThread;

    public Conversation(Socket socket, int remoteDatagramPort) {
        this.socket = socket;
        this.remoteDatagramPort = remoteDatagramPort;
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
        byte[] record = recorder.record();
        DatagramPacket datagramPacket = new DatagramPacket(record, record.length, socket.getInetAddress(), remoteDatagramPort);
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        recorder.close();
    }

}
