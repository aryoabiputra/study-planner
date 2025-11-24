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
import studyplanner.model.Tugas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import studyplanner.database.DataBase;
//import studyplanner.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TugasDAO {

    public static int countTotalTugas() {
        String sql = "SELECT COUNT(*) FROM tugas";
        try (Connection conn = DataBase.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Gagal countTotalTugas: " + e.getMessage());
        }
        return 0;
    }

    public static int countMendekatiDeadline(int hariKeDepan) {
        String sql = """
        SELECT COUNT(*)
        FROM tugas
        WHERE selesai = 0
          AND deadline BETWEEN date('now') AND date('now', '+' || ? || ' day')
        """;

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hariKeDepan);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Gagal countMendekatiDeadline: " + e.getMessage());
        }
        return 0;
    }

    public static int countSelesai() {
        String sql = "SELECT COUNT(*) FROM tugas WHERE selesai = 1";
        try (Connection conn = DataBase.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Gagal countSelesai: " + e.getMessage());
        }
        return 0;
    }

    public static int countMingguIni() {
        String sql = """
        SELECT COUNT(*)
        FROM tugas
        WHERE tanggal_dibuat >= date('now', 'weekday 1')
        """;

        try (Connection conn = DataBase.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Gagal countMingguIni: " + e.getMessage());
        }

        return 0;
    }

    public static int countAktif() {
        String sql = "SELECT COUNT(*) FROM tugas WHERE selesai = 0";
        try (Connection conn = DataBase.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Gagal countAktif: " + e.getMessage());
        }
        return 0;
    }

    public static List<Tugas> getMendekatiDeadline(int hariKeDepan) {
        List<Tugas> list = new ArrayList<>();
        String sql = """
        SELECT * FROM tugas
        WHERE selesai = 0
          AND deadline BETWEEN date('now') AND date('now', '+' || ? || ' day')
        ORDER BY deadline ASC
        """;

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hariKeDepan);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetKeTugas(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Gagal getMendekatiDeadline: " + e.getMessage());
        }

        return list;
    }

    public static List<Tugas> getByMatkul(int idMatkul) {
        List<Tugas> list = new ArrayList<>();
        String sql = "SELECT * FROM tugas WHERE matkul_id = ? ORDER BY deadline ASC";

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMatkul);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetKeTugas(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Gagal getByMatkul: " + e.getMessage());
        }

        return list;
    }

    private static Tugas mapResultSetKeTugas(ResultSet rs) throws SQLException {
        Tugas t = new Tugas();
        t.setIdTugas(rs.getInt("id_tugas"));
        t.setJudul(rs.getString("judul"));
        t.setDeskripsi(rs.getString("deskripsi"));
        t.setDeadline(rs.getString("deadline"));

        int mkId = rs.getInt("matkul_id");
        if (rs.wasNull()) {
            t.setMatkulId(null);
        } else {
            t.setMatkulId(mkId);
        }

        t.setSelesai(rs.getInt("selesai") == 1);
        t.setTanggalDibuat(rs.getString("tanggal_dibuat"));

        t.setTipe(rs.getString("tipe"));
        t.setPrioritas(rs.getInt("prioritas"));
        t.setStatus(rs.getString("status"));
        t.setUpdatedAt(rs.getString("updated_at"));

        return t;
    }

    public static List<Tugas> getByStatus(String status) {
        List<Tugas> list = new ArrayList<>();
        String sql = "SELECT * FROM tugas WHERE status = ? ORDER BY deadline ASC";

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tugas t = mapResultSetKeTugas(rs);
                    list.add(t);
                }
            }

        } catch (SQLException e) {
            System.out.println("Gagal getByStatus: " + e.getMessage());
        }
        return list;
    }

    public static void updateStatusDanSelesai(int idTugas, String statusBaru, boolean selesai) {
        String sql = """
        UPDATE tugas
        SET status = ?, selesai = ?, updated_at = date('now')
        WHERE id_tugas = ?
        """;

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statusBaru);                 // "TODO" / "DOING" / "DONE"
            ps.setInt(2, selesai ? 1 : 0);               // 0 / 1
            ps.setInt(3, idTugas);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Gagal update status tugas: " + e.getMessage());
        }
    }

    public static void insert(Tugas t) {
        String sql = """
        INSERT INTO tugas
        (judul, deskripsi, deadline, matkul_id, selesai, tanggal_dibuat,
         tipe, prioritas, status, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getJudul());
            ps.setString(2, t.getDeskripsi());
            ps.setString(3, t.getDeadline());

            if (t.getMatkulId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, t.getMatkulId());
            }

            ps.setInt(5, t.isSelesai() ? 1 : 0);
            ps.setString(6, t.getTanggalDibuat());

            ps.setString(7, t.getTipe());
            ps.setInt(8, t.getPrioritas());
            ps.setString(9, t.getStatus());
            ps.setString(10, t.getUpdatedAt());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Gagal insert tugas: " + e.getMessage());
        }
    }

    public static List<Tugas> getAll() {
        List<Tugas> list = new ArrayList<>();
        String sql = "SELECT * FROM tugas ORDER BY deadline ASC";

        try (Connection conn = DataBase.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetKeTugas(rs));
            }

        } catch (SQLException e) {
            System.out.println("Gagal getAll tugas: " + e.getMessage());
        }

        return list;
    }

    public static void setSelesai(int idTugas, boolean selesai) {
        String sql = "UPDATE tugas SET selesai = ? WHERE id_tugas = ?";

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selesai ? 1 : 0);
            ps.setInt(2, idTugas);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Gagal setSelesai tugas: " + e.getMessage());
        }
    }

    public static void delete(int idTugas) {
        String sql = "DELETE FROM tugas WHERE id_tugas = ?";

        try (Connection conn = DataBase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTugas);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Gagal delete tugas: " + e.getMessage());
        }
    }
}
