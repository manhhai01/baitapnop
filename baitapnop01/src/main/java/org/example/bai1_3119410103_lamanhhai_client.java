package org.example;

import java.io.*;
import java.net.Socket;

public class bai1_3119410103_lamanhhai_client {
    Socket socket;
    BufferedReader inStream, stdIn;
    BufferedWriter outStream;

    public bai1_3119410103_lamanhhai_client(String host, int port) {
        try {
            socket = new Socket(host, port);
            System.out.println("Client đã kết nối đến server " + socket.getRemoteSocketAddress());

            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket() {
        try {
            stdIn.close();
            inStream.close();
            outStream.close();
            socket.close();
            System.out.println("Client đóng socket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleData() {
        try {
            while (true) {
                System.out.println("Nhập dữ liệu: ");
                String input = stdIn.readLine();

                outStream.write(input + "\n");
                outStream.flush();

                String data = inStream.readLine();
                do {
                    System.out.println(data);
                    data = inStream.readLine();
                } while (!data.equals("-END-"));

                if(input.equals("bye")) {
                    break;
                }
            }
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        bai1_3119410103_lamanhhai_client client = new bai1_3119410103_lamanhhai_client("localhost", 12345);
        client.handleData();
    }
}
