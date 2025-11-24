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
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigration {
    
    public static void tambahKolomBaruKeTugas() {
        try (Connection conn = DataBase.getConnection();
             Statement st = conn.createStatement()) {

            // Tambah kolom TIPE (tugas / kuis / ujian)
            try {
                st.execute("ALTER TABLE tugas ADD COLUMN tipe TEXT DEFAULT 'tugas';");
                System.out.println("Kolom 'tipe' ditambahkan.");
            } catch (SQLException e) {
                System.out.println("Kolom 'tipe' mungkin sudah ada: " + e.getMessage());
            }

            // Tambah kolom PRIORITAS (1–3)
            try {
                st.execute("ALTER TABLE tugas ADD COLUMN prioritas INTEGER DEFAULT 2;");
                System.out.println("Kolom 'prioritas' ditambahkan.");
            } catch (SQLException e) {
                System.out.println("Kolom 'prioritas' mungkin sudah ada: " + e.getMessage());
            }

            // Tambah kolom STATUS (TODO / DOING / DONE)
            try {
                st.execute("ALTER TABLE tugas ADD COLUMN status TEXT DEFAULT 'TODO';");
                System.out.println("Kolom 'status' ditambahkan.");
            } catch (SQLException e) {
                System.out.println("Kolom 'status' mungkin sudah ada: " + e.getMessage());
            }

            // Tambah kolom UPDATED_AT
            try {
                st.execute("ALTER TABLE tugas ADD COLUMN updated_at TEXT;");
                System.out.println("Kolom 'updated_at' ditambahkan.");
            } catch (SQLException e) {
                System.out.println("Kolom 'updated_at' mungkin sudah ada: " + e.getMessage());
            }

            System.out.println("Migrasi kolom baru selesai.");

        } catch (SQLException e) {
            System.out.println("Gagal migrasi tugas: " + e.getMessage());
        }
    }
}
