package com.trab.distribuida;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class Processo extends Thread {

    private final String processName;

    private final int port;

    private final int coordenatorPort;

    private final String filePath = "C:\\Users\\SouthSystem\\Desktop\\arquivo.txt";

    public Processo(String processName, int port, int coordenatorPort) {
        this.processName = processName;
        this.port = port;
        this.coordenatorPort = coordenatorPort;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("localhost"));

            socket.setSoTimeout(10000000);

            System.out.println("No while true do processo");

            while (true) {
                byte[] bytesPacote = "LOCK".getBytes();

                DatagramPacket packet = new DatagramPacket(bytesPacote, bytesPacote.length, InetAddress.getByName("localhost"), coordenatorPort);

                socket.send(packet);

                socket.receive(packet);

                String response = new String(packet.getData(), 0, packet.getLength());

                if (response.equals("ACK")) {
                    System.out.println("Recebi um ack de liberação pra editar");

                    List<String> lines = read();

                    write(lines);

                    bytesPacote = "UNLOCK".getBytes();

                    System.out.println("Enviando pacote pra liberar");

                    packet = new DatagramPacket(bytesPacote, bytesPacote.length, InetAddress.getByName("localhost"), coordenatorPort);

                    socket.send(packet);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<String> read() throws FileNotFoundException {
        Path path = Paths.get(filePath);

        File file = path.toFile();

        FileReader fr = new FileReader(file);

        BufferedReader reader = new BufferedReader(fr);

        return reader.lines().collect(Collectors.toList());
    }

    private void write(List<String> lines) throws IOException {
        for (int i = 0; i < 50; i++) {
            String line = processName + " - " + i + "\n";

            lines.add(line);
            Files.write(Paths.get(filePath), line.getBytes(), StandardOpenOption.APPEND);

        }
    }
}
