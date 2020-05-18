package com.example.monitoringtanaman.config.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.monitoringtanaman.R;
import com.example.monitoringtanaman.config.Config;
import com.example.monitoringtanaman.config.item.ItemGaleri;
import com.example.monitoringtanaman.config.item.ItemTanaman;
import com.example.monitoringtanaman.menuutama.beranda.BerandaDetailActivity;
import com.example.monitoringtanaman.menuutama.beranda.menu.GaleriDetailActivity;

import java.util.ArrayList;

public class MyAdapterGaleri extends RecyclerView.Adapter<MyAdapterGaleri.ViewHolder>{
    private ArrayList<ItemGaleri> itemGaleris;
    private Context context;

    public MyAdapterGaleri(final ArrayList<ItemGaleri> itemGaleris, Context context){
        this.itemGaleris = itemGaleris;
        this.context    = context;
    }

    @Override
    public MyAdapterGaleri.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_galeri,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapterGaleri.ViewHolder holder, int position) {
        holder.tv_deskripsi.setText(itemGaleris.get(position).getDeskripsi());
        Glide.with(context).load(Config.URL_FOTO_GALERI+itemGaleris.get(position).getFoto())
                .error(R.mipmap.not_found).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).centerCrop().into(holder.imv);
    }

    @Override
    public int getItemCount() {
        return itemGaleris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_deskripsi;
        CardView cardView;
        ImageView imv;

        public ViewHolder(View view){
            super(view);
            imv  = view.findViewById(R.id.imv);
            tv_deskripsi  = view.findViewById(R.id.tv_deskripsi);
            cardView = view.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, GaleriDetailActivity.class);
            intent.putExtra("id_tb_galeri",itemGaleris.get(getAdapterPosition()).getId_tb_tanaman());
            intent.putExtra("foto",itemGaleris.get(getAdapterPosition()).getFoto());
            intent.putExtra("deskripsi",itemGaleris.get(getAdapterPosition()).getDeskripsi());
            context.startActivity(intent);
        }
    }
}
