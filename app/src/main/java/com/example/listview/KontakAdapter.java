package com.example.listview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class KontakAdapter extends ArrayAdapter<Kontak> {
    private boolean selectingDelete;
    private boolean selectingEdit;
    public Kontak selected_edit;

    // View lookup cache
    private static class ViewHolder {
        TextView nama;
        TextView nohp;
    }

    public void setSelectingEdit(boolean selectingEdit) {
        this.selectingEdit = selectingEdit;
    }

    public boolean getSelectingEdit() {
        return this.selectingEdit;
    }

    public void setSelectingDelete(boolean selectingDelete)
    {
        this.selectingDelete = selectingDelete;
    }

    public boolean getSelectingDelete()
    {
        return this.selectingDelete;
    }

    public KontakAdapter(Context context, int resource, List<Kontak> objects) {
        super(context, resource, objects);
        selectingDelete = false;
        selectingEdit = false;
        selected_edit = null;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Kontak dtkontak = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewKontak; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewKontak = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            viewKontak.nama = convertView.findViewById(R.id.tNama);
            viewKontak.nohp = convertView.findViewById(R.id.tnoHp);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewKontak);
        } else {
            viewKontak = (ViewHolder) convertView.getTag();
        }

        // Check if the item is selected
        if (dtkontak.isSelected()) {
            // Apply selected state appearance
            convertView.setBackgroundColor(Color.parseColor("#FF0000"));
        } else {
            // Apply default state appearance
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        if (!selectingDelete) {
            dtkontak.setSelected(false);
        }

        viewKontak.nama.setText(dtkontak.getNama() + " - ");
        viewKontak.nohp.setText(dtkontak.getNoHp());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectingDelete) {
                    // Toggle the selected state of the contact
                    dtkontak.setSelected(!dtkontak.isSelected());
                    // Update the view to reflect the new selected state
                    notifyDataSetChanged();
                } else if (selectingEdit) {
                    selected_edit = dtkontak;
                    notifyDataSetChanged();
                    ((MainActivity) getContext()).edit_dialog(selected_edit);
                }
            }
        });

        return convertView;
    }

    public List<Kontak> getSelectedContacts() {
        // Create a list to store selected contacts
        List<Kontak> selectedContacts = new ArrayList<>();

        // Iterate through the items in the adapter
        for (int i = 0; i < getCount(); i++) {
            // Get the contact at the current position
            Kontak contact = getItem(i);

            // Check if the contact is selected
            if (contact.isSelected()) {
                // If selected, add it to the list of selected contacts
                selectedContacts.add(contact);
            }
        }

        // Return the list of selected contacts
        return selectedContacts;
    }
}
