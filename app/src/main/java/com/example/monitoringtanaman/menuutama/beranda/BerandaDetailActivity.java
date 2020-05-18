package com.example.monitoringtanaman.menuutama.beranda;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;

import com.example.monitoringtanaman.R;
import com.example.monitoringtanaman.menuutama.beranda.menu.GaleriActivity;
import com.example.monitoringtanaman.menuutama.beranda.menu.KelembapanActivity;
import com.example.monitoringtanaman.menuutama.beranda.menu.PenyiramanActivity;
import com.example.monitoringtanaman.menuutama.beranda.menu.TanamanActivity;

public class BerandaDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView card_tanaman, card_kelembaban, card_penyiraman, card_galeri;
    private String id_tb_tanaman, nama_tanaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        id_tb_tanaman = b.getString("id_tb_tanaman");
        nama_tanaman = b.getString("nama_tanaman");

        getSupportActionBar().setTitle("Menu Tanaman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        card_tanaman = findViewById(R.id.card_tanaman);
        card_kelembaban = findViewById(R.id.card_kelembaban);
        card_penyiraman = findViewById(R.id.card_penyiraman);
        card_galeri = findViewById(R.id.card_galeri);
        card_tanaman.setOnClickListener(this);
        card_kelembaban.setOnClickListener(this);
        card_penyiraman.setOnClickListener(this);
        card_galeri.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == card_tanaman){
            Intent i = new Intent(this, TanamanActivity.class);
            i.putExtra("id_tb_tanaman",id_tb_tanaman);
            startActivity(i);
        } else if (v == card_kelembaban){
            Intent i = new Intent(this, KelembapanActivity.class);
            i.putExtra("id_tb_tanaman",id_tb_tanaman);
            startActivity(i);
        } else if (v == card_penyiraman){
            Intent i = new Intent(this, PenyiramanActivity.class);
            i.putExtra("id_tb_tanaman",id_tb_tanaman);
            startActivity(i);
        } else if (v == card_galeri){
            Intent i = new Intent(this, GaleriActivity.class);
            i.putExtra("id_tb_tanaman",id_tb_tanaman);
            startActivity(i);
        }
    }
}
