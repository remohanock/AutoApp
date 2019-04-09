package com.example.autoapp.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autoapp.R;
import com.example.autoapp.adapters.ContactsAdapter;
import com.example.autoapp.models.Contacts;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static Context mContext;
    public static final int RequestPermissionCode = 1;
    ArrayList<Contacts> contactsArrayList;

    private RecyclerView rv_contacts;
    private TextView tv_no_contacts;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsFragment.
     */
    public static ContactsFragment newInstance(Context context) {
        mContext = context;
        return new ContactsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getContacts();
        } else {
            enableReadContactsPermission();
        }

    }

    private void getContacts() {
        Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            contactsArrayList = new ArrayList<>();
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String contactID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                Log.d("CONTACTS", contactID + ": " + name + "_" + phoneNumber);
                Contacts contacts = new Contacts(contactID, name, phoneNumber, "", "", "");
                contactsArrayList.add(contacts);
            }
            Collections.sort(contactsArrayList, (o1, o2) -> o1.getContactName().compareTo(o2.getContactName()));
            phones.close();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        rv_contacts = view.findViewById(R.id.rv_contacts);
        tv_no_contacts = view.findViewById(R.id.tv_no_contacts);
        rv_contacts.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
        ContactsAdapter adapter = new ContactsAdapter(contactsArrayList);
        rv_contacts.setAdapter(adapter);
        if (contactsArrayList.size() == 0) {
            tv_no_contacts.setVisibility(View.VISIBLE);
            rv_contacts.setVisibility(View.GONE);
        } else {
            tv_no_contacts.setVisibility(View.GONE);
            rv_contacts.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void enableReadContactsPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                (Activity) mContext,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(mContext, "Contact read permission is required for obtaining the contact details", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } /*else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
