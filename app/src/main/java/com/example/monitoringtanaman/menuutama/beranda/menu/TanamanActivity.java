package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.monitoringtanaman.MainActivity;
import com.example.monitoringtanaman.SignInActivity;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.example.monitoringtanaman.config.adapter.MyAdapterTanaman;
import com.example.monitoringtanaman.config.item.ItemTanaman;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.monitoringtanaman.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TanamanActivity extends AppCompatActivity implements View.OnClickListener {
    private String id_tb_tanaman, nama_tanaman, penyiraman_tanaman, foto_tanaman, pemupukan_tanaman, deskripsi_tanaman, stt_siram;
    private EditText et_nama, et_penyiraman, et_pemupukan, et_deskripsi;
    private Button btn_ubah, btn_simpan;
    private Switch sw_siram;
    private ImageView imv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanaman);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Tanaman Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       Bundle b = getIntent().getExtras();
       id_tb_tanaman = b.getString("id_tb_tanaman");

       imv = findViewById(R.id.imv);
        sw_siram = findViewById(R.id.sw_siram);
        et_nama = findViewById(R.id.et_nama);
        et_penyiraman = findViewById(R.id.et_penyiraman);
        et_pemupukan = findViewById(R.id.et_pemupukan);
        et_deskripsi = findViewById(R.id.et_deskripsi);
        btn_ubah = findViewById(R.id.btn_ubah);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_ubah.setOnClickListener(this);
        btn_simpan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ubah){
            Intent i = new Intent(this, TanamanUbahActivity.class);
            i.putExtra("id_tb_tanaman",id_tb_tanaman);
            startActivity(i);
        } else if (v == btn_simpan){
            if (sw_siram.isChecked()){
                popup("menyalakan penyiraman?", "1");
            } else {
                popup("mematikan penyiraman?","0");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_tanaman", id_tb_tanaman);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.getTanamanDetail(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");
                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        id_tb_tanaman   = c.getString("id_tb_tanaman");
                        nama_tanaman        = c.getString("nama_tanaman");
                        foto_tanaman        = c.getString("foto_tanaman");
                        penyiraman_tanaman  = c.getString("penyiraman_tanaman");
                        pemupukan_tanaman   = c.getString("pemupukan_tanaman");
                        deskripsi_tanaman   = c.getString("deskripsi_tanaman");
                        stt_siram         = c.getString("stt_siram");

                        et_nama.setText(nama_tanaman);
                        et_penyiraman.setText(penyiraman_tanaman);
                        et_pemupukan.setText(pemupukan_tanaman);
                        et_deskripsi.setText(deskripsi_tanaman);
                        Glide.with(getApplicationContext()).load(Config.URL_FOTO_TANAMAN+foto_tanaman)
                                .error(R.mipmap.not_found).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).centerCrop().into(imv);

                        if (stt_siram.equals("1")){
                            sw_siram.setChecked(true);
                        } else {
                            sw_siram.setChecked(false);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TanamanActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TanamanActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(TanamanActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void popup(String status,String stt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Apakah anda yakin akan "+status);
        builder.setIcon(R.mipmap.motan3);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                send(stt);
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (stt_siram.equals("1")){
                    sw_siram.setChecked(true);
                } else {
                    sw_siram.setChecked(false);
                }
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void send(String stt){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_tanaman", id_tb_tanaman);
        builder.addFormDataPart("stt_siram", stt);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.updatePenyiraman(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");
                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        String status   = c.getString("status");
                        if (status.equals("1")){
                            if (stt.equals("1")){
                                stt_siram = "1";
                                sw_siram.setChecked(true);
                            } else {
                                stt_siram = "0";
                                sw_siram.setChecked(false);
                            }
                            Toast.makeText(TanamanActivity.this, "Berhasil mengubah penyiraman!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(TanamanActivity.this, "Gagal mengubah penyiraman!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TanamanActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TanamanActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(TanamanActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }
}
