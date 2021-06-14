package com.trab.distribuida;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;

public class Coordenator extends Thread {

    private static DatagramSocket connectionSocket;

    // store host and port from process
    private static LinkedList<String> processRequestAddress;

    //true = free; false = locked;
    private static boolean resourceStatus;

    private CoordenatorAck coordenatorAckThread;

    public Coordenator(int port) throws SocketException {

        connectionSocket = new DatagramSocket(port);
        processRequestAddress = new LinkedList<>();
        resourceStatus = true;

        new CoordenatorAck().start();
    }

    @Override
    public void start() {
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

    public static DatagramSocket getConnectionSocket() {
        return connectionSocket;
    }

    public static LinkedList<String> getProcessRequestAddress() {
        return processRequestAddress;
    }

    public static boolean getResourceStatus() {
        return resourceStatus;
    }

    public static void setResourceStatus(boolean resourceStatus) {
        Coordenator.resourceStatus = resourceStatus;
    }
}
