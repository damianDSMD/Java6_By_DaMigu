/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.java6_lab5.client;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 *
 * @author Desmond
 */
public class HttpClient {
    
    /**
     * Mở kết nối
     *
     * @param method là web method (GET, POST, PUT hay DELETE)
     * @param url địa chỉ URL của REST API
     */
    public static HttpURLConnection openConnection(String method, String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        
        if ("POST".equals(method) || "PUT".equals(method)) {
            connection.setDoOutput(true);
        }
        
        return connection;
    }
    
    /**
     * Đọc dữ liệu phản hồi từ server và đóng kết nối
     */
    public static byte[] readData(HttpURLConnection connection) throws IOException {
        try {
            int responseCode = connection.getResponseCode();
            InputStream inputStream;
            
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }
            
            if (inputStream == null) {
                return new byte[0];
            }
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;
            
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            
            buffer.flush();
            inputStream.close();
            
            return buffer.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Gửi dữ liệu lên server và đọc dữ liệu phản hồi từ server
     */
    public static byte[] writeData(HttpURLConnection connection, byte[] data) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            os.write(data);
            os.flush();
        }
        
        return readData(connection);
    }
}