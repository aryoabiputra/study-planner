/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package studyplanner;

import studyplanner.ui.MainFrame;
import studyplanner.database.DataBase;
import studyplanner.database.InitDatabase;
import java.sql.Connection;
//import studyplanner.dao.MatkulDAO;
//import studyplanner.dao.TugasDAO;
//import studyplanner.model.Matkul;
//import studyplanner.model.Tugas;
//import studyplanner.database.DatabaseMigration;
//
//import java.time.LocalDate;

/**
 *
 * @author aryop
 */
public class Main {

    public static void main(String[] args) {
        Connection conn = DataBase.getConnection();
        System.out.println(conn != null ? "Koneksi OK!" : "Gagal");

        InitDatabase.init();

        //Tampilkan UI
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
