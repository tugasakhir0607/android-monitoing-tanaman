package com.example.monitoringtanaman.menuutama.profil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import com.example.monitoringtanaman.R;
import com.example.monitoringtanaman.config.SharePreference;

import java.util.HashMap;

public class ProfilFragment extends Fragment implements View.OnClickListener {
    private EditText et_nama, et_username, et_email, et_nohp, et_sex, et_alamat;
    private SharePreference sharePreference;
    private Button btn_ubah, btn_password;
    private String nama, username, email, nohp, sex, alamat;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profil, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharePreference = new SharePreference(getContext());

        et_nama = view.findViewById(R.id.et_nama);
        et_username = view.findViewById(R.id.et_username);
        et_email = view.findViewById(R.id.et_email);
        et_nohp = view.findViewById(R.id.et_nohp);
        et_sex = view.findViewById(R.id.et_sex);
        et_alamat = view.findViewById(R.id.et_alamat);
        btn_ubah = view.findViewById(R.id.btn_ubah);
        btn_password = view.findViewById(R.id.btn_password);
        btn_ubah.setOnClickListener(this);
        btn_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ubah){
            startActivity(new Intent(getContext(), ProfilUbahActivity.class));
        } else if (v == btn_password){
            startActivity(new Intent(getContext(), ProfilPasswordActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HashMap<String, String> user = sharePreference.getUserDetails();
        nama = user.get(sharePreference.KEY_NAMA_PENGGUNA);
        username = user.get(sharePreference.KEY_USERNAME_PENGGUNA);
        email = user.get(sharePreference.KEY_EMAIL_PENGGUNA);
        nohp = user.get(sharePreference.KEY_NOHP_PENGGUNA);
        sex = user.get(sharePreference.KEY_SEX_PENGGUNA);
        alamat = user.get(sharePreference.KEY_ALAMAT_PENGGUNA);

        et_nama.setText(nama);
        et_username.setText(username);
        et_email.setText(email);
        et_nohp.setText(nohp);
        et_sex.setText(sex);
        et_alamat.setText(alamat);
    }
}