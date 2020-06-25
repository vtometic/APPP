package com.example.parkingappv01.ui.pronadi2;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.pronadi1.Pronadi1Fragment;
import com.example.parkingappv01.ui.pronadi1.Pronadi1ViewModel;
import com.example.parkingappv01.ui.rezerviran.RezerviranFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pronadi2Fragment extends Fragment {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    TextView infNaziv, infDatum, infVod, infVdo, infCps, inpSati, inpUkupno;
    TextInputLayout inpOd, inpDo, inpRego;

    String rez;

    private Pronadi2ViewModel mViewModel;

    public static Pronadi2Fragment newInstance() {
        return new Pronadi2Fragment();
}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).lockDrawer();
        View v = inflater.inflate(R.layout.pronadi2_fragment, container, false);

        fAuth= FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        final Bundle bundle = getArguments();
        String naziv = bundle.getString("Naziv");
        final String vriod = bundle.getString("Vriod");
        final String vrido = bundle.getString("Vrido");
        final String cps = bundle.getString("Cps");
        String pid = bundle.getString("Id");

        infNaziv = v.findViewById(R.id.infnaziv);
        infNaziv.setText("Adresa:"+naziv);
        infDatum = v.findViewById(R.id.infdatum);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String danas = sdf.format(new Date());
        infDatum.setText("Datum: "+danas);
        infVod = v.findViewById(R.id.infvrijemeod);
        infVod.setText("Raspoloživo od: "+vriod);
        infVdo = v.findViewById(R.id.infvrijemedo);
        infVdo.setText("Raspoloživo do: "+vrido);
        infCps = v.findViewById(R.id.infcijena);
        infCps.setText("Cijena po satu: "+cps);

        inpSati = v.findViewById(R.id.inputSati);
        inpSati.setText("Ukupno sati:");

        inpOd = v.findViewById(R.id.inputOd);
        inpOd.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeOd();

            }
        });

        inpDo = v.findViewById(R.id.inputDo);
        inpDo.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDo();

            }

        });

        inpOd.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(inpOd.getEditText().getText().length() >= 0) {
                    final String vriod = inpOd.getEditText().getText().toString().trim();
                    final String vrido = inpDo.getEditText().getText().toString().trim();
                    if(!vriod.isEmpty()){
                        if(!vrido.isEmpty()){
                        uku(cps,raz());
                        inpSati.setText("Ukupno sati"+raz());
                        }
                    }
                }
            }
        });
        inpDo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(inpDo.getEditText().getText().length() >= 0) {
                    final String vriod = inpOd.getEditText().getText().toString().trim();
                    final String vrido = inpDo.getEditText().getText().toString().trim();
                    if(!vriod.isEmpty()){
                        if(!vrido.isEmpty()){
                            uku(cps,raz());
                            inpSati.setText("Ukupno sati: "+raz());
                        }
                    }
                }
            }
        });

        inpRego = v.findViewById(R.id.inputRego);
        inpUkupno = v.findViewById(R.id.inputUkupno);

        ImageButton imgBtnNazad3 = (ImageButton) v.findViewById(R.id.imgBtnNaz);
        imgBtnNazad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, Pronadi1Fragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        Button btnRezerviraj = (Button) v.findViewById(R.id.btn_rezerviraj);
        btnRezerviraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kreirajRezervaciju(v, vriod, vrido, bundle);

            }
        });

        return v;
    }
    public void timeOd (){

        Calendar calendar = Calendar.getInstance();
        int H = calendar.get(Calendar.HOUR);
        int M = calendar.get(Calendar.MINUTE);


        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int h, int m) {
                inpOd.getEditText().setText(String.format("%02d:%02d",h,m));
            }
        }, H, M,true);
        timePickerDialog.show();
    }
    public void timeDo (){
        Calendar calendar = Calendar.getInstance();

        int H = calendar.get(Calendar.HOUR_OF_DAY);
        int M = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {

                inpDo.getEditText().setText(String.format("%02d:%02d",h,m));
            }
        }, H, M, true);
        timePickerDialog.show();
    }
    public String raz() {
        String t1 = inpOd.getEditText().getText().toString();
        String t2 = inpDo.getEditText().getText().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(t1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            endDate = simpleDateFormat.parse(t2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = endDate.getTime() - startDate.getTime();
        if(difference<0)
        {
            Date dateMax = null;
            try {
                dateMax = simpleDateFormat.parse("24:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date dateMin = null;
            try {
                dateMin = simpleDateFormat.parse("00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            difference=(dateMax.getTime() -startDate.getTime() )+(endDate.getTime()-dateMin.getTime());
        }
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        double hr = (double) hours;
        double mi = (double) min;
        double hmi = hr+(mi/60);
        Double rounded = new BigDecimal(hmi).setScale(2, RoundingMode.HALF_UP).doubleValue();
        String h = String.valueOf(rounded);

        inpSati.setText(h);
        String ukupnosati =inpSati.getText().toString();
        return ukupnosati;
    }
    public void uku(String cps, String sati){
        double c = Double.parseDouble(cps);
        double s = Double.parseDouble(sati);
        double r = c*s;
        Double rounded = new BigDecimal(r).setScale(2, RoundingMode.HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("#.00");

        rez = String.valueOf(df.format(rounded));

        inpUkupno.setText("Ukupan iznos: "+rez);

    }
    private boolean validateOdvri(String v, String v2){
        String t3 = v2;
        String t2 = v;
        String t1 = inpOd.getEditText().getText().toString().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        if(t1.isEmpty()){
            inpOd.setError("Unesite vrijeme;");
            return false;
        }else{
            Date rasOd, rasDo, picked;
            try {
                rasOd = sdf.parse(t2);
                rasDo = sdf.parse(t3);
                picked = sdf.parse(t1);

                if (picked.before(rasOd) | picked.after(rasDo)) {
                    inpOd.setError("Vrijeme mora biti u dozvoljenom rasponu");
                    return false;
                } else {
                    inpOd.setError(null);
                    return true;
                }
            }catch (ParseException e){
                e.printStackTrace();
            }
            return false;
        }
    }
    private boolean validateDovri(String v, String v2){
        String t1 = inpDo.getEditText().getText().toString().trim();
        String t2 = v; //od
        String t3 = v2; //do
        boolean pass = false;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if(t1.isEmpty()){
            inpDo.setError("Unesite vrijeme;");
            return false;
        }
            Date rasOd, rasDo, picked;
            try {
                rasOd = sdf.parse(t2);
                rasDo = sdf.parse(t3);
                picked = sdf.parse(t1);

                if (picked.before(rasOd)|picked.after(rasDo)) {
                    inpDo.setError("Vrijeme mora biti u dozvoljenom rasponu");
                    pass = false;
                } else {
                    inpDo.setError(null);
                    pass = true;
                }
            }catch (ParseException e){
                e.printStackTrace();
            }

            if(pass) return true;
            else return false;
    }
    private boolean validateReg(){
        String reg = inpRego.getEditText().getText().toString().trim();
        if(reg.isEmpty()){
            inpRego.setError("Unesite registarsku oznaku vozila");
            return false;
        }else{
            inpRego.setError(null);
            return true;
        }
    }
    public void kreirajRezervaciju (View v, String Vod, String Vdo, Bundle bundle){
        if(!validateOdvri(Vod, Vdo) | !validateDovri(Vod, Vdo) | !validateReg()){
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String danas = sdf.format(new Date());

        final Map<String,String> rezervacija = new HashMap<>();
        rezervacija.put("naziv", infNaziv.getText().toString());
        rezervacija.put("kreator_id", fAuth.getUid());
        rezervacija.put("datum", danas);
        rezervacija.put("parking_id", bundle.getString("Id"));
        rezervacija.put("vrijemeod", inpOd.getEditText().getText().toString());
        rezervacija.put("vrijemedo", inpDo.getEditText().getText().toString());
        rezervacija.put("iznos", rez );
        rezervacija.put("regos", inpRego.getEditText().getText().toString());
        rezervacija.put("placeno","Ne");
        rezervacija.put("isRated", "Ne");

        fStore.collection("rezervacije").add(rezervacija).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                DocumentReference np = fStore.collection("rezervacije").document(documentReference.getId());
                np.update("rezervacija_id", documentReference.getId());

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, RezerviranFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(getActivity(), "Greška: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Pronadi2ViewModel.class);
        // TODO: Use the ViewModel

    }
}