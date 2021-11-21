package com.pack1;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class Chat extends JFrame{
    private JButton sendMessageButton;
    private JTextField messageTextField;
    private JTextArea MessagesBox;
    private JScrollPane scrollPane;
    private JPanel chatMainForm;
    private JTextField userNameTextField;

    public Chat(String title){
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(chatMainForm);
        this.pack();

        // Listener que se activará cuando se pulse el botón de enviar

        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!messageTextField.getText().equals("")){
                    String message = "";
                    // Se añade el nombre del usuario al mensaje
                    if(userNameTextField.getText().equals("")){
                        message += "Anonymous: ";
                    }else{
                        message += userNameTextField.getText() + ": ";
                    }
                    message += messageTextField.getText();
                    // Llamamos al enviador del mensaje
                    MulticastPeerSender mps = new MulticastPeerSender(Definitions.ip,message);
                    messageTextField.setText("");
                }
            }
        });

        this.setVisible(true);

        // Se activará el recibidor de mensajes
        multicastReceive(Definitions.ip);
    }

    public static void main(String[] args){
        JFrame frame = new Chat("My multicast chat ");
    }


    public void multicastReceive(String ip){
        MulticastSocket s = null;
        try {
            InetAddress group = InetAddress.getByName(ip);
            s = new MulticastSocket(Definitions.port);
            s.joinGroup(group);
            byte[] buffer = new byte[1000];
            String message = "";
            // Se pone a esperar mensajes y procesarlos infinitamente (se podría poner un bucle for limitado)
            while(true){
                // Se crea el datagrama en el que se guardará el mensaje
                DatagramPacket messageIn =  new DatagramPacket(buffer, buffer.length);
                // Espera hasta recibir un mensaje
                s.receive(messageIn);
                message = new String(messageIn.getData());
                System.out.println("Received:" + message);
                // Actualiza el cuadro donde se muestran los mensajes
                if(MessagesBox.getText().equals("")){
                    MessagesBox.setText(message);
                }else{
                    MessagesBox.setText(MessagesBox.getText() + "\n" + message);
                }
                buffer = new byte[1000];

            }
            // Se sale de la ip indicada (está comentado debido a que previamente estamos usando un bucle while(1) )
            //s.leaveGroup(group);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (s != null) s.close();
        }
    }
}
