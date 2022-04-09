package com.moch.mochyasinabdillah_uas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }
    public void gitClicked(View view){
        Intent i = new Intent(Menu.this, MainActivity.class);
        startActivity(i);
    }

    public void camClicked(View view){
        Intent i = new Intent(Menu.this, start.class);
        startActivity(i);
    }
}

