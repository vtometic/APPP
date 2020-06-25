package com.example.parkingappv01;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class PonudaItem {
    String naziv;
    String datumod;
    String datumdo;
    String vrijemeod;
    String vrijemedo;
    String cijena;
    String parking_id;

    private  PonudaItem(){}

    private PonudaItem(String naziv, String datumod, String datumdo, String vrijemeod, String vrijemedo, String cijena, String parking_id){
        this.naziv = naziv;
        this.datumod = datumod;
        this.datumdo = datumdo;
        this.vrijemeod = vrijemeod;
        this.vrijemedo = vrijemedo;
        this.cijena = cijena;
        this.parking_id = parking_id;

    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDatumod() {
        return datumod;
    }

    public void setDatumod(String datumod) {
        this.datumod = datumod;
    }

    public String getDatumdo() {
        return datumdo;
    }

    public void setDatumdo(String datumdo) {
        this.datumdo = datumdo;
    }

    public String getVrijemeod() {
        return vrijemeod;
    }

    public void setVrijemeod(String vrijemeod) {
        this.vrijemeod = vrijemeod;
    }

    public String getVrijemedo() {
        return vrijemedo;
    }

    public void setVrijemedo(String vrijemedo) {
        this.vrijemedo = vrijemedo;
    }

    public String getCijena() {
        return cijena;
    }

    public void setCijena(String cijena) {
        this.cijena = cijena;
    }
    public String getParking_id() {
        return parking_id;
    }

    public void setParking_id(String parking_id) {
        this.parking_id = parking_id;
    }
}
