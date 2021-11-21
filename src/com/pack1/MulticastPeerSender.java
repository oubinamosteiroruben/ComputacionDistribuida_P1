package com.pack1;

import java.net.*;
import java.io.*;
import java.sql.SQLOutput;

public class MulticastPeerSender {
    public MulticastPeerSender(String ip, String message){

        MulticastSocket s = null;
        try {
            InetAddress group = InetAddress.getByName(ip);
            // Se inicializa el socket y se le indicarán los parámetros necesarios para la conexión
            s = new MulticastSocket(Definitions.port);
            s.joinGroup(group);
            byte [] m = message.getBytes();
            // Creación del datagrama
            DatagramPacket messageOut =  new DatagramPacket(m, m.length, group, Definitions.port);
            // Envía el mensaje indicado por la clase principal
            s.send(messageOut);
            System.out.println("Sent: " + new String(messageOut.getData()));
            s.leaveGroup(group);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (s != null) s.close();
        }
    }
}