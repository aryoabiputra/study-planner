/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package studyplanner.model;

/**
 *
 * @author aryop
 */
public class Matkul {
    private int idMatkul;
    private String namaMatkul;
    private String dosen;
    private String warna; // bisa null

    public Matkul() {
    }

    public Matkul(int idMatkul, String namaMatkul, String dosen, String warna) {
        this.idMatkul = idMatkul;
        this.namaMatkul = namaMatkul;
        this.dosen = dosen;
        this.warna = warna;
    }

    public Matkul(String namaMatkul, String dosen, String warna) {
        this.namaMatkul = namaMatkul;
        this.dosen = dosen;
        this.warna = warna;
    }

    public int getIdMatkul() {
        return idMatkul;
    }

    public void setIdMatkul(int idMatkul) {
        this.idMatkul = idMatkul;
    }

    public String getNamaMatkul() {
        return namaMatkul;
    }

    public void setNamaMatkul(String namaMatkul) {
        this.namaMatkul = namaMatkul;
    }

    public String getDosen() {
        return dosen;
    }

    public void setDosen(String dosen) {
        this.dosen = dosen;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    @Override
    public String toString() {
        return namaMatkul; // biar enak kalau ditaruh di JComboBox
    }
}
