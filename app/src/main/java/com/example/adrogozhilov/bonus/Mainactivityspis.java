package com.example.adrogozhilov.bonus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by a.drogozhilov on 13.12.2016.
 */
public class Mainactivityspis extends AppCompatActivity {

    public String responseString = "";
    ArrayList names = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        names.clear();
        Log.d("LOG_P", "activity 2");
        names = (ArrayList) getIntent().getSerializableExtra("anames");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // находим список
        ListView lvMain = (ListView) findViewById(R.id.listView);

        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.priz_layoyt, names);

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        // Очистите все ресурсы. Это касается завершения работы
        // потоков, закрытия соединений с базой данных и т. д.
        names.clear();
        super.onDestroy();
    }

}
