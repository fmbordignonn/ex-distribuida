package com.trab.distribuida;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Processo extends Thread {

    private final String processName;

    private final int port;

    private final int coordenatorPort;

    private int iteration;

    public Processo(String processName, int port, int coordenatorPort) {
        this.processName = processName;
        this.port = port;
        this.coordenatorPort = coordenatorPort;
    }

    @Override
    public synchronized void start() {
        try {
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getLocalHost());

            socket.setSoTimeout(10000000);

            while (true) {
                byte[] bytesPacote = "LOCK".getBytes();

                DatagramPacket packet = new DatagramPacket(bytesPacote, bytesPacote.length, InetAddress.getLocalHost(), coordenatorPort);

                socket.send(packet);

                socket.receive(packet);

                String response = new String(packet.getData(), 0, packet.getLength());

                if (response.equals("ACK")) {
                    List<String> lines = read();

                    write(lines);

                    bytesPacote = "UNLOCK".getBytes();

                    packet = new DatagramPacket(bytesPacote, bytesPacote.length, InetAddress.getLocalHost(), coordenatorPort);

                    socket.send(packet);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<String> read() throws FileNotFoundException {
        Path path = Paths.get("C:\\Users\\Felipe\\Desktop\\arquivo.txt");

        File file = path.toFile();

        FileReader fr = new FileReader(file);

        BufferedReader reader = new BufferedReader(fr);

        return reader.lines().collect(Collectors.toList());
    }

    private void write(List<String> lines) {
        for (int i = iteration; iteration < iteration + 50; iteration++) {
            lines.add(processName + " - " + i);
        }
    }
}
