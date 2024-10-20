package com.ensa.etudiantcrud.ui.gallery;

import android.os.Handler;

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

public class GalleryViewModel extends ViewModel {
    private MutableLiveData<List<Etudiant>> etudiants;
    private ApiService apiService;
    private Handler handler;
    private Runnable runnable;

    // Constructor
    public GalleryViewModel() {
        etudiants = new MutableLiveData<>();
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        handler = new Handler();
    }

    public LiveData<List<Etudiant>> getEtudiants() {
        return etudiants;
    }

    public void fetchEtudiants() {
        apiService.getAllEtudiants().enqueue(new Callback<List<Etudiant>>() {
            @Override
            public void onResponse(Call<List<Etudiant>> call, Response<List<Etudiant>> response) {
                if (response.isSuccessful()) {
                    etudiants.setValue(response.body());
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<Etudiant>> call, Throwable t) {
            }
        });
    }

    public void startFetchingEtudiants() {
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchEtudiants();
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runnable);
    }

    // Stop fetching students
    public void stopFetchingEtudiants() {
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopFetchingEtudiants();
    }
    public void deleteEtudiant(int etudiantId) {
        apiService.deleteEtudiant(etudiantId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchEtudiants();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

}
