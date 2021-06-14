package com.trab.distribuida;

import java.net.SocketException;

public class Main {

    public static void main(String[] args) throws SocketException {
        new Coordenator(3556).start();
    }
}
