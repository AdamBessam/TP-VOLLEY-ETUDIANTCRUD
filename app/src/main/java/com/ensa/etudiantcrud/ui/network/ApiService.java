package com.ensa.etudiantcrud.ui.network;

import com.ensa.etudiantcrud.ui.model.Etudiant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/api/etudiants/all")
    Call<List<Etudiant>> getAllEtudiants();
    @DELETE("/api/etudiants/{id}")
    Call<Void> deleteEtudiant(@Path("id") int id);
    @PUT("/api/etudiants/{id}")
    Call<Etudiant> updateEtudiant(@Path("id") int id, @Body Etudiant etudiant);
}
