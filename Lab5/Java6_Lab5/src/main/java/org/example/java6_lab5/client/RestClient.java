/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.java6_lab5.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.util.Scanner;
/**
 *
 * @author Desmond
 */


public class RestClient {
    static String host = "https://lab6java5ts00680-default-rtdb.asia-southeast1.firebasedatabase.app";
    static ObjectMapper mapper = new ObjectMapper();
    static Scanner scanner = new Scanner(System.in);
    
    private static void getAll() {
        try {
            String url = host + "/students.json";
            HttpURLConnection connection = HttpClient.openConnection("GET", url);
            byte[] response = HttpClient.readData(connection);
            
            String jsonResponse = new String(response, "UTF-8");
            System.out.println("GET ALL Response:");
            System.out.println(jsonResponse);
            System.out.println("-------------------\n");
        } catch (Exception e) {
            System.err.println("Error in getAll: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void getByKey() {
        try {
            System.out.print("Enter student key: ");
            String key = scanner.nextLine();
            
            String url = host + "/students/" + key + ".json";
            HttpURLConnection connection = HttpClient.openConnection("GET", url);
            byte[] response = HttpClient.readData(connection);
            
            String jsonResponse = new String(response, "UTF-8");
            System.out.println("GET BY KEY Response:");
            System.out.println(jsonResponse);
            System.out.println("-------------------\n");
        } catch (Exception e) {
            System.err.println("Error in getByKey: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void post() {
        try {
            System.out.print("Enter student ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            System.out.print("Enter student mark: ");
            double mark = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter student gender (true/false): ");
            boolean gender = Boolean.parseBoolean(scanner.nextLine());
            
            // Tạo JSON object
            String jsonData = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"mark\":%.1f,\"gender\":%b}",
                id, name, mark, gender
            );
            
            String url = host + "/students.json";
            HttpURLConnection connection = HttpClient.openConnection("POST", url);
            byte[] response = HttpClient.writeData(connection, jsonData.getBytes("UTF-8"));
            
            String jsonResponse = new String(response, "UTF-8");
            System.out.println("POST Response:");
            System.out.println(jsonResponse);
            System.out.println("-------------------\n");
        } catch (Exception e) {
            System.err.println("Error in post: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void put() {
        try {
            System.out.print("Enter student key to update: ");
            String key = scanner.nextLine();
            System.out.print("Enter new student ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter new student name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new student mark: ");
            double mark = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter new student gender (true/false): ");
            boolean gender = Boolean.parseBoolean(scanner.nextLine());
            
            // Tạo JSON object
            String jsonData = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"mark\":%.1f,\"gender\":%b}",
                id, name, mark, gender
            );
            
            String url = host + "/students/" + key + ".json";
            HttpURLConnection connection = HttpClient.openConnection("PUT", url);
            byte[] response = HttpClient.writeData(connection, jsonData.getBytes("UTF-8"));
            
            String jsonResponse = new String(response, "UTF-8");
            System.out.println("PUT Response:");
            System.out.println(jsonResponse);
            System.out.println("-------------------\n");
        } catch (Exception e) {
            System.err.println("Error in put: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void delete() {
        try {
            System.out.print("Enter student key to delete: ");
            String key = scanner.nextLine();
            
            String url = host + "/students/" + key + ".json";
            HttpURLConnection connection = HttpClient.openConnection("DELETE", url);
            byte[] response = HttpClient.readData(connection);
            
            String jsonResponse = new String(response, "UTF-8");
            System.out.println("DELETE Response:");
            System.out.println(jsonResponse);
            System.out.println("-------------------\n");
        } catch (Exception e) {
            System.err.println("Error in delete: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        while (true) {
            System.out.println("=== REST CLIENT MENU ===");
            System.out.println("1. GET ALL");
            System.out.println("2. GET BY KEY");
            System.out.println("3. POST (Create)");
            System.out.println("4. PUT (Update)");
            System.out.println("5. DELETE");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    getAll();
                    break;
                case "2":
                    getByKey();
                    break;
                case "3":
                    post();
                    break;
                case "4":
                    put();
                    break;
                case "5":
                    delete();
                    break;
                case "0":
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}