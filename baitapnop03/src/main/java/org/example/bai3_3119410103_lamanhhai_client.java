package org.example;

import java.io.*;
import java.net.Socket;

public class bai3_3119410103_lamanhhai_client {
    private static String host = "localhost";
    private static int port = 1234;
    private static Socket socket;

    public static void main(String[] args) throws IOException {
        try {
            socket = new Socket(host, port);
            System.out.println("Client đã kết nối đến server");
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while(true) {
                System.out.print("Nhập dữ liệu: ");
                input = stdIn.readLine();
                out.write(input + "\n");
                out.flush();
                if(input.equals("bye"))
                    break;
                String data = in.readLine();
                System.out.println(data);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if(socket!=null) {
                socket.close();
                System.out.println("Client đã đóng socket");
            }
        }
    }
}
