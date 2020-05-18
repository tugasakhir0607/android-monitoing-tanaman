package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import com.example.monitoringtanaman.R;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenyiramanActivity extends AppCompatActivity {
    TextView tv_no, tv_pompa, tv_waktu;
    TableLayout tableLayout;
    private String id_tb_tanaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penyiraman);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Menu Penyiraman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        id_tb_tanaman = b.getString("id_tb_tanaman");

        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
    }

    @Override
    public void onResume() {
        super.onResume();
        data();
    }

    private void data() {
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
        Call<ResponseBody> post = api.getPenyiraman(requestBody);
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
                        String pompa   = c.getString("pompa");
                        String waktu   = c.getString("waktu");

                        setTabelBody(i, pompa, waktu);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setTabelBody(int no, String pompa, String waktu) {
        tv_no           = new TextView(getApplicationContext());
        tv_pompa        = new TextView(getApplicationContext());
        tv_waktu        = new TextView(getApplicationContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        params.setMargins(1,1,1,1);
        params.gravity= Gravity.CENTER;

        tv_no.setTextColor(Color.BLACK);
        tv_no.setText(String.valueOf(no+1));
        tv_no.setBackgroundResource(R.drawable.chell_shape);
        tv_no.setGravity(Gravity.CENTER );

        tv_pompa.setTextColor(Color.BLACK);
        tv_pompa.setText(pompa);
        tv_pompa.setBackgroundResource(R.drawable.chell_shape);
        tv_pompa.setGravity(Gravity.CENTER );

        tv_waktu.setTextColor(Color.BLACK);
        tv_waktu.setText(waktu);
        tv_waktu.setBackgroundResource(R.drawable.chell_shape);
        tv_waktu.setGravity(Gravity.CENTER );

        TableRow row = new TableRow(getApplicationContext());
        row.addView(tv_no);
        row.addView(tv_pompa);
        row.addView(tv_waktu);

        tableLayout.addView(row);
    }
}
