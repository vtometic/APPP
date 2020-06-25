package com.example.parkingappv01;

import android.widget.Button;

public class RezervacijaItem {
    String naziv;
    String datum;
    String iznos;
    String vrijemeod;
    String vrijemedo;
    String regos;
    String rezervacija_id;
    String parking_id;

    private RezervacijaItem() {
    }

    private RezervacijaItem(String naziv, String datum, String iznos, String vrijemeod, String vrijemedo, String regos, String rezervacija_id, String parking_id) {
        this.naziv = naziv;
        this.datum = datum;
        this.iznos = iznos;
        this.vrijemeod = vrijemeod;
        this.vrijemedo = vrijemedo;
        this.regos = regos;
        this.rezervacija_id = rezervacija_id;
        this.parking_id = parking_id;

    }
    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getIznos() {
        return iznos;
    }

    public void setIznos(String iznos) {
        this.iznos = iznos;
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

    public String getRegos() {
        return regos;
    }

    public void setRegos(String regos) {
        this.regos = regos;
    }

    public String getRezervacija_id() {
        return rezervacija_id;
    }

    public void setRezervacija_id(String rezervacija_id) {
        this.rezervacija_id = rezervacija_id;
    }

    public String getParking_id() {
        return parking_id;
    }

    public void setParking_id(String parking_id) {
        this.parking_id = parking_id;
    }
}
