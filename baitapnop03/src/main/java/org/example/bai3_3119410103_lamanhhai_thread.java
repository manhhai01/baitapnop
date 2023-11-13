package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;

//33M8fYwpRABFEhPcuM6CrZoPobA3Owdp
public class bai3_3119410103_lamanhhai_thread implements Runnable {
    private Socket socket;
    public bai3_3119410103_lamanhhai_thread(Socket s) {
        this.socket = s;
    }
    public void run() {
        System.out.println("Client " + socket.toString() + " accepted");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String input = "";
            while(true) {
                input = in.readLine();
                System.out.println("Server received: " + input + " from " + socket.toString());
                if(input.equals("bye"))
                    break;

                String resp = processData(input);
                out.write(resp + "\n");
                out.flush();
            }
            System.out.println("Closed socket for client " + socket.toString());
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String processData(String req) {
        String resp = "";
        if(checkFormatRequest(req)) {
            String[] parts = getInfoFromRequest(req);
            if(parts[0].equals("weather")) {
                return processWeather(parts[1]);
            } else if (parts[0].equals("calc")) {
                return processCalc(parts[1]);
            }
        } else {
            resp = "Lỗi dữ liệu đầu vào!";
        }
        return resp;
    }

    public boolean checkFormatRequest(String req) {
        String[] parts = req.split(" ");
        return (parts[0].startsWith("weather") || parts[0].startsWith("calc")) && parts.length >= 2;
    }

    public String[] getInfoFromRequest(String req) {
        String[] res = new String[2];
        String[] parts = req.split(" ");
        String data = "";
        for (int i = 1; i < parts.length; i++) {
            data += parts[i] + " ";
        }
        res[0] = parts[0];
        res[1] = data;
        return res;
    }

    public String processWeather(String location) {
        String res = "";
        try {
            String urlEncodeLocation = URLEncoder.encode(location, "UTF-8");
            String url = "https://api.tomorrow.io/v4/weather/forecast?location="+urlEncodeLocation+"&apikey=33M8fYwpRABFEhPcuM6CrZoPobA3Owdp";
            Document doc = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute()
                    .parse();
            JSONObject json = new JSONObject(doc.text());
            String temperature = getTemperatureFromJsonWeather(json);
            String name = getNameFromJsonWeather(json);
            res = "--> Chức năng kiểm tra nhiệt độ: " + name + " có nhiệt độ hiện tại là: " + temperature + " độ C.";
        } catch (Exception e) {
            e.printStackTrace();
            res = "--> Không thực hiện lấy được nhiệt độ hiện tại";
        }
        return res;
    }

    public String getTemperatureFromJsonWeather(JSONObject json) {
        JSONObject timelines = (JSONObject) json.get("timelines");
        JSONArray array = timelines.getJSONArray("minutely");
        JSONObject object = (JSONObject) array.get(0);
        JSONObject values = (JSONObject) object.get("values");
        return values.get("temperature").toString();
    }

    public String getNameFromJsonWeather(JSONObject json) {
        JSONObject location = (JSONObject) json.get("location");
        return location.get("name").toString();
    }

    public String processCalc(String expression) {
        String res = "";
        try {
            String urlEncodeExpression = URLEncoder.encode(expression, "UTF-8");
            String url = "http://api.mathjs.org/v4/?expr="+urlEncodeExpression;
            Document doc = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute()
                    .parse();
            res = "--> Kết quả phép tính: " + doc.text();
        } catch (Exception e) {
            e.printStackTrace();
            res = "--> Phép tính có lỗi, không tính kết quả được.";
        }
        return res;
    }
}
