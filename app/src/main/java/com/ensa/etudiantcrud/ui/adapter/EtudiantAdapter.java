package com.ensa.etudiantcrud.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ensa.etudiantcrud.R;
import com.ensa.etudiantcrud.ui.model.Etudiant;

import java.util.ArrayList;
import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> implements Filterable {
    private static final String TAG = "EtudiantAdapter";
    private List<Etudiant> etudiants; // Original list of students
    private List<Etudiant> etudiantsFiltered; // Filtered list of students
    private NewFilter mFilter;
    private Context context;

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
        this.etudiantsFiltered = new ArrayList<>();
        etudiantsFiltered.addAll(etudiants);
        this.mFilter = new NewFilter(this);
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.etudiant_item, parent, false);
        return new EtudiantViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Log.d(TAG, "onBindView call !" + position);

        Etudiant etudiant = etudiantsFiltered.get(position);

        String imageUrl = "http://10.0.2.2:4000/api/images/" + etudiant.getImageUrl();

        Glide.with(holder.imageView.getContext())
                .load(etudiant.getImageUrl() != null ? imageUrl : R.drawable.user)
                .apply(new RequestOptions().override(100, 100))
                .into(holder.imageView);

        holder.name.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        holder.ville.setText("Ville : " + etudiant.getVille());
        Log.d(TAG, "id de l'etudiant: " + etudiant.getId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 300) {
                    if (onEtudiantClickListener != null) {
                        onEtudiantClickListener.onDoubleClick(etudiant);
                    }
                }
                lastClickTime = currentTime;
            }
        });
    }


    @Override
    public int getItemCount() {
        return etudiantsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView name,id;
        ImageView imageView;
        TextView ville;
        CardView parent;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nom);
            imageView = itemView.findViewById(R.id.img);
            ville=itemView.findViewById(R.id.ville);
            parent=itemView.findViewById(R.id.parent);

        }
    }

    public class NewFilter extends Filter {
        public  EtudiantAdapter mAdapter;

        public NewFilter(EtudiantAdapter adapter) {
            super();
            this.mAdapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Etudiant> filteredList = new ArrayList<>();
             FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(etudiants); // Show all items
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (Etudiant etudiant : etudiants) {
                    if (etudiant.getNom().toLowerCase().startsWith(filterPattern) ||
                            etudiant.getPrenom().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(etudiant);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            Log.d(TAG, "Filtered count: hellllllllllllo " + results.count);
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            etudiantsFiltered.clear();
            etudiantsFiltered.addAll((List<Etudiant>) results.values);
            notifyDataSetChanged();
        }
    }


    public void updateEtudiants(List<Etudiant> newEtudiants) {
        etudiants.clear();
        etudiants.addAll(newEtudiants);
        etudiantsFiltered.clear();
        etudiantsFiltered.addAll(newEtudiants);
        notifyDataSetChanged();
    }
    public void removeEtudiant(int position) {
        etudiants.remove(position);
        notifyItemRemoved(position);
    }

    public Etudiant getEtudiantAtPosition(int position) {
        return etudiants.get(position);
    }
    private OnEtudiantClickListener onEtudiantClickListener;

    public interface OnEtudiantClickListener {
        void onDoubleClick(Etudiant etudiant);
    }

    public void setOnEtudiantClickListener(OnEtudiantClickListener listener) {
        this.onEtudiantClickListener = listener;
    }

}
