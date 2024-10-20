
package com.ensa.etudiantcrud.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ensa.etudiantcrud.R;

public class EtudiantDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiant_detial);

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView surnameTextView = findViewById(R.id.surnameTextView);
        TextView villeTextView = findViewById(R.id.villeTextView);
        TextView sexeTextView = findViewById(R.id.sexeTextView);
        ImageView imageView = findViewById(R.id.imageView);

        // Get data from intent
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String ville = getIntent().getStringExtra("ville");
        String sexe = getIntent().getStringExtra("sexe");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String imageUrll = "http://10.0.2.2:4000/api/images/" + imageUrl;

        // Set data to views
        nameTextView.setText(name);
        surnameTextView.setText(surname);
        villeTextView.setText(ville);
        sexeTextView.setText(sexe);

        // Load the image using Glide or another image loading library
        if (imageUrl != null && !imageUrl.isEmpty()) {

            Glide.with(this).load(imageUrll).into(imageView);
        }
    }
}
