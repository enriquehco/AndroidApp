package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /**Created when the user presses the send button*/

    public void startMap(View view) {
        Intent intent = new Intent(this, DisplayRutasPosibles.class);
        startActivity(intent);
    }

    public void startQR(View view) {
        Intent intent = new Intent(this, DisplayQRActivity.class);
        startActivity(intent);
    }
}