package com.trab.distribuida;

import java.net.SocketException;

public class Main {

    public static void main(String[] args) throws SocketException {
        System.out.println("Iniciando aplicação");

        new Coordenator(3556).start();

        //new Processo("Processo 1", 9091, 3556).start();
    }
}
