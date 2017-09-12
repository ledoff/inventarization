package com.example.adrogozhilov.bonus;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrogozhilov.bonus.zxing.IntentIntegrator;
import com.example.adrogozhilov.bonus.zxing.IntentResult;

/**
 * Created by a.drogozhilov on 09.12.2016.
 */
public class Main4Activity extends AppCompatActivity implements View.OnClickListener, SoundPool.OnLoadCompleteListener {
    final String LOG_TAG = "myLogs";
    final int MAX_STREAMS = 5;
    private Button scanBtn;
    private TextView cnameou;
    private String cmol;
    DBHelper dbHelper;
    SoundPool sp;
    int soundIdShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LOG_P", "activity 4");
        cmol = (String) getIntent().getSerializableExtra("lico");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
        sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);

        soundIdShot = sp.load(this, R.raw.sou, 1);
        Log.d(LOG_TAG, "soundIdShot = " + soundIdShot);

        //cnameou = (EditText) findViewById(R.id.editkod);
        //cmol = (TextView) findViewById(R.id.mol);
        Log.d("LOG_P", "activity 5");


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button5) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
//we have a result
            String scanContent = scanningResult.getContents();
            //String scanFormat = scanningResult.getFormatName();
            EditText editkod = (EditText) findViewById(R.id.editkod);
            editkod.setText(scanContent); // показали штрихкод прочитанный
            findbase(scanContent);  // Поиск в base штрихкода


        } else

        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    // поиск кода в базе.
    private void findbase(String strkod) {
        // смотрим в базу
        TextView textView = (TextView)findViewById(R.id.textpos);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c;
        String sqlQuery = "select name "
                + "from mytablebase  "
                + "where kod='"+strkod+"'";
        c = db.rawQuery(sqlQuery,null);
        if (c.getCount()!=0 ) {
            textView.setText(c.getString(c.getColumnIndex("name")));
            entvvod(c.getString(c.getColumnIndex("name")));

        }
        else {
            textView.setText("");
            entvvod("Кодне найден в базе, проверьте код.");
            sp.play(soundIdShot, 1, 1, 0, 0, 1);
        }
        c.close();
        dbHelper.close();


    }



    private void entvvod(String scomment) {
        Toast toast = Toast.makeText(getApplicationContext(), scomment, Toast.LENGTH_SHORT);
        toast.show();
    }

    //  заполняем в темповую таблицу
    public void onClickEnter(View view) {
        EditText editkod = (EditText) findViewById(R.id.editkod);
        TextView textView = (TextView)findViewById(R.id.textpos);

        if ("".equals(editkod.getText().toString())) {
            proverka();
        } else {
            switch (view.getId()) {
                case R.id.button6:
                    ContentValues cv = new ContentValues();

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    cv.put("name", textView.getText().toString());
                    cv.put("kod", editkod.getText().toString());
                    cv.put("kol", 1);
                    //cv.put("name2", editText2.getText().toString()); // МОЛ


                    entvvod(" " + cmol + " код:" + editkod.getText().toString() + " " + 1 + " шт.\n Сохранено.");
                    db.insert("mytabletmp", null, cv);
                    dbHelper.close();
                    editkod.setText("");
                    textView.setText("");

                    break;
                default:
                    break;
            }

        }


    }

    // Удаляем если что,
    public void onClickClear(View view) {
        switch (view.getId()) {
            case R.id.button7:
                // подключаемся к БД
                // создаем объект для данных
                ContentValues cv = new ContentValues();

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                int clearCount = db.delete("mytabletmp", null, null);
                Log.d("datete", "deleted rows count = " + clearCount);
                entvvod("Удалено  " + clearCount + " записей");
                dbHelper.close();
                break;
            // вызов 3его активити

            default:
                break;
        }


    }

    public void proverka() {
        //     TextView textView = (TextView)findViewById(R.id.txtb);
// задаём текст
        //      textView.setText("Вы не ввели номер телефона...");
        //      textView = (TextView)findViewById(R.id.txtbon);
        //       textView.setText(" ");
        Toast toast = Toast.makeText(getApplicationContext(), "Код не получен!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.d(LOG_TAG, "onLoadComplete, sampleId = " + sampleId + ", status = " + status);
    }
}
