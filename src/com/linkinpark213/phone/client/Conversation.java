package com.linkinpark213.phone.client;

import com.linkinpark213.phone.client.receiver.ReceiverThread;
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
import java.net.Socket;

/**
 * Created by ooo on 2017/5/1 0001.
 */
public class Conversation {
    private Socket socket;
    private ReceiverThread receiverThread;
    private Recorder recorder;
    private ConversationControlThread conversationControlThread;
    private boolean keepAlive;

    public Conversation(Socket socket, Listener listener) {
        this.socket = socket;
        this.keepAlive = false;
        recorder = new Recorder("cache\\" + socket.getLocalPort());
        receiverThread = new ReceiverThread(this);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void hangOff() {
        try {
            this.setKeepAlive(false);
            ObjectOutputStream outputStream = new ObjectOutputStream(this.getSocket().getOutputStream());
            outputStream.writeObject(new Message(Message.HANG_OFF, ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startRecording() {
        recorder.startRecording();
    }

    public void stopRecordingAndSend() {
        recorder.stopRecording();
        try {
            File dir = new File("cache\\" + socket.getLocalPort());
            dir.mkdirs();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("cache\\" + socket.getLocalPort() + "\\record.wav"));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.AU, byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] audioByteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            audioInputStream.close();

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Message("", audioByteArray));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }
}
