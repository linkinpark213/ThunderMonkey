package com.linkinpark213.phone.client;

import com.linkinpark213.phone.common.Message;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class CallInListenerThread extends Thread {
    /*
    * Keeps checking if there's any calls
    * NOT the same as the receiver!
    * */
    private ServerSocket serverSocket;
    private DatagramSocket datagramSocket;
    private Socket socket;
    private ConversationControlThread conversationControlThread;
    private Controller controller;

    public CallInListenerThread(Controller controller) {
        this.controller = controller;
        boolean shouldGetNewSocket = true;
        while (shouldGetNewSocket) {
            try {
                serverSocket = new ServerSocket(1024 + (int) (Math.random() * 32768));
                shouldGetNewSocket = false;
                controller.setServerSocket(serverSocket);
            } catch (IOException e) {
                System.out.println("Failed to Get a Port for Listening Socket. Retrying...");
            }
        }
        System.out.println("Server Listening on Port " + serverSocket.getLocalPort());
        System.out.println("Waiting for call");
        System.out.println("Local IP Address: " + serverSocket.getLocalSocketAddress() + "  Port: " + serverSocket.getLocalPort());

        shouldGetNewSocket = true;
        while (shouldGetNewSocket) {
            try {
                datagramSocket = new DatagramSocket(1024 + (int) (Math.random() * 32768));
                shouldGetNewSocket = false;
            } catch (SocketException e) {
                shouldGetNewSocket = true;
            }
        }
        controller.setDatagramSocket(datagramSocket);
        System.out.println("UDP Port: " + datagramSocket.getLocalPort());

        controller.setLocalStatus("Local Control Port: " + serverSocket.getLocalPort() +
                "\nLocal Transfer Port: " + datagramSocket.getLocalPort());
    }

    @Override
    public void run() {
        while (true) {
            while (controller.isListening()) {
                    /*
                    Waiting for call
                     */
                try {
                    socket = serverSocket.accept();
                    if (controller.isListening()) {
                        System.out.println("Connection Established With " + socket.getRemoteSocketAddress());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                        Message message = (Message) objectInputStream.readObject();
                        if (message.getType() == Message.CALL_REQUEST) {
                            System.out.println("Call coming From " + socket.getRemoteSocketAddress()
                                    + "\nRemote Datagram Socket is " + message.getDatagramPort());
                            System.out.println("Public Key: " + message.getPublicKey());
                            controller.setPeerPublicKey(message.getPublicKey());
                            controller.callIncoming(socket, message.getDatagramPort());
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
