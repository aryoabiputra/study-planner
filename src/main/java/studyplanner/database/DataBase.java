/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studyplanner.database;
/**
 *
 * @author aryop
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    // Lokasi file database (akan otomatis dibuat kalau belum ada)
    private static final String URL = "jdbc:sqlite:studyplanner.db";

    // Method untuk mengambil koneksi database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Koneksi SQLite gagal: " + e.getMessage());
            return null;
        }
    }
}
