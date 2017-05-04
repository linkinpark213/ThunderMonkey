package com.linkinpark213.phone.client;

import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Listener extends Thread {
    /*
    * Keeps checking if there's any calls
    * NOT the same as the receiver!
    * */
    private ServerSocket serverSocket;
    private Socket socket;
    private boolean keepListening;
    private ConversationControlThread conversationControlThread;
    private Controller controller;

    public Listener(Controller controller) {
        try {
            serverSocket = new ServerSocket(1024 + (int) (Math.random() * 32768));
            keepListening = true;
            System.out.println("Server Listening on Port " + serverSocket.getLocalPort());

            System.out.println("Local IP Address: " + serverSocket.getLocalSocketAddress() + "  Port: " + serverSocket.getLocalPort());

        } catch (IOException e) {
            System.out.println("Failed to Open Server Socket.");
        }
    }

    @Override
    public void run() {
        while (true) {
            while (isKeepListening()) {
                try {
                    socket = serverSocket.accept();
                    setKeepListening(false);
                    System.out.println("Connection Established With " + socket.getRemoteSocketAddress());
                    System.out.println("Answering Call From " + socket.getRemoteSocketAddress());
                    controller.getStatusText().setText("Answering Call From " + socket.getRemoteSocketAddress());

                    Conversation conversation = new Conversation(socket, this);
                    conversationControlThread = new ConversationControlThread(conversation);
                    conversationControlThread.run();
                } catch (SocketException e) {
                    System.out.println("Connection Lost.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isKeepListening() {
        return keepListening;
    }

    public void setKeepListening(boolean keepListening) {
        this.keepListening = keepListening;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
