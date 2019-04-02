package com.example.autoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.autoapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class PlaylistSpinnerAdapter extends BaseAdapter {

    List<String> playlist = new ArrayList<>();

    public PlaylistSpinnerAdapter(List<String>playlist){
        this.playlist = playlist;
    }
    @Override
    public int getCount() {
        return playlist.size();
    }

    @Override
    public Object getItem(int position) {
        return playlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_list_item_row, parent, false);
        TextView tv_title = convertView.findViewById(R.id.playlist_title);
        tv_title.setText(playlist.get(position));
        return convertView;
    }
}
