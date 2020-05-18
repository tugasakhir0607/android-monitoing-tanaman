package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class GaleriDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_hapus;
    private ImageView imv;
    private EditText et_deskripsi;
    String id_tb_galeri, foto, deskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeri_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Galeri Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        id_tb_galeri = b.getString("id_tb_galeri");
        foto = b.getString("foto");
        deskripsi = b.getString("deskripsi");

        imv = findViewById(R.id.imv);
        et_deskripsi = findViewById(R.id.et_deskripsi);
        btn_hapus = findViewById(R.id.btn_hapus);
        btn_hapus.setOnClickListener(this);

        et_deskripsi.setText(deskripsi);
        Glide.with(getApplicationContext()).load(Config.URL_FOTO_GALERI+foto)
                .error(R.mipmap.not_found).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).centerCrop().into(imv);

    }

    @Override
    public void onClick(View v) {
        if (v == btn_hapus){
            popup();
        }
    }

    private void popup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Apakah anda yakin akan menghapus?");
        builder.setIcon(R.mipmap.motan3);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                send();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void send(){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_galeri", id_tb_galeri);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.deleteGaleri(requestBody);
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
                            Toast.makeText(GaleriDetailActivity.this, "Berhasil menghapus galeri!", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(GaleriDetailActivity.this, "Gagal menghapus galeri!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(GaleriDetailActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GaleriDetailActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(GaleriDetailActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }
}
