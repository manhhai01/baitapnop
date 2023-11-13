package org.example;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class bai2_3119410103_lamanhhai_server {
    private static int buffsize = 512;
    private static int port = 1234;

    public static void main(String[] args) {
        DatagramSocket socket;
        DatagramPacket dpreceive, dpsend;

        try {
            socket = new DatagramSocket(port);
            dpreceive = new DatagramPacket(new byte[buffsize], buffsize);

            while(true) {
                socket.receive(dpreceive);
                String tmp = new String(dpreceive.getData(), 0, dpreceive.getLength());
                System.out.println("Server recevied: " + tmp + " from " + dpreceive.getAddress().getHostAddress()
                        + " at port " + socket.getLocalPort());

                if(tmp.equals("bye")) {
                    System.out.println("Server socket closed");
                    socket.close();
                    break;
                }

                String data = processRequest(tmp);
                byte[] resp = data.getBytes();
                dpsend = new DatagramPacket(resp, resp.length, dpreceive.getAddress(), dpreceive.getPort());
                System.out.println("Server sent back " + data + " to client");
                socket.send(dpsend);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    *   return 1: băm mp5
    *   return 2: dịch mp5
    *   return 3: đổi cơ số
    *   return -1: không đúng định dạng
    * */
    public static int checkFormatFromClient(String message) {
        String[] parts = message.split(";");
        int res = -1;
        switch (parts.length) {
            case 2:
                if(parts[0].equals("enc"))
                    res = 1;
                else if (parts[0].equals("dec"))
                    res = 2;
                break;
            case 3:
                try {
                    for(int i = 0; i < 2; i++) {
                        Integer.parseInt(parts[i]);
                    }
                    res = 3;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                res = -1;
                break;
        }
        return res;
    }

    public static String processRequest(String request) throws IOException {
        int reqFormat = checkFormatFromClient(request);
        String[] parts = request.split(";");
        String resp = "";
        switch (reqFormat) {
            case 1:
                resp = encodeDataMP5(parts[1]);
                break;
            case 2:
                resp = decodeDataMP5(parts[1]);
                break;
            case 3:
                resp = convertBaseNumber(parts[0], parts[1], parts[2]);
                break;
            default:
                resp = "Lỗi dữ liệu đầu vào!";
                break;
        }
        return resp;
    }

    public static String encodeDataMP5(String data) {
        try {
            String url = "https://hashtoolkit.com/generate-hash/?text=" + data;
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).get();
            String encStr = doc.getElementsByClass("res-hash").get(0).getElementsByTag("a").get(0).text();
            return "--> Chức năng băm MP5: " + data + " --> " + encStr;
        } catch (Exception e) {
            return "Lỗi không thực hiện được!";
        }
    }

    public static String decodeDataMP5(String data) {
        try {
            String url = "https://hashtoolkit.com/decrypt-hash/?hash=" + data;
            Document doc = Jsoup.connect(url).method(Connection.Method.GET).get();
            String decStr = doc.getElementsByClass("res-text").get(0).getElementsByTag("a").get(0).text();
            return "--> Chức năng dịch ngược chuỗi MP5: " + data + " --> " + decStr;
        } catch (Exception e) {
            return "Không tìm thấy kết quả!";
        }
    }

    public static String convertBaseNumber(String from, String to, String number) {
        try {
            String url = "https://networkcalc.com/api/binary/"+number+"?from="+from+"&to=" + to;
            Document doc = SSLHelper.getConnection(url)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute()
                    .parse();

            JSONObject json = new JSONObject(doc.text());
            if(json.get("status").equals("OK")) {
                return "--> Kết quả đổi cơ số: " + json.get("converted").toString();
            } else {
                return "Lỗi không thực hiện được!";
            }

        } catch (Exception e) {
            return "Lỗi không thực hiện đươc!";
        }
    }
}
