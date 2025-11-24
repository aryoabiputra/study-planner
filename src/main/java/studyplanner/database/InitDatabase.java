package studyplanner.database;
/**
 *
 * @author aryop
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InitDatabase {

    public static void init() {
        try (Connection conn = DataBase.getConnection(); Statement st = conn.createStatement()) {

            // Tabel MATKUL
            String sqlMatkul = """
                CREATE TABLE IF NOT EXISTS matkul (
                    id_matkul      INTEGER PRIMARY KEY AUTOINCREMENT,
                    nama_matkul    TEXT NOT NULL,
                    dosen          TEXT,
                    warna          TEXT
                );
                """;

            // Tabel TUGAS
            String sqlTugas = """
                CREATE TABLE IF NOT EXISTS tugas (
                    id_tugas        INTEGER PRIMARY KEY AUTOINCREMENT,
                    judul           TEXT NOT NULL,
                    deskripsi       TEXT,
                    deadline        TEXT NOT NULL,
                    matkul_id       INTEGER,
                    selesai         INTEGER DEFAULT 0,
                    tanggal_dibuat  TEXT NOT NULL,
                    FOREIGN KEY (matkul_id) REFERENCES matkul(id_matkul)
                );
                """;

            // Tabel KALENDER 
            String sqlKalender = """
                CREATE TABLE IF NOT EXISTS kalender (
                    id_kalender  INTEGER PRIMARY KEY AUTOINCREMENT,
                    tanggal      TEXT NOT NULL,
                    tugas_id     INTEGER NOT NULL,
                    FOREIGN KEY (tugas_id) REFERENCES tugas(id_tugas)
                );
                """;

            st.execute(sqlMatkul);
            st.execute(sqlTugas);
            st.execute(sqlKalender);

            System.out.println("InitDatabase: tabel siap 👍");

        } catch (SQLException e) {
            System.out.println("InitDatabase gagal: " + e.getMessage());
        }
    }

}
