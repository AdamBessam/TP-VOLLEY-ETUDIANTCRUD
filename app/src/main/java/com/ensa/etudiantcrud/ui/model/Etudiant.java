package com.ensa.etudiantcrud.ui.model;

public class Etudiant {
    private int id; // ou autre identifiant
    private String nom;
    private String prenom;
    private String ville;
    private String sexe;
    private String imageUrl;

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "Etudiant{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", ville='" + ville + '\'' +
                ", sexe='" + sexe + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
