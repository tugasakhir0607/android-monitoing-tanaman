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
import com.example.monitoringtanaman.config.item.ItemTanaman;
import com.example.monitoringtanaman.menuutama.beranda.BerandaDetailActivity;

import java.util.ArrayList;

public class MyAdapterTanaman extends RecyclerView.Adapter<MyAdapterTanaman.ViewHolder>{
    private ArrayList<ItemTanaman> itemTanamen;
    private Context context;

    public MyAdapterTanaman(final ArrayList<ItemTanaman> itemTanamen, Context context){
        this.itemTanamen  = itemTanamen;
        this.context    = context;
    }

    @Override
    public MyAdapterTanaman.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_tanaman,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapterTanaman.ViewHolder holder, int position) {
        holder.tv_nama.setText(itemTanamen.get(position).getNama_tanaman());
        Glide.with(context).load(Config.URL_FOTO_TANAMAN+itemTanamen.get(position).getFoto_tanaman())
                .error(R.mipmap.not_found).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).centerCrop().into(holder.imv);
    }

    @Override
    public int getItemCount() {
        return itemTanamen.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_nama;
        CardView cardView;
        ImageView imv;

        public ViewHolder(View view){
            super(view);
            imv  = view.findViewById(R.id.imv);
            tv_nama  = view.findViewById(R.id.tv_nama);
            cardView = view.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, BerandaDetailActivity.class);
            intent.putExtra("id_tb_tanaman",itemTanamen.get(getAdapterPosition()).getId_tb_tanaman());
            intent.putExtra("nama_tanaman",itemTanamen.get(getAdapterPosition()).getNama_tanaman());
            context.startActivity(intent);
        }
    }
}
