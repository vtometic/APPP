package com.example.parkingappv01.ui.ponude;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingappv01.MainActivity;
import com.example.parkingappv01.PonudaItem;
import com.example.parkingappv01.R;
import com.example.parkingappv01.ui.ponudi1.Ponudi1Fragment;
import com.example.parkingappv01.ui.rezervacije.RezervacijeFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class PonudeFragment extends Fragment {
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter adapter;
    FloatingActionButton mfBtnDodaj;
    private Query query;
    String id;
    private FirestoreRecyclerOptions options;
    public PonudeFragment()  {

    }

    public static Fragment newInstance() {
        return new PonudeFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).unlockDrawer();
        View v = inflater.inflate(R.layout.ponude_fragment, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.ponude_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        id = fAuth.getCurrentUser().getUid();
        query = fStore.collection("parkirna_mjesta").whereEqualTo("kreator_id", id);


        mfBtnDodaj = v.findViewById(R.id.fBtnDodaj);
        mfBtnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, Ponudi1Fragment.newInstance());
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
                new FirestoreRecyclerOptions.Builder<PonudaItem>()
                        .setQuery(query, PonudaItem.class)
                        .build();

        FirestoreRecyclerAdapter<PonudaItem, PonudaViewHolder> adapter = new FirestoreRecyclerAdapter<PonudaItem, PonudaViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PonudaViewHolder holder, int position, @NonNull PonudaItem model) {
                holder.mNaziv.setText(model.getNaziv());
                holder.mDatOd.setText("Datum od: "+model.getDatumod());
                holder.mDatDo.setText("Datum do: "+model.getDatumdo());
                holder.mVriOd.setText("Vrijeme od: "+model.getVrijemeod());
                holder.mVriDo.setText("Vrijeme od: "+model.getVrijemedo());
                holder.mCijena.setText("Cijena: "+model.getCijena()+"kn");
                holder.mParkingId = model.getParking_id();
            }

            @NonNull
            @Override
            public PonudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ponuda_item, parent, false);
                PonudaViewHolder viewHolder = new PonudaViewHolder(view, fStore,getLayoutInflater());
                return viewHolder;
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class PonudaViewHolder extends RecyclerView.ViewHolder {

        TextView mNaziv, mDatOd, mDatDo, mVriOd, mVriDo, mCijena;
        String mParkingId;
        public ImageButton btnDelete;

        public PonudaViewHolder(@NonNull final View itemView, final FirebaseFirestore firestore, final LayoutInflater inflater) {
            super(itemView);
            mNaziv = itemView.findViewById(R.id.pnaziv);
            mDatOd = itemView.findViewById(R.id.pdatumod);
            mDatDo = itemView.findViewById(R.id.pdatumdo);
            mVriOd = itemView.findViewById(R.id.pvrijemeod);
            mVriDo = itemView.findViewById(R.id.pvrijemedo);
            mCijena = itemView.findViewById(R.id.pcijena);
            btnDelete =itemView.findViewById(R.id.pdelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final String id = mParkingId;
                    final AlertDialog.Builder mBulider = new AlertDialog.Builder(itemView.getContext());
                    final View mView = inflater.inflate(R.layout.potvrdi_dialog, null);
                    mBulider.setView(mView);
                    final AlertDialog dialog = mBulider.create();
                    dialog.show();
                    Button mNe = (Button) mView.findViewById(R.id.btnNe);
                    mNe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button mDa = (Button) mView.findViewById(R.id.btnDa);
                    mDa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            firestore.collection("parkirna_mjesta").document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(itemView.getContext(),"Ponuda obrisana ", Toast.LENGTH_SHORT).show();
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