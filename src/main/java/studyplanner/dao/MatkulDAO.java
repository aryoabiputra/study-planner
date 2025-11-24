/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studyplanner.dao;

/**
 *
 * @author aryop
 */

import studyplanner.database.DataBase;
import studyplanner.model.Matkul;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatkulDAO {

    public static void insert(Matkul m) {
        String sql = """
            INSERT INTO matkul (nama_matkul, dosen, warna)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNamaMatkul());
            ps.setString(2, m.getDosen());
            ps.setString(3, m.getWarna());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Gagal insert matkul: " + e.getMessage());
        }
    }

    public static List<Matkul> getAll() {
        List<Matkul> list = new ArrayList<>();
        String sql = "SELECT * FROM matkul ORDER BY nama_matkul ASC";

        try (Connection conn = DataBase.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Matkul m = new Matkul();
                m.setIdMatkul(rs.getInt("id_matkul"));
                m.setNamaMatkul(rs.getString("nama_matkul"));
                m.setDosen(rs.getString("dosen"));
                m.setWarna(rs.getString("warna"));
                list.add(m);
            }

        } catch (SQLException e) {
            System.out.println("Gagal getAll matkul: " + e.getMessage());
        }

        return list;
    }

    public static Matkul findById(int id) {
        String sql = "SELECT * FROM matkul WHERE id_matkul = ?";
        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Matkul m = new Matkul();
                    m.setIdMatkul(rs.getInt("id_matkul"));
                    m.setNamaMatkul(rs.getString("nama_matkul"));
                    m.setDosen(rs.getString("dosen"));
                    m.setWarna(rs.getString("warna"));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal findById matkul: " + e.getMessage());
        }
        return null;
    }
}
