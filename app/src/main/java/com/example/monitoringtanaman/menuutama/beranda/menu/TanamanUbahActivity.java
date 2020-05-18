package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.example.monitoringtanaman.menuutama.beranda.BerandaTambahActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.monitoringtanaman.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TanamanUbahActivity extends AppCompatActivity implements View.OnClickListener {
    private String id_tb_tanaman, nama_tanaman, penyiraman_tanaman, pemupukan_tanaman, deskripsi_tanaman, foto_tanaman;
    private EditText et_nama, et_penyiraman, et_pemupukan, et_deskripsi;
    private Button btn_simpan, btn_batal;
    private FloatingActionButton fab;
    private ImageView imv;
    private String urlPath;
    public static final int REQUEST_CODE_CAMERA = 001;
    public static final int REQUEST_CODE_GALLERY = 002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanaman_ubah);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Tanaman Ubah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        id_tb_tanaman = b.getString("id_tb_tanaman");

        fab = findViewById(R.id.fab);
        imv = findViewById(R.id.imv);
        et_nama = findViewById(R.id.et_nama);
        et_penyiraman = findViewById(R.id.et_penyiraman);
        et_pemupukan = findViewById(R.id.et_pemupukan);
        et_deskripsi = findViewById(R.id.et_deskripsi);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_batal = findViewById(R.id.btn_batal);
        btn_simpan.setOnClickListener(this);
        btn_batal.setOnClickListener(this);
        fab.setOnClickListener(this);
        getData();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_simpan){
            simpan();
        } else if (v == btn_batal){
            onBackPressed();
        } else if (v == fab){
            popup();
        }
    }

    private void popup(){
        new AlertDialog.Builder(this)
                .setTitle("Pilih")
                .setPositiveButton("Galeri", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EasyImage.openGallery(TanamanUbahActivity.this,REQUEST_CODE_GALLERY);
                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EasyImage.openCameraForImage(TanamanUbahActivity.this,REQUEST_CODE_CAMERA);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> list, EasyImage.ImageSource imageSource, int type) {
                switch (type){
                    case REQUEST_CODE_GALLERY:
                    case REQUEST_CODE_CAMERA:
                        Glide.with(TanamanUbahActivity.this).load(list.get(0).getAbsolutePath()).centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imv);
                        urlPath = list.get(0).getAbsolutePath();
                        break;
                }
            }
        });
    }

    private void simpan(){
        et_nama.setError(null);
        et_penyiraman.setError(null);
        et_pemupukan.setError(null);
        et_deskripsi.setError(null);
        String nama = et_nama.getText().toString();
        String penyiraman = et_penyiraman.getText().toString();
        String pemupukan = et_pemupukan.getText().toString();
        String deskripsi = et_deskripsi.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nama)){
            et_nama.setError("Silahkann diisi..");
            focusView = et_nama;
            cancel = true;
        } else if (TextUtils.isEmpty(penyiraman)){
            et_penyiraman.setError("Silahkann diisi..");
            focusView = et_penyiraman;
            cancel = true;
        } else if (TextUtils.isEmpty(pemupukan)){
            et_pemupukan.setError("Silahkann diisi..");
            focusView = et_pemupukan;
            cancel = true;
        } else if (TextUtils.isEmpty(deskripsi)){
            et_deskripsi.setError("Silahkann diisi..");
            focusView = et_deskripsi;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Tunggu sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("id_tb_tanaman",id_tb_tanaman);
            builder.addFormDataPart("nama_tanaman",nama);
            builder.addFormDataPart("pemupukan_tanaman",pemupukan);
            builder.addFormDataPart("penyiraman_tanaman",penyiraman);
            builder.addFormDataPart("deskripsi_tanaman",deskripsi);
            if (!TextUtils.isEmpty(urlPath)) {
                builder.addFormDataPart("foto_lama",foto_tanaman);
                File file = Config.setFileResize(urlPath,1);
                builder.addFormDataPart("foto", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            }
            MultipartBody requestBody = builder.build();

            UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
            Call<ResponseBody> post = api.updateTanaman(requestBody);
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
                                Toast.makeText(TanamanUbahActivity.this, "Berhasil mengubah data.", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(TanamanUbahActivity.this, "Gagal mengubah data!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(TanamanUbahActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(TanamanUbahActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pDialog.dismiss();
                    Toast.makeText(TanamanUbahActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
                }
            });
        }
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
                        id_tb_tanaman        = c.getString("id_tb_tanaman");
                        nama_tanaman         = c.getString("nama_tanaman");
                        foto_tanaman         = c.getString("foto_tanaman");
                        penyiraman_tanaman   = c.getString("penyiraman_tanaman");
                        pemupukan_tanaman    = c.getString("pemupukan_tanaman");
                        deskripsi_tanaman    = c.getString("deskripsi_tanaman");

                        et_nama.setText(nama_tanaman);
                        et_penyiraman.setText(penyiraman_tanaman);
                        et_pemupukan.setText(pemupukan_tanaman);
                        et_deskripsi.setText(deskripsi_tanaman);

                        Glide.with(getApplicationContext()).load(Config.URL_FOTO_TANAMAN+foto_tanaman)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).centerCrop().into(imv);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TanamanUbahActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TanamanUbahActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(TanamanUbahActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }
}
