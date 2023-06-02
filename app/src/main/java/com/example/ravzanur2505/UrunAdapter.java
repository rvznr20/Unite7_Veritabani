package com.example.ravzanur2505;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UrunAdapter extends ArrayAdapter<Urun> {
    ArrayList<Urun> urunler;
    Context context;

    public UrunAdapter(@NonNull Context context, ArrayList<Urun> urunler) {
        super(context, 0, urunler);
        this.context = context;
        this.urunler = urunler;
    }


    @Override
    public int getCount() {
        return urunler.size();
    }

    @Override
    public Urun getItem(int i) {
        return urunler.get(i);
    }

    @Override
    public long getItemId(int i) {
        return urunler.get(i).hashCode();
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView satirUrunAdi;
        TextView satirUrunFiyat;
        TextView satirUrunAdet;
        ImageView satirUrunResmi;
        Urun urun = urunler.get(i);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.uyg3_listview_satir, null);
        }
        satirUrunAdi = view.findViewById(R.id.satirUrunAdi);
        satirUrunFiyat = view.findViewById(R.id.satirUrunFiyat);
        satirUrunAdet = view.findViewById(R.id.satirUrunAdet);
        satirUrunResmi = view.findViewById(R.id.satirUrunResmi);

        satirUrunAdi.setText(urun.getUrunAdi());
        satirUrunFiyat.setText(String.format("%.02f", urun.getFiyat()) + "TL");
        satirUrunAdet.setText(urun.getAdet() + "");
        satirUrunResmi.setImageResource(urun.getResim());


        return view;


    }
}
