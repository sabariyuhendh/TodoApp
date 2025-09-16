package com.todo;
import com.todo.util.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            Connection cn =  db.getDBConnection();
            System.out.println("Connected to database successfully");
        }
        catch(Exception e) {
            System.out.println("Connection Failed! Check output console");
        }
    }
}