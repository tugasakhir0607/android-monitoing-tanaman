package com.example.monitoringtanaman.menuutama.profil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.monitoringtanaman.MainActivity;
import com.example.monitoringtanaman.SignInActivity;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.SharePreference;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.monitoringtanaman.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilUbahActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private String id_tb_pengguna, nama, username, email, nohp, sex, alamat;
    private SharePreference sharePreference;
    private EditText et_nama, et_username, et_email, et_nohp, et_alamat;
    private RadioGroup rg_sex;
    private Button btn_simpan, btn_batal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_ubah);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Profil Ubah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sharePreference = new SharePreference(this);
        HashMap<String, String> user = sharePreference.getUserDetails();
        id_tb_pengguna = user.get(sharePreference.KEY_ID_PENGGUNA);
        nama = user.get(sharePreference.KEY_NAMA_PENGGUNA);
        username = user.get(sharePreference.KEY_USERNAME_PENGGUNA);
        email = user.get(sharePreference.KEY_EMAIL_PENGGUNA);
        nohp = user.get(sharePreference.KEY_NOHP_PENGGUNA);
        sex = user.get(sharePreference.KEY_SEX_PENGGUNA);
        alamat = user.get(sharePreference.KEY_ALAMAT_PENGGUNA);

        et_nama = findViewById(R.id.et_nama);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_nohp = findViewById(R.id.et_nohp);
        et_alamat = findViewById(R.id.et_alamat);
        rg_sex = findViewById(R.id.rg_sex);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_batal = findViewById(R.id.btn_batal);
        btn_simpan.setOnClickListener(this);
        btn_batal.setOnClickListener(this);
        rg_sex.setOnCheckedChangeListener(this);

        et_nama.setText(nama);
        et_username.setText(username);
        et_email.setText(email);
        et_nohp.setText(nohp);
        et_alamat.setText(alamat);
        if (sex.equals("Laki-Laki")){
            ((RadioButton)rg_sex.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton)rg_sex.getChildAt(1)).setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_simpan){
            simpan();
        } else if (v == btn_batal){
            onBackPressed();
        }
    }

    private void simpan() {
        et_nama.setError(null);
        et_username.setError(null);
        et_email.setError(null);
        et_nohp.setError(null);
        et_alamat.setError(null);
        nama = et_nama.getText().toString();
        username = et_username.getText().toString();
        email = et_email.getText().toString();
        nohp = et_nohp.getText().toString();
        alamat = et_alamat.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nama)){
            et_nama.setError("Silahkann diisi..");
            focusView = et_nama;
            cancel = true;
        } else if (TextUtils.isEmpty(username)){
            et_username.setError("Silahkann diisi..");
            focusView = et_username;
            cancel = true;
        } else if (TextUtils.isEmpty(email)){
            et_email.setError("Silahkann diisi..");
            focusView = et_email;
            cancel = true;
        } else if (TextUtils.isEmpty(nohp)){
            et_nohp.setError("Silahkann diisi..");
            focusView = et_nohp;
            cancel = true;
        } else if (TextUtils.isEmpty(alamat)){
            et_alamat.setError("Silahkann diisi..");
            focusView = et_alamat;
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
            builder.addFormDataPart("id_tb_pengguna",id_tb_pengguna);
            builder.addFormDataPart("nama_pengguna",nama);
            builder.addFormDataPart("username_pengguna",username);
            builder.addFormDataPart("email_pengguna",email);
            builder.addFormDataPart("nohp_pengguna",nohp);
            builder.addFormDataPart("sex_pengguna",sex);
            builder.addFormDataPart("alamat_pengguna",alamat);
            MultipartBody requestBody = builder.build();

            UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
            Call<ResponseBody> post = api.updateProfil(requestBody);
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
                            String status = c.getString("status");

                            if (status.equals("1")) {
                                Toast.makeText(getApplicationContext(), "Berhasil Mengubah Profil.", Toast.LENGTH_LONG).show();
                                sharePreference.update(nama, username, email, nohp,  sex, alamat);
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal Mengubah Profil.", Toast.LENGTH_LONG).show();
                            }
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
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_laki_laki:
                sex = "Laki-Laki";
                break;
            case R.id.rb_perempuan:
                sex = "Perempuan";
                break;
        }
    }
}
