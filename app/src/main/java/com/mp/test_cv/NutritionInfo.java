package com.mp.test_cv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NutritionInfo extends AppCompatActivity {
    final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutritioninfo);
        Button btnScan = (Button) findViewById(R.id.scan);

        btnScan.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraView.class);
                startActivity(intent);
            }
        });
    }
}
