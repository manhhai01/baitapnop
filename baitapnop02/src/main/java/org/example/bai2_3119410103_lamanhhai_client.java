package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class bai2_3119410103_lamanhhai_client {
    private static int destPort = 1234;
    private static String hostName = "localhost";

    public static void main(String[] args) {
        DatagramSocket socket;
        DatagramPacket dpsend, dpreceive;
        InetAddress address;
        Scanner stdIn;

        try {
            address = InetAddress.getByName(hostName);
            socket = new DatagramSocket();
            stdIn = new Scanner(System.in);

            while (true) {
                System.out.print("Client gửi: ");
                String tmp = stdIn.nextLine();
                byte[] data = tmp.getBytes();
                dpsend = new DatagramPacket(data, data.length, address, destPort);
                socket.send(dpsend);
                if(tmp.equals("bye")) {
                    System.out.println("Client đóng socket");
                    stdIn.close();
                    socket.close();
                    break;
                }
                //
                dpreceive = new DatagramPacket(new byte[512], 512);
                socket.receive(dpreceive);
                tmp = new String(dpreceive.getData(), 0, dpreceive.getLength());
                System.out.println(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
