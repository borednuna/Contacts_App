package com.example.listview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private ImageView add;
    private ImageView search;
    private ImageView refresh;
    private ImageView delete;
    private ImageView edit;
    private KontakAdapter kAdapter;
    private SQLiteDatabase dbku;
    private SQLiteOpenHelper dbopen;
    private Snackbar delete_snackbar;
    private Snackbar edit_snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView);
        add = (ImageView) findViewById(R.id.tambah);
        search = (ImageView) findViewById(R.id.search);
        refresh = (ImageView) findViewById(R.id.refresh);
        delete = (ImageView) findViewById(R.id.deleteButton);
        edit = (ImageView) findViewById(R.id.editButton);

        add.setOnClickListener(operasi);
        search.setOnClickListener(operasi);
        refresh.setOnClickListener(operasi);
        delete.setOnClickListener(operasi);
        edit.setOnClickListener(operasi);

        ArrayList<Kontak> listKontak = new ArrayList<Kontak>();
        kAdapter = new KontakAdapter(this,0,listKontak);
        lv.setAdapter(kAdapter);

        dbopen = new SQLiteOpenHelper(this,"kontak.db",null,1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
        };

        dbku = dbopen.getWritableDatabase();
        dbku.execSQL("create table if not exists kontak(nama TEXT, nohp TEXT);");
        ambildata();
    }
    View.OnClickListener operasi= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tambah) {
                tambah_data();
            } else if (v.getId() == R.id.search) {
                search_data();
            } else if (v.getId() == R.id.refresh) {
                refresh_view();
            } else if (v.getId() == R.id.deleteButton) {
                if (kAdapter.getSelectingDelete()) {
                    confirm_delete();
                } else {
                    select_delete_data();
                }
            } else {
                select_edit_data();
            }
        }
    };

    private void add_item(String nm, String hp)
    {
        ContentValues datanya = new ContentValues();
        datanya.put("nama",nm);
        datanya.put("nohp",hp);
        dbku.insert("kontak",null,datanya);
        Kontak newKontak = new Kontak(nm,hp);
        kAdapter.add(newKontak);
    }

    private void refresh_view()
    {
        kAdapter.clear();
        kAdapter.setSelectingDelete(false);
        kAdapter.setSelectingEdit(false);

        if (delete_snackbar != null && delete_snackbar.isShown()) {
            delete_snackbar.dismiss();
        }

        if (edit_snackbar != null && edit_snackbar.isShown()) {
            edit_snackbar.dismiss();
        }

        ambildata();
    }

    private void search_item(String nm)
    {
        // Clear the existing items in the adapter
        kAdapter.clear();

        // Perform a database query to search for contacts with matching name
        Cursor cursor = dbku.query(
                "kontak", // Table name
                new String[]{"nama", "nohp"}, // Columns to retrieve
                "nama LIKE ?", // Selection criteria
                new String[]{"%" + nm + "%"}, // Selection args (search query)
                null, // Group by
                null, // Having
                null // Order by
        );

        // Iterate through the cursor to retrieve search results
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract contact details from the cursor
                String name = cursor.getString(cursor.getColumnIndex("nama"));
                String phone = cursor.getString(cursor.getColumnIndex("nohp"));

                // Create a new Kontak object and add it to the adapter
                Kontak contact = new Kontak(name, phone);
                kAdapter.add(contact);
            } while (cursor.moveToNext());

            // Close the cursor after use
            cursor.close();
        } else {
            // No matching results found, you can display a message or handle it accordingly
            Toast.makeText(this, "No matching contacts found", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertKontak(String nm, String hp)
    {
        Kontak newKontak = new Kontak(nm,hp);
        kAdapter.add(newKontak);
    }

    private void ambildata()
    {
        Cursor cur;
        cur = dbku.rawQuery("select * from kontak",null);
        Toast.makeText(this,"Terdapat sejumlah " + cur.getCount(),
                Toast.LENGTH_LONG).show();
        int i=0;if(cur.getCount() > 0) cur.moveToFirst();
        while(i<cur.getCount())
        {
            insertKontak(cur.getString(cur.getColumnIndex("nama")),
                    cur.getString(cur.getColumnIndex("nohp")));
            cur.moveToNext();
            i++;
        }
    }

    private void tambah_data(){
        AlertDialog.Builder buat;
        buat = new AlertDialog.Builder(this);
        buat.setTitle("Add Kontak");

        View vAdd = LayoutInflater.from(this).inflate(R.layout.add_kontak,null);
        final EditText nm = (EditText) vAdd.findViewById(R.id.nm);
        final EditText hp = (EditText) vAdd.findViewById(R.id.hp);

        buat.setView(vAdd);
        // Set up the buttons
        buat.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                add_item( nm.getText().toString(),hp.getText().toString());
                Toast.makeText(getBaseContext(),"Data Tersimpan",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        buat.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        buat.show();
    }

    private void search_data() {
        AlertDialog.Builder search;
        search = new AlertDialog.Builder(this);
        search.setTitle("Search Kontak");

        View vSearch = LayoutInflater.from(this).inflate(R.layout.search_kontak,null);
        final EditText nm = (EditText) vSearch.findViewById(R.id.nmSearch);

        search.setView(vSearch);
        // Set up the buttons
        search.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                search_item(nm.getText().toString());
            }
        });
        search.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        search.show();
    }

    private void select_delete_data()
    {
        kAdapter.setSelectingDelete(true);
        delete_snackbar = Snackbar.make(findViewById(android.R.id.content), "Click contacts then press delete button again to confirm or press the refresh button to cancel.", Snackbar.LENGTH_INDEFINITE);
        delete_snackbar.show();
    }

    private void confirm_delete()
    {
        // Get the list of selected contacts from the adapter
        List<Kontak> selectedContacts = kAdapter.getSelectedContacts();

        // Check if any contacts are selected for deletion
        if (selectedContacts.isEmpty()) {
            Toast.makeText(this, "No contacts selected for deletion", Toast.LENGTH_SHORT).show();
            return;
        }

        // Iterate through the selected contacts and delete them from the database and adapter
        for (Kontak contact : selectedContacts) {
            // Delete the contact from the database
            dbku.delete("kontak", "nama = ? AND nohp = ?", new String[]{contact.getNama(), contact.getNoHp()});

            // Remove the contact from the adapter
            kAdapter.remove(contact);
        }

        // Notify the user that contacts have been deleted
        Toast.makeText(this, "Selected contacts deleted", Toast.LENGTH_SHORT).show();

        // Reset the selection mode and dismiss the delete snackbar
        kAdapter.setSelectingDelete(false);
        if (delete_snackbar != null && delete_snackbar.isShown()) {
            delete_snackbar.dismiss();
        }
    }

    private void select_edit_data()
    {
        kAdapter.setSelectingEdit(true);
        edit_snackbar = Snackbar.make(findViewById(android.R.id.content), "Select a contact to edit. Or click refresh to cancel", Snackbar.LENGTH_INDEFINITE);
        edit_snackbar.show();
    }

    public void edit_dialog(Kontak k)
    {
        AlertDialog.Builder edit;
        edit = new AlertDialog.Builder(this);
        edit.setTitle("Edit Kontak");

        View vAdd = LayoutInflater.from(this).inflate(R.layout.add_kontak,null);
        final EditText nm = (EditText) vAdd.findViewById(R.id.nm);
        final EditText hp = (EditText) vAdd.findViewById(R.id.hp);

        edit.setView(vAdd);
        // Set up the buttons
        edit.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nm.getText().length() > 0 && hp.getText().length() > 0) {
                    ContentValues edited = new ContentValues();
                    edited.put("nama",nm.getText().toString());
                    edited.put("nohp",hp.getText().toString());
                    dbku.update("kontak", edited, "nama = ? AND nohp = ?", new String[]{kAdapter.selected_edit.getNama(), kAdapter.selected_edit.getNoHp()});
                    kAdapter.selected_edit = null;
                    kAdapter.setSelectingEdit(false);
                    edit_snackbar.dismiss();
                    refresh_view();
                }
            }
        });
        edit.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        edit.show();
    }
}
