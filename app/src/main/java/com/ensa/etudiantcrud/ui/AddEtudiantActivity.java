package com.ensa.etudiantcrud.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.android.volley.toolbox.Volley;
import com.ensa.etudiantcrud.MainActivity;
import com.ensa.etudiantcrud.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddEtudiantActivity extends AppCompatActivity {
    private EditText nom;
    private EditText prenom;
    private Spinner ville;
    private RadioButton m;
    private RadioButton f;
    private Button add,annuller;
    private ImageView imageView;
    private Button btnSelectImage;
    private Uri selectedImageUri;
    private RequestQueue requestQueue;
    private String insertUrl = "http://10.0.2.2:4000/api/etudiants";
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);
        initializeViews();
        setupButtonListeners();
        setupImagePicker();
        annuller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddEtudiantActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        nom = findViewById(R.id.editTextNom);
        prenom = findViewById(R.id.editTextPrenom);
        ville = findViewById(R.id.spinnerVille);
        add = findViewById(R.id.buttonAjouter);
        m = findViewById(R.id.radioHomme);
        f = findViewById(R.id.radioFemme);
        imageView = findViewById(R.id.imageViewEtudiant);
        btnSelectImage = findViewById(R.id.buttonChoisirImage);
        annuller=findViewById(R.id.buttonAnnuler);

    }

    private void setupButtonListeners() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    sendMultipartRequest();
                    Intent intent=new Intent(AddEtudiantActivity.this, MainActivity.class);
                    startActivity(intent);

                }

            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageView.setImageURI(selectedImageUri);
                            Log.d("IMAGE_PICKER", "Image URI: " + selectedImageUri.toString());
                        } else {
                            showToast("Erreur lors de la sélection de l'image");
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
        if (selectedImageUri == null) {
            showToast("Veuillez sélectionner une image");
            return false;
        }
        return true;
    }

    private void sendMultipartRequest() {
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, insertUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        try {
                            JSONObject result = new JSONObject(resultResponse);
                            Log.d("API_SUCCESS", "Response: " + result.toString());
                            showToast("Étudiant ajouté avec succès");
                            clearInputs();
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
                        String errorMessage = "Erreur lors de l'ajout";
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
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    if (selectedImageUri != null) {
                        byte[] imageData = getFileDataFromUri(selectedImageUri);
                        params.put("image", new DataPart("image.jpg", imageData, "image/jpeg"));
                    } else {
                        Log.e("IMAGE_ERROR", "Selected image URI is null");
                    }
                } catch (IOException e) {
                    Log.e("IMAGE_ERROR", "Error getting image data: " + e.getMessage());
                }
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(multipartRequest);
    }

    private byte[] getFileDataFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        if (inputStream != null) {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            inputStream.close();
        }
        return byteBuffer.toByteArray();
    }

    private void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(AddEtudiantActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    private void clearInputs() {
        nom.setText("");
        prenom.setText("");
        ville.setSelection(0);
        m.setChecked(false);
        f.setChecked(false);
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        selectedImageUri = null;
    }
}
