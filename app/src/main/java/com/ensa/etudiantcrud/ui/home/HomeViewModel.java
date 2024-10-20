package com.ensa.etudiantcrud.ui.home;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ensa.etudiantcrud.ui.model.Etudiant;
import com.ensa.etudiantcrud.ui.network.ApiService;
import com.ensa.etudiantcrud.ui.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Etudiant> dernierEtudiant = new MutableLiveData<>();
    private final ApiService apiService;
    private Handler handler;
    private Runnable fetchRunnable;

    public HomeViewModel() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        handler = new Handler();
        startFetchingDernierEtudiant(); // Démarre le fetch toutes les 5 secondes
    }

    // Fonction qui appelle l'API pour récupérer tous les étudiants
    private void getAllEtudiants() {
        apiService.getAllEtudiants().enqueue(new Callback<List<Etudiant>>() {
            @Override
            public void onResponse(Call<List<Etudiant>> call, Response<List<Etudiant>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Récupérer le dernier étudiant de la liste
                    List<Etudiant> etudiants = response.body();
                    dernierEtudiant.setValue(etudiants.get(etudiants.size() - 1));
                    Log.d("Dernier Etudiant", dernierEtudiant.getValue().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Etudiant>> call, Throwable t) {
                Log.e("HomeViewModel", "Erreur lors de la récupération des étudiants", t);
            }
        });
    }

    // Démarrer le fetch toutes les 5 secondes
    public void startFetchingDernierEtudiant() {
        fetchRunnable = new Runnable() {
            @Override
            public void run() {
                getAllEtudiants();
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(fetchRunnable);
    }

    // Arrêter le fetch
    public void stopFetchingDernierEtudiant() {
        if (fetchRunnable != null) {
            handler.removeCallbacks(fetchRunnable);
        }
    }

    // Nettoyage lorsque ViewModel est détruit
    @Override
    protected void onCleared() {
        super.onCleared();
        stopFetchingDernierEtudiant(); // Arrêter le fetch quand ViewModel est détruit
    }

    // LiveData pour observer le dernier étudiant
    public LiveData<Etudiant> getDernierEtudiant() {
        return dernierEtudiant;
    }
}
