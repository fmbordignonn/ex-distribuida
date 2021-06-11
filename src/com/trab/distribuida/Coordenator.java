package com.trab.distribuida;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

public class Coordenator extends Thread {

    private DatagramSocket connectionSocket;

    // store host and port from process
    private LinkedList<String> processRequestAddress;

    //true = free; false = locked;
    private boolean resourceStatus;


    public Coordenator(int port) throws SocketException {

        connectionSocket = new DatagramSocket(port);
        processRequestAddress = new LinkedList<String>();

        resourceStatus = true;
    }

    @Override
    public void start() {
        ack();
        while(true) {
            try {
                byte[] bytesPacote = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bytesPacote, bytesPacote.length);
                connectionSocket.setSoTimeout(10000);
                connectionSocket.receive(packet);

                String response = new String(packet.getData(), 0, packet.getLength());

                switch (response) {

                    case "LOCK":
                        String processHost = String.valueOf(packet.getAddress());
                        String processPort = String.valueOf(packet.getPort());

                        processRequestAddress.add(processHost + ":" + processPort);
                        break;

                    case "UNLOCK":
                        // remove the first process request host and port
                        processRequestAddress.remove(0);
                        resourceStatus = true;

                    default:
                        throw new RuntimeException("Command not recognized");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void ack() {
        while(true) {
            if(!processRequestAddress.isEmpty()){
                if (resourceStatus) {

                    resourceStatus = false;

                    try {
                        byte[] bytesPacote = ("ACK").getBytes();

                        // get the first process request host and port
                        String[] processAdress = processRequestAddress.get(0).split(":");
                        String processHost = processAdress[0];
                        String processPort = processAdress[1];
                        DatagramPacket packet = new DatagramPacket(bytesPacote, bytesPacote.length, InetAddress.getByName(processHost), Integer.parseInt(processPort));

                        connectionSocket.send(packet);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
