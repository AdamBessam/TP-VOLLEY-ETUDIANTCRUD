package com.ensa.etudiantcrud;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class FirstPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        ImageView userImageView = findViewById(R.id.userImageView);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        userImageView.startAnimation(zoomIn);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FirstPage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}
