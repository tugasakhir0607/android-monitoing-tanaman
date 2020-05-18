package com.example.monitoringtanaman.config.item;

public class ItemTanaman {
    String id_tb_tanaman, nama_tanaman, foto_tanaman, penyiraman_tanaman, pemupukan_tanaman, deskripsi_tanaman, waktu;

    public ItemTanaman(String id_tb_tanaman, String nama_tanaman, String foto_tanaman, String penyiraman_tanaman,
                       String pemupukan_tanaman, String deskripsi_tanaman, String waktu){
        this.id_tb_tanaman = id_tb_tanaman;
        this.nama_tanaman = nama_tanaman;
        this.foto_tanaman = foto_tanaman;
        this.penyiraman_tanaman = penyiraman_tanaman;
        this.pemupukan_tanaman = pemupukan_tanaman;
        this.deskripsi_tanaman = deskripsi_tanaman;
        this.waktu = waktu;
    }

    public String getId_tb_tanaman() {
        return id_tb_tanaman;
    }

    public String getNama_tanaman() {
        return nama_tanaman;
    }

    public String getFoto_tanaman() {
        return foto_tanaman;
    }

    public String getPenyiraman_tanaman() {
        return penyiraman_tanaman;
    }

    public String getPemupukan_tanaman() {
        return pemupukan_tanaman;
    }

    public String getDeskripsi_tanaman() {
        return deskripsi_tanaman;
    }

    public String getWaktu() {
        return waktu;
    }
}
