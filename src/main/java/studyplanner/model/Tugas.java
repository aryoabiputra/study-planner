/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studyplanner.model;

/**
 *
 * @author aryop
 */
public class Tugas {
 private int idTugas;
    private String judul;
    private String deskripsi;
    private String deadline;       // "2025-11-30"
    private Integer matkulId;      // boleh null
    private boolean selesai;
    private String tanggalDibuat;  // created_at

    // Kolom baru (sesuai requirement)
    private String tipe;           // tugas / kuis / ujian
    private int prioritas;         // 1 (tinggi) - 3 (rendah)
    private String status;         // TODO / DOING / DONE
    private String updatedAt;      // tanggal terakhir diubah

    public Tugas() {
    }

    // Constructor praktis (boleh kamu pakai atau nggak)
    public Tugas(String judul,
                String deskripsi,
                String deadline,
                Integer matkulId,
                boolean selesai,
                String tanggalDibuat,
                String tipe,
                int prioritas,
                String status,
                String updatedAt) {

        this.judul = judul;
        this.deskripsi = deskripsi;
        this.deadline = deadline;
        this.matkulId = matkulId;
        this.selesai = selesai;
        this.tanggalDibuat = tanggalDibuat;
        this.tipe = tipe;
        this.prioritas = prioritas;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public int getIdTugas() {
        return idTugas;
    }

    public void setIdTugas(int idTugas) {
        this.idTugas = idTugas;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Integer getMatkulId() {
        return matkulId;
    }

    public void setMatkulId(Integer matkulId) {
        this.matkulId = matkulId;
    }

    public boolean isSelesai() {
        return selesai;
    }

    public void setSelesai(boolean selesai) {
        this.selesai = selesai;
    }

    public String getTanggalDibuat() {
        return tanggalDibuat;
    }

    public void setTanggalDibuat(String tanggalDibuat) {
        this.tanggalDibuat = tanggalDibuat;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public int getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(int prioritas) {
        this.prioritas = prioritas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
