package com.comp2601.mazesolver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import java.lang.reflect.Parameter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static Spinner gameOptions;
    private static Button btGo;
    private static String url = "https://www.mazes.ws/mazes-medium-puzzle-one.htm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Spinner spinnerGames = findViewById(R.id.spinner_games);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.games,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGames.setAdapter(adapter);
        spinnerGames.setOnItemSelectedListener(this);
        buttonListener();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        String selectedGame = parent.toString();
        Toast.makeText(this,getResources().getString(R.string.dropdown),Toast.LENGTH_SHORT).show();
    }

    public void buttonListener() {

        gameOptions = (Spinner) findViewById(R.id.spinner_games);
        btGo = (Button) findViewById(R.id.button_go);

        btGo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(gameOptions.getSelectedItem() == getResources().getString(R.string.mazeSolver))
                {
                    Intent launchActivity1= new Intent(MainActivity.this,MainMaze.class);
                    startActivity(launchActivity1);
                }
                else if(gameOptions.getSelectedItem() == getResources().getString(R.string.onlineGames))
                {
                    if(url.length() != 0){
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(webIntent);
                    }

                }
            }

        });
    }


}