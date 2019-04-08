package com.example.autoapp.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.autoapp.R;
import com.example.autoapp.models.Contacts;

import java.util.ArrayList;
import java.util.Random;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private ArrayList<Contacts> contacts;

    public ContactsAdapter(ArrayList<Contacts> contacts) {
        this.contacts = contacts;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.contactName.setText(contacts.get(i).getContactName());
        viewHolder.contactNumber.setText(contacts.get(i).getContactNumber());
        viewHolder.tv_contact_image.setText(contacts.get(i).getContactName().charAt(0)+"");
        viewHolder.tv_contact_image.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_contact_image;
        private TextView contactName, contactNumber;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_contact_image = itemView.findViewById(R.id.tv_contact_image);
            contactName = itemView.findViewById(R.id.contactName);
            contactNumber = itemView.findViewById(R.id.contactNumber);
        }
    }
}
