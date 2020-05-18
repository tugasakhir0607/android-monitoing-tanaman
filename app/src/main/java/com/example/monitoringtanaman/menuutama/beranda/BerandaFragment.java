package com.example.monitoringtanaman.menuutama.beranda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoringtanaman.R;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.SharePreference;
import com.example.monitoringtanaman.config.UserAPIServices;
import com.example.monitoringtanaman.config.adapter.MyAdapterTanaman;
import com.example.monitoringtanaman.config.item.ItemTanaman;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BerandaFragment extends Fragment implements View.OnClickListener {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ArrayList<ItemTanaman> itemTanamanArrayList = new ArrayList<>();
    MyAdapterTanaman myAdapterTanaman;
    SharePreference sharePreference;
    String id_tb_pengguna;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_beranda, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharePreference = new SharePreference(getContext());
        HashMap<String, String> user = sharePreference.getUserDetails();
        id_tb_pengguna = user.get(sharePreference.KEY_ID_PENGGUNA);

        fab = view.findViewById(R.id.fab_tambah);
        fab.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onClick(View v) {
        if (v == fab){
            startActivity(new Intent(getContext(), BerandaTambahActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        data();
    }

    private void data() {
        itemTanamanArrayList.clear();
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_pengguna",id_tb_pengguna);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.getTanaman(requestBody);
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
                        String nama_tanaman         = c.getString("nama_tanaman");
                        String foto_tanaman         = c.getString("foto_tanaman");
                        String penyiraman_tanaman   = c.getString("penyiraman_tanaman");
                        String pemupukan_tanaman    = c.getString("pemupukan_tanaman");
                        String deskripsi_tanaman    = c.getString("deskripsi_tanaman");
                        String waktu                = c.getString("waktu");

                        itemTanamanArrayList.add(new ItemTanaman(id_tb_tanaman, nama_tanaman, foto_tanaman, penyiraman_tanaman, pemupukan_tanaman, deskripsi_tanaman, waktu));
                    }
                    myAdapterTanaman = new MyAdapterTanaman(itemTanamanArrayList,getContext());
                    recyclerView.setAdapter(myAdapterTanaman);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(getContext(), "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }
}