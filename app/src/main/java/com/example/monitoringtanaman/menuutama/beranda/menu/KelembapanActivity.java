package com.example.monitoringtanaman.menuutama.beranda.menu;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.UserAPIServices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monitoringtanaman.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KelembapanActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_no, tv_kelembapan, tv_keterangan, tv_waktu;
    TableLayout tableLayout;
    ProgressBar progress;
    Button btn_more;
    int total_page = 0, offset = 20, no=0;
    private BarChart chart ;
    private String id_tb_tanaman;
    ArrayList NoOfEmp = new ArrayList();
    ArrayList year = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelembapan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Menu Kelembapan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        id_tb_tanaman = b.getString("id_tb_tanaman");

        progress = (ProgressBar) findViewById(R.id.progress);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        chart  = findViewById(R.id.chart);
        btn_more = (Button) findViewById(R.id.btn_more);

        btn_more.setOnClickListener(this);
        getGrafik();
    }



    private void getGrafik() {
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
        Call<ResponseBody> post = api.getGrafikKelembapan(requestBody);
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
                        String wkt  = c.getString("wkt");
                        String jml  = c.getString("jml");

                        NoOfEmp.add(new BarEntry(Integer.valueOf(jml), i));
                        year.add(wkt);
                    }

                    BarDataSet bardataset = new BarDataSet(NoOfEmp, "Hari");
                    chart.animateY(3000);
                    BarData data = new BarData(year, bardataset);
                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    chart.setData(data);
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
        builder.addFormDataPart("offset", String.valueOf(offset));
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.getKelembaban(requestBody);
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
                        String kelembapan   = c.getString("kelembapan");
                        String keterangan   = c.getString("keterangan");
                        String waktu        = c.getString("waktu");

                        setTabelBody(no++, kelembapan, keterangan, waktu);
                    }
                    offset += 20;
                    total_page = jsonObj.getInt("total");
                    Log.d("catatan", String.valueOf(offset));
                    if (offset<total_page){
                        btn_more.setVisibility(View.VISIBLE);
                    } else {
                        btn_more.setVisibility(View.GONE);
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

    private void setTabelBody(int no, String kelembaban, String keterangan, String waktu) {
        tv_no           = new TextView(getApplicationContext());
        tv_kelembapan   = new TextView(getApplicationContext());
        tv_keterangan   = new TextView(getApplicationContext());
        tv_waktu        = new TextView(getApplicationContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        params.setMargins(1,1,1,1);
        params.gravity= Gravity.CENTER;

        tv_no.setTextColor(Color.BLACK);
        tv_no.setText(String.valueOf(no+1));
        tv_no.setBackgroundResource(R.drawable.chell_shape);
        tv_no.setGravity(Gravity.CENTER );

        tv_kelembapan.setTextColor(Color.BLACK);
        tv_kelembapan.setText(kelembaban);
        tv_kelembapan.setBackgroundResource(R.drawable.chell_shape);
        tv_kelembapan.setGravity(Gravity.CENTER );

        tv_keterangan.setTextColor(Color.BLACK);
        tv_keterangan.setText(keterangan);
        tv_keterangan.setBackgroundResource(R.drawable.chell_shape);
        tv_keterangan.setGravity(Gravity.CENTER );

        tv_waktu.setTextColor(Color.BLACK);
        tv_waktu.setText(waktu);
        tv_waktu.setBackgroundResource(R.drawable.chell_shape);
        tv_waktu.setGravity(Gravity.CENTER );

        TableRow row = new TableRow(getApplicationContext());
        row.addView(tv_no);
        row.addView(tv_kelembapan);
        row.addView(tv_keterangan);
        row.addView(tv_waktu);

        tableLayout.addView(row);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_more){
            if (offset<total_page){
                data();
            }
        }
    }
}
