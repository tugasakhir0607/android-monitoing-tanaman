package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.example.monitoringtanaman.config.adapter.MyAdapterGaleri;
import com.example.monitoringtanaman.config.item.ItemGaleri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import com.example.monitoringtanaman.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GaleriActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private String id_tb_tanaman;
    RecyclerView recyclerView;
    ArrayList<ItemGaleri> itemGaleriArrayList = new ArrayList<>();
    MyAdapterGaleri myAdapterGaleri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeri);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Galeri");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        id_tb_tanaman = b.getString("id_tb_tanaman");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == fab){
            Intent i = new Intent(this, GaleriTambahActivity.class);
            i.putExtra("id_tb_tanaman",id_tb_tanaman);
            startActivity(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        data();
    }

    private void data() {
        itemGaleriArrayList.clear();
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_tanaman",id_tb_tanaman);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.getGaleri(requestBody);
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
                        String id_tb_tanaman        = c.getString("id_tb_tanaman");
                        String foto         = c.getString("foto");
                        String deskripsi    = c.getString("deskripsi");
                        String waktu                = c.getString("waktu");

                        itemGaleriArrayList.add(new ItemGaleri(id_tb_tanaman, foto, deskripsi, waktu));
                    }
                    myAdapterGaleri = new MyAdapterGaleri(itemGaleriArrayList,GaleriActivity.this);
                    recyclerView.setAdapter(myAdapterGaleri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(GaleriActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GaleriActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(GaleriActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }
}
