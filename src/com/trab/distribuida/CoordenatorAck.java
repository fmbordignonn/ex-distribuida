package com.trab.distribuida;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class CoordenatorAck extends Thread {

    @Override
    public void run() {
        System.out.println("No while true do coordenator ack thread");

        while(true) {
            if(!Coordenator.getProcessRequestAddress().isEmpty()){
                if (Coordenator.getResourceStatus()) {

                    Coordenator.setResourceStatus(false);

                    try {
                        byte[] bytesPacote = ("ACK").getBytes();

                        // get the first process request host and port
                        String[] processAdress = Coordenator.getProcessRequestAddress().get(0).split(":");
                        String processHost = processAdress[0].replace("/", "");;
                        String processPort = processAdress[1];

                        var ss = InetAddress.getByName("localhost");
                        DatagramPacket packet = new DatagramPacket(bytesPacote, bytesPacote.length, InetAddress.getByName(processHost), Integer.parseInt(processPort));

                        Coordenator.getConnectionSocket().send(packet);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
