package com.ensa.etudiantcrud.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ensa.etudiantcrud.R;
import com.ensa.etudiantcrud.databinding.FragmentHomeBinding;
import com.ensa.etudiantcrud.ui.model.Etudiant;
import com.bumptech.glide.Glide;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Observer les changements du dernier étudiant
        homeViewModel.getDernierEtudiant().observe(getViewLifecycleOwner(), this::updateUI);

        return root;
    }

    private void updateUI(Etudiant etudiant) {
        if (etudiant != null) {
            binding.textViewLastAdded.setText("Dernier étudiant ajouté : ");
            // Afficher les informations de l'étudiant
            binding.textViewName.setText("Nom : " + etudiant.getNom());
            binding.textViewPrenom.setText("Prénom : " + etudiant.getPrenom());
            binding.textViewCity.setText("Ville : " + etudiant.getVille());
            binding.textViewSex.setText("Sexe : " + etudiant.getSexe());


            String imageUrl = etudiant.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Log.d("Image URL", imageUrl);

                // Utilisation de Glide pour charger l'image
                String imageUrll = "http://10.0.2.2:4000/api/images/" + etudiant.getImageUrl();
                Glide.with(this)
                        .load(imageUrll)
                        .placeholder(R.drawable.user)  // Image par défaut pendant le chargement
                        .error(R.drawable.user)
                        .into(binding.imageViewStudent);
            } else {
                binding.imageViewStudent.setImageResource(R.drawable.user);
            }
        } else {
            // Gérer le cas où l'étudiant est null
            binding.textViewName.setText("Aucun étudiant trouvé");
            binding.textViewPrenom.setText("");
            binding.textViewCity.setText("");
            binding.textViewSex.setText("");
            binding.imageViewStudent.setImageResource(R.drawable.user);  // Image par défaut
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
