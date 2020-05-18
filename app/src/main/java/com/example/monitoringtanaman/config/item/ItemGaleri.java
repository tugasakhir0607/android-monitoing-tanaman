package com.example.monitoringtanaman.config.item;

public class ItemGaleri {
    String id_tb_tanaman, foto, deskripsi, waktu;

    public ItemGaleri(String id_tb_tanaman, String foto, String deskripsi, String waktu){
        this.id_tb_tanaman = id_tb_tanaman;
        this.foto = foto;
        this.deskripsi = deskripsi;
        this.waktu = waktu;
    }

    public String getId_tb_tanaman() {
        return id_tb_tanaman;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getFoto() {
        return foto;
    }

    public String getWaktu() {
        return waktu;
    }
}
