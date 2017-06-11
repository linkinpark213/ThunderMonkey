package com.linkinpark213.phone.client;

import com.linkinpark213.phone.common.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class CallInListenerThread extends Thread {
    /*
    * Keeps checking if there's any calls
    * NOT the same as the receiver!
    * */
    private ServerSocket serverSocket;
    private Socket socket;
    private ConversationControlThread conversationControlThread;
    private Controller controller;

    public CallInListenerThread(Controller controller) {
        try {
            this.controller = controller;
            serverSocket = new ServerSocket(1024 + (int) (Math.random() * 32768));
            System.out.println("Server Listening on Port " + serverSocket.getLocalPort());
            System.out.println("Waiting for call");
            System.out.println("Local IP Address: " + serverSocket.getLocalSocketAddress() + "  Port: " + serverSocket.getLocalPort());
            controller.setLocalStatus("Local Port: " + serverSocket.getLocalPort());
        } catch (IOException e) {
            System.out.println("Failed to Open Listening Socket.");
        }
    }

    @Override
    public void run() {
        while (true) {
            while (controller.isListening()) {
                if (controller.getCurrentState() == Controller.WAITING_FOR_CALL) {
                    /*
                    Waiting for call
                     */
                    try {
                        socket = serverSocket.accept();
                        if (controller.isListening()) {
//                            controller.stopListening();
                            System.out.println("Connection Established With " + socket.getRemoteSocketAddress());
                            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                            Message message = (Message) objectInputStream.readObject();
                            if (message.getType() == Message.CALL_REQUEST) {
                                System.out.println("Call coming From " + socket.getRemoteSocketAddress());
                                controller.callIncoming(socket);
                            }
                        }
                    } catch (SocketException e) {
                        System.out.println("Connection Lost.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
