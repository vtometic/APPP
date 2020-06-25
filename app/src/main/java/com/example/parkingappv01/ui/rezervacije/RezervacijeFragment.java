package com.example.parkingappv01.ui.rezervacije;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.PonudaItem;
import com.example.parkingappv01.R;
import com.example.parkingappv01.RezervacijaItem;
import com.example.parkingappv01.ui.ponude.PonudeFragment;
import com.example.parkingappv01.ui.ponude.PonudeViewModel;
import com.example.parkingappv01.ui.ponudi1.Ponudi1Fragment;
import com.example.parkingappv01.ui.pronadi1.Pronadi1Fragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.android.volley.VolleyLog.TAG;

public class RezervacijeFragment extends Fragment {
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter adapter;
    FloatingActionButton mfBtnDodaj;
    private Query query;
    String id;
    private FirestoreRecyclerOptions options;
    private RezervacijeViewModel mViewModel;
    public RezervacijeFragment()  {

    }
    public static RezervacijeFragment newInstance() {

        return new RezervacijeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).unlockDrawer();
        View v = inflater.inflate(R.layout.rezervacije_fragment, container, false);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rezervacije_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        id = fAuth.getCurrentUser().getUid();
        query = fStore.collection("rezervacije").whereEqualTo("kreator_id", id);

