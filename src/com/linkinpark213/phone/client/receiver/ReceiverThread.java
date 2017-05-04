package com.linkinpark213.phone.client.receiver;

import com.linkinpark213.phone.client.Conversation;
import com.linkinpark213.phone.common.Message;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;

/**
 * Created by ooo on 2017/4/30 0030.
 */
public class ReceiverThread extends Thread {
    private Conversation conversation;
    private Socket socket;
    private boolean keepAlive;

    public ReceiverThread(Conversation conversation) {
        this.conversation = conversation;
        this.socket = conversation.getSocket();
        keepAlive = true;
        System.out.println("Receiver Listening...");
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void messageIncoming(Message message) {
        /*
         * Receive the message and play the sound according to the data.
         */
        switch (message.getType()) {
            case Message.SPEAK:
                try {
                    File dir = new File("cache\\" + socket.getLocalPort());
                    dir.mkdirs();
                    String fileName = "\\download" + (int) (Math.random() * 65536) + ".wav";
                    FileOutputStream fileOutputStream = new FileOutputStream(new File("cache\\" + socket.getLocalPort() + fileName));
                    fileOutputStream.write(message.getAudioByteArray());
                    fileOutputStream.close();
                    PlayerThread playerThread = new PlayerThread("cache\\" + socket.getLocalPort() + fileName);
                    playerThread.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Message.HANG_OFF:
            default:
                this.setKeepAlive(false);
                this.conversation.setKeepAlive(false);
                this.conversation.end();
                System.out.println("Phone Call Was Hung Off");
        }
    }

    @Override
    public void run() {
        try {
            while (isKeepAlive()) {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) inputStream.readObject();
                messageIncoming(message);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
