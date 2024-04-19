package com.example.listview;

public class Kontak {
    private String nama;
    private String noHp;
    private boolean selected;


    public Kontak(String nama, String noHp) {
        this.nama = nama;
        this.noHp = noHp;
        this.selected = false;
    }

    public String getNoHp() {  return noHp;}

    public void setNoHp(String noHp) {this.noHp = noHp;}

    public String getNama() {return nama;}

    public void setNama(String nama) {this.nama = nama;}

    // Getters and setters for selected field
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
