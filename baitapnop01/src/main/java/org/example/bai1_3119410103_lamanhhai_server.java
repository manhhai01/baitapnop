package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class bai1_3119410103_lamanhhai_server {
   BufferedReader inStream;
   BufferedWriter outStream;
   ServerSocket server;
   Socket client;

   public bai1_3119410103_lamanhhai_server(int port) {
       try {
           server = new ServerSocket(port);
           System.out.println("Khởi tạo server thành công.");
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   private boolean startServer() {
       try {
           client = server.accept();
           System.out.println("Client " + client.getRemoteSocketAddress() + " đã kết nối.");
           inStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
           outStream = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
           return true;
       } catch (IOException e) {
           return false;
       }
   }

   private void closeSocket() throws IOException {
       inStream.close();
       outStream.close();
       client.close();
       server.close();
   }

   private void handleClient() throws IOException {
       while(true) {
           String input = inStream.readLine();
           if(input.equals("bye")) {
               System.out.println("Server nhận được yêu cầu đóng kết nối từ client");
               outStream.write("Server nhận được yêu cầu đóng kết nối\n-END-\n");
               outStream.flush();
               break;
           }
           System.out.println("Server nhận được: " + input);
           String output = processData(input);
           outStream.write(output + "\n-END-\n");
           outStream.flush();
       }
       closeSocket();
   }

    private String processData(String input) {
        try {
            if(isTikiLink(input)) {
                String productID = getProductID(input);
                return getDataResponse(productID);
            } else {
                return "Lỗi tra cứu thông tin, vui lòng thử lại!";
            }
        } catch (Exception e) {
            return "Lỗi tra cứu thông tin, vui lòng thử lại!";
        }
    }

    private String getDataResponse(String productID) throws IOException {
       return getDataProduct(productID) + "\n" + getDataReview(productID);
    }

    private String getDataProduct(String productID) throws IOException {
        String urlApi = "https://tiki.vn/api/v2/products/" + productID;
        Document doc = Jsoup.connect(urlApi)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute()
                .parse();
        JSONObject json = new JSONObject(doc.text());
        String productName = json.get("name").toString();
        String price = json.get("price").toString();

        return "Tên sản phẩm: " + productName + ". Giá: " + price;
    }

    private String getDataReview(String productID) throws IOException {
       String data = "";
       String urlApi = "https://tiki.vn/api/v2/reviews?limit=10&product_id=" + productID;
       Document doc = Jsoup.connect(urlApi)
               .method(Connection.Method.GET)
               .ignoreContentType(true)
               .execute()
               .parse();

       JSONObject json = new JSONObject(doc.text());
       String rating_average = json.get("rating_average").toString();
       String reviews_count = json.get("reviews_count").toString();

       data += "Sản phẩm có " + reviews_count + " review với điểm trung bình là: " + rating_average + "\n";

       JSONArray array = json.getJSONArray("data");
       for(int i = 0; i < array.length(); i++) {
           JSONObject object = (JSONObject) array.get(i);
           if(!object.get("content").toString().trim().equals("")) {
               data += "\t" + " + " + object.get("content") + "\n";
           }
        }

       return data;
    }


    private boolean isTikiLink(String url) {
        return url.startsWith("https://tiki.vn");
    }

    private String getProductID(String url) {
        return url.substring(url.lastIndexOf("-p") + 2, url.indexOf(".html"));
    }

    public static void main(String[] args) throws IOException {
        bai1_3119410103_lamanhhai_server myserver = new bai1_3119410103_lamanhhai_server(12345);
        if (!myserver.startServer()) {
            System.out.println("Lỗi không chấp nhận kết nối");
            return;
        }
        myserver.handleClient();
    }
}
