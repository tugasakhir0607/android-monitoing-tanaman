package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;
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

public class EvaluasiEditActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_simpan;
    EditText et_saran, et_keterangan;
    String id_tb_tanaman, saran, keterangan, foto;
    ImageView imv;
    FloatingActionButton fab;
    private String urlPath;
    public static final int REQUEST_CODE_CAMERA = 001;
    public static final int REQUEST_CODE_GALLERY = 002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluasi_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Evaluasi Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        id_tb_tanaman = b.getString("id_tb_tanaman");
        saran = b.getString("saran");
        keterangan = b.getString("keterangan");
        foto = b.getString("foto");

        et_keterangan = findViewById(R.id.et_keterangan);
        et_saran = findViewById(R.id.et_saran);
        fab = findViewById(R.id.fab);
        imv = findViewById(R.id.imv);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_simpan.setOnClickListener(this);
        fab.setOnClickListener(this);

        if (!keterangan.equals("null")){
            et_keterangan.setText(keterangan);
        }
        if (!saran.equals("null")){
            et_saran.setText(saran);
        }
        if (!foto.equals("null")){
            Glide.with(this).load(Config.URL_FOTO_EVALUASI+foto)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f)
                    .centerCrop().into(imv);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_simpan){
            simpan();
        } else if (v == fab){
            popUp();
        }
    }

    private void popUp(){
        new AlertDialog.Builder(this)
                .setTitle("Pilih")
                .setPositiveButton("Galeri", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EasyImage.openGallery(EvaluasiEditActivity.this,REQUEST_CODE_GALLERY);
                    }
                })
                .setNegativeButton("Kamera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EasyImage.openCameraForImage(EvaluasiEditActivity.this,REQUEST_CODE_CAMERA);
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
                        Glide.with(EvaluasiEditActivity.this).load(list.get(0).getAbsolutePath()).centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imv);
                        urlPath = list.get(0).getAbsolutePath();
                        break;
                }
            }
        });
    }

    private void simpan() {
        et_saran.setError(null);
        et_keterangan.setError(null);
        String saran = et_saran.getText().toString();
        String keterangan = et_keterangan.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(saran)){
            et_saran.setError("Silahkann diisi..");
            focusView = et_saran;
            cancel = true;
        }
        if (TextUtils.isEmpty(keterangan)){
            et_keterangan.setError("Silahkann diisi..");
            focusView = et_keterangan;
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
                builder.addFormDataPart("id_tb_tanaman",id_tb_tanaman);
                builder.addFormDataPart("saran_evaluasi",saran);
                builder.addFormDataPart("keterangan_evaluasi",keterangan);
                builder.addFormDataPart("foto_lama",foto);
                File file = Config.setFileResize(urlPath,1);
                builder.addFormDataPart("foto", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                MultipartBody requestBody = builder.build();

                UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
                Call<ResponseBody> post = api.updateEvaluasi(requestBody);
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
                                    Toast.makeText(EvaluasiEditActivity.this, "Berhasil mengubah data.", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(EvaluasiEditActivity.this, "Gagal mengubah data!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(EvaluasiEditActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EvaluasiEditActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pDialog.dismiss();
                        Toast.makeText(EvaluasiEditActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}
