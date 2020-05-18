package com.example.monitoringtanaman.menuutama.beranda;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.SharePreference;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitoringtanaman.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

public class BerandaTambahActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_nama, et_penyiraman, et_pemupukan, et_deskripsi;
    private Button btn_simpan;
    private String id_tb_pengguna;
    private SharePreference sharePreference;
    private FloatingActionButton fab;
    private ImageView imv;
    private String urlPath;
    public static final int REQUEST_CODE_CAMERA = 001;
    public static final int REQUEST_CODE_GALLERY = 002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda_tambah);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharePreference = new SharePreference(this);
        HashMap<String, String> user = sharePreference.getUserDetails();
        id_tb_pengguna = user.get(sharePreference.KEY_ID_PENGGUNA);

        getSupportActionBar().setTitle("Tambah Penanaman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fab = findViewById(R.id.fab);
        imv = findViewById(R.id.imv);
        et_nama = findViewById(R.id.et_nama);
        et_pemupukan = findViewById(R.id.et_pemupukan);
        et_penyiraman = findViewById(R.id.et_penyiraman);
        et_deskripsi = findViewById(R.id.et_deskripsi);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_simpan.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_simpan){
            simpan();
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
                        EasyImage.openGallery(BerandaTambahActivity.this,REQUEST_CODE_GALLERY);
                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EasyImage.openCameraForImage(BerandaTambahActivity.this,REQUEST_CODE_CAMERA);
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
                        Glide.with(BerandaTambahActivity.this).load(list.get(0).getAbsolutePath()).centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imv);
                        urlPath = list.get(0).getAbsolutePath();
                        break;
                }
            }
        });
    }

    private void simpan() {
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
            if (TextUtils.isEmpty(urlPath)) {
                Toast.makeText(this, "Silahkan pilih foto terlebih dahulu.", Toast.LENGTH_LONG).show();
            } else {
                final ProgressDialog pDialog = new ProgressDialog(this);
                pDialog.setMessage("Tunggu sebentar...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("id_tb_pengguna",id_tb_pengguna);
                builder.addFormDataPart("nama_tanaman",nama);
                builder.addFormDataPart("pemupukan_tanaman",pemupukan);
                builder.addFormDataPart("penyiraman_tanaman",penyiraman);
                builder.addFormDataPart("deskripsi_tanaman",deskripsi);
                File file = Config.setFileResize(urlPath,1);
                builder.addFormDataPart("foto", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                MultipartBody requestBody = builder.build();

                UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
                Call<ResponseBody> post = api.inputTanaman(requestBody);
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
                                    Toast.makeText(BerandaTambahActivity.this, "Berhasil menambahkan data.", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(BerandaTambahActivity.this, "Gagal menambahkan data!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(BerandaTambahActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BerandaTambahActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pDialog.dismiss();
                        Toast.makeText(BerandaTambahActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
