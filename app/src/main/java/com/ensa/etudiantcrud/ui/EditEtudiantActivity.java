package com.ensa.etudiantcrud.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ensa.etudiantcrud.MainActivity;
import com.ensa.etudiantcrud.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditEtudiantActivity extends AppCompatActivity {
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button update, annuller;
    private ImageView imageView;
    private Button btnSelectImage;
    private Uri selectedImageUri;
    private RequestQueue requestQueue;
    private String updateUrl = "http://10.0.2.2:4000/api/etudiants/";
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int etudiantId;
    private String existingImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_etudiant);
        initializeViews();
        setupButtonListeners();
        setupImagePicker();
        loadEtudiantData();

        annuller.setOnClickListener(v -> {
            Intent intent = new Intent(EditEtudiantActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        nom = findViewById(R.id.editTextNom);
        prenom = findViewById(R.id.editTextPrenom);
        ville = findViewById(R.id.spinnerVille);
        update = findViewById(R.id.buttonMdf);
        m = findViewById(R.id.radioHomme);
        f = findViewById(R.id.radioFemme);
        imageView = findViewById(R.id.imageViewEtudiant);
        btnSelectImage = findViewById(R.id.buttonChoisirImage);
        annuller = findViewById(R.id.buttonAnnuler);
    }

    private void setupButtonListeners() {
        update.setOnClickListener(view -> {
            if (validateInputs()) {
                sendMultipartUpdateRequest();
                Intent intent = new Intent(EditEtudiantActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSelectImage.setOnClickListener(v -> openImagePicker());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                        // Check if the selected image URI is not null
                        if (selectedImageUri != null) {
                            // Load the selected image into the ImageView using Glide
                            Glide.with(this)
                                    .load(selectedImageUri)  // Load the image from the URI
                                    .into(imageView);

                            Log.d("IMAGE_PICKER", "Image loaded from URI: " + selectedImageUri.toString());
                        } else {
                            showToast("Erreur: L'image sélectionnée est vide.");
                        }
                    } else {
                        Log.e("IMAGE_PICKER", "Erreur lors de la sélection de l'image, code de résultat: " + result.getResultCode());
                    }
                }
        );
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private boolean validateInputs() {
        if (nom.getText().toString().trim().isEmpty()) {
            showToast("Veuillez entrer un nom");
            return false;
        }
        if (prenom.getText().toString().trim().isEmpty()) {
            showToast("Veuillez entrer un prénom");
            return false;
        }
        if (ville.getSelectedItemPosition() == 0) {
            showToast("Veuillez sélectionner une ville");
            return false;
        }
        if (!m.isChecked() && !f.isChecked()) {
            showToast("Veuillez sélectionner un sexe");
            return false;
        }

        return true;
    }

    private void loadEtudiantData() {
        Intent intent = getIntent();
        etudiantId = intent.getIntExtra("etudiant_id", -1);
        existingImageUrl = intent.getStringExtra("imageUrl"); // Get the image URL from the intent

        // Fetch existing student data from the server
        String fetchUrl = updateUrl + etudiantId;
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fetchUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Populate the fields with existing data
                            nom.setText(response.getString("nom"));
                            prenom.setText(response.getString("prenom"));
                            // Set the appropriate city in the spinner
                            ville.setSelection(getCityPosition(response.getString("ville")));
                            String sexe = response.getString("sexe");
                            if ("homme".equalsIgnoreCase(sexe)) {
                                m.setChecked(true);
                            } else {
                                f.setChecked(true);
                            }

                            // Load the existing image if available
                            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                                // Use Glide to load the existing image
                                Glide.with(EditEtudiantActivity.this)
                                        .load("http://10.0.2.2:4000/api/images/" + existingImageUrl) // Construct the URL
                                        .into(imageView);
                            } else {
                                Log.e("IMAGE_LOAD", "Image URL is null or empty");
                            }
                        } catch (JSONException e) {
                            showToast("Erreur lors du chargement des données: " + e.getMessage());
                            Log.e("LOAD_DATA_ERROR", "JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                        showToast("Erreur lors du chargement des données");
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private int getCityPosition(String city) {
        // Get the adapter from the spinner
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) ville.getAdapter();

        // Loop through the items in the adapter
        for (int i = 0; i < adapter.getCount(); i++) {
            // Check if the current item matches the city
            if (adapter.getItem(i).equalsIgnoreCase(city)) {
                return i; // Return the index if found
            }
        }

        // Return 0 if the city is not found
        return 0; // Optionally, return -1 or another indicator if you want to specify not found
    }

    private void sendMultipartUpdateRequest() {
        if (!validateInputs()) {
            return;  // Early exit if validation fails
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PUT, updateUrl + etudiantId,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        try {
                            JSONObject result = new JSONObject(resultResponse);
                            Log.d("API_SUCCESS", "Response: " + result.toString());
                            showToast("Étudiant modifié avec succès");
                            loadEtudiantData(); // Reload to show updated data
                        } catch (JSONException e) {
                            Log.e("API_ERROR", "JSON parsing error: " + e.getMessage());
                            showToast("Erreur lors du traitement de la réponse");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API_ERROR", "Error: " + error.toString());
                        String errorMessage = "Erreur lors de la modification";
                        if (error.networkResponse != null) {
                            errorMessage += " (Code: " + error.networkResponse.statusCode + ")";
                        }
                        showToast(errorMessage);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nom", nom.getText().toString().trim());
                params.put("prenom", prenom.getText().toString().trim());
                params.put("ville", ville.getSelectedItem().toString());
                params.put("sexe", m.isChecked() ? "homme" : "femme");

                // If no new image is selected, retain the existing image URL
                if (selectedImageUri == null) {
                    params.put("imageUrl", existingImageUrl); // Keep the existing image URL
                } else {
                    params.put("imageUrl", null); // Set to null if a new image is being uploaded
                }

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (selectedImageUri != null) {
                    params.put("image", new DataPart("image.jpg", getFileDataFromDrawable(selectedImageUri), "image/jpeg"));
                }
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(multipartRequest);
    }

    private byte[] getFileDataFromDrawable(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            Log.e("FILE_ERROR", "Error reading file: " + e.getMessage());
            return null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(EditEtudiantActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