        mfBtnDodaj = v.findViewById(R.id.fBtnDodaj2);
        mfBtnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, Pronadi1Fragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }


    @Override
    public void onStart() {

        super.onStart();

        options =
                new FirestoreRecyclerOptions.Builder<RezervacijaItem>()
                        .setQuery(query, RezervacijaItem.class)
                        .build();

        FirestoreRecyclerAdapter<RezervacijaItem, RezervacijeFragment.RezervacijaViewHolder> adapter = new FirestoreRecyclerAdapter<RezervacijaItem, RezervacijeFragment.RezervacijaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RezervacijeFragment.RezervacijaViewHolder holder, int position, @NonNull RezervacijaItem model) {
                holder.mNaziv.setText(model.getNaziv());
                holder.mDat.setText("Datum: "+model.getDatum());
                holder.mIznos.setText("Iznos: "+model.getIznos());
                holder.mVriOd.setText("Vrijeme od: "+model.getVrijemeod());
                holder.mVriDo.setText("Vrijeme od: "+model.getVrijemedo());
                holder.mRegos.setText("Registarske oznake: "+model.getRegos());
                holder.mRezervacijaId = model.getRezervacija_id();
                holder.mParkingId = model.getParking_id();
            }

            @NonNull
            @Override
            public RezervacijeFragment.RezervacijaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rezervacija_item, parent, false);
                RezervacijeFragment.RezervacijaViewHolder viewHolder = new RezervacijeFragment.RezervacijaViewHolder(view, fStore,getLayoutInflater(), fAuth);

                return viewHolder;
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RezervacijaViewHolder extends RecyclerView.ViewHolder {

        TextView mNaziv, mDat, mIznos, mVriOd, mVriDo, mRegos;
        String mRezervacijaId, mParkingId;
        public ImageButton btnDelete;
        public Button btnPlati, btnRate;

        public RezervacijaViewHolder(@NonNull final View itemView, final FirebaseFirestore firestore, final LayoutInflater inflater, final FirebaseAuth fauth) {
            super(itemView);
            mNaziv = itemView.findViewById(R.id.rnaziv);
            mDat = itemView.findViewById(R.id.rdatum);
            mIznos = itemView.findViewById(R.id.riznos);
            mVriOd = itemView.findViewById(R.id.rvrijemeod);
            mVriDo = itemView.findViewById(R.id.rvrijemedo);
            mRegos = itemView.findViewById(R.id.rregos);
            btnPlati = itemView.findViewById(R.id.btnPlati);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = itemView.getContext();
                    DocumentReference doc = firestore.collection("rezervacije").document(mRezervacijaId);
                    doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String placeno = document.getString("placeno");
                                    String isRated = document.getString("isRated");
                                    if(placeno.equals("Da")){
                                        btnPlati.setVisibility(GONE);
                                        btnRate.setVisibility(VISIBLE);
                                        if(isRated.equals("Da")){
                                            btnRate.setVisibility(GONE);
                                        }
                                    }
                                    if(placeno.equals("Ne")){
                                        btnPlati.setVisibility(VISIBLE);
                                        btnRate.setVisibility(GONE);

                                    }

                                }
                            }
                        }
                    });
                }

            });
            btnPlati.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final String id = mRezervacijaId;
                    final String Pid = mParkingId;
                    final AlertDialog.Builder mBulider = new AlertDialog.Builder(itemView.getContext());
                    final View mView = inflater.inflate(R.layout.plati_dialog, null);
                    Query query = firestore.collection("parkirna_mjesta").whereEqualTo("parking_id",Pid);
                    mBulider.setView(mView);
                    final AlertDialog dialog = mBulider.create();
                    dialog.show();
                    Button mNe = (Button) mView.findViewById(R.id.btnNe3);
                    mNe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button mDa = (Button) mView.findViewById(R.id.btnDa3);
                    mDa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DocumentReference docRef = firestore.collection("korisnici").document(fauth.getUid());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            String kiznos = document.getString("kredit");
                                            final Double mKiznos = Double.valueOf(kiznos);
                                            DocumentReference docRef1 = firestore.collection("rezervacije").document(mRezervacijaId);
                                            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            String piznos = document.getString("iznos");
                                                            final Double mPiznos = Double.valueOf(piznos);
                                                            Double NP = (mKiznos-mPiznos);
                                                            if(mPiznos < mKiznos){
                                                                String NPS = String.valueOf(NP);
                                                                firestore.collection("parkirna_mjesta").document(mParkingId)
                                                                        .update("status","zauzeto");
                                                                firestore.collection("rezervacije").document(mRezervacijaId)
                                                                        .update("placeno","Da");
                                                                firestore.collection("korisnici").document(fauth.getUid())
                                                                        .update("kredit",NPS);
                                                                Toast.makeText(itemView.getContext(),"Placeno", Toast.LENGTH_SHORT).show();
                                                                btnPlati.setVisibility(GONE);
                                                                btnRate.setVisibility(VISIBLE);
                                                            }else{
                                                                Toast.makeText(itemView.getContext(),"Nedovoljan iznos na racunu", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                            dialog.dismiss();

                        }
                    });

                }
            });
            btnRate =itemView.findViewById(R.id.btnRate);
            btnRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final String id = mRezervacijaId;
                    final AlertDialog.Builder mBulider = new AlertDialog.Builder(itemView.getContext());
                    final View mView = inflater.inflate(R.layout.recenzija_dialog, null);
                    mBulider.setView(mView);
                    final AlertDialog dialog = mBulider.create();
                    dialog.show();
                    Button mOdustani = (Button) mView.findViewById(R.id.btnOdustani1);
                    mOdustani.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button mPotvrdi = (Button) mView.findViewById(R.id.btnPotvrdi1);
                    mPotvrdi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RatingBar rating = mView.findViewById(R.id.ratingBar);
                            rating.setNumStars(5);
                            final float ratedValue = rating.getRating();
                            final DocumentReference docRef = firestore.collection("parkirna_mjesta").document(mParkingId);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //
                                            DecimalFormat df = new DecimalFormat("0.00");
                                            double cnt = Double.valueOf(document.getString("nor"));
                                            double ukupno = Double.valueOf(document.getString("rating"));
                                            cnt = (cnt+1);
                                            docRef.update("nor", String.valueOf(cnt));
                                            double rat = (ukupno+ratedValue);
                                            docRef.update("rating", String.valueOf(rat));
                                            btnRate.setVisibility(GONE);
                                            final DocumentReference docRef = firestore.collection("rezervacije").document(mRezervacijaId);
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            docRef.update("isRated","Da");
                                                        }
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                }
            });
            btnDelete =itemView.findViewById(R.id.rdelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final String id = mRezervacijaId;
                    final AlertDialog.Builder mBulider = new AlertDialog.Builder(itemView.getContext());
                    final View mView = inflater.inflate(R.layout.potvrdi_dialog2, null);
                    mBulider.setView(mView);
                    final AlertDialog dialog = mBulider.create();
                    dialog.show();
                    Button mNe = (Button) mView.findViewById(R.id.btnNe2);
                    mNe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button mDa = (Button) mView.findViewById(R.id.btnDa2);
                    mDa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            firestore.collection("rezervacije").document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(itemView.getContext(),"Rezervacija obrisana ", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(itemView.getContext(),"Greška ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            dialog.dismiss();
                        }
                    });
                }
            });

        }

    }

}
