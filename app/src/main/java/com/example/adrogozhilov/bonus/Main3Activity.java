package com.example.adrogozhilov.bonus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {
    ArrayList names = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        names = (ArrayList) getIntent().getSerializableExtra("anames");
        Log.d("LOG_P", "activity 3");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // находим список
        ListView lvMain = (ListView) findViewById(R.id.listactiv2);
        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.priz_layoyt, names);
        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);


    }

}
