package com.example.adrogozhilov.bonus;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.adrogozhilov.bonus.zxing.IntentIntegrator;
import com.example.adrogozhilov.bonus.zxing.IntentResult;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase db;
    public String cquery;
    Button btnActTwo;
    JSONObject contactsMap = new JSONObject();
    ArrayList aspisinvent = new ArrayList();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // создаем объект для создания и управления версиями БД .
        dbHelper = new DBHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            onClickSetting();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Нажатие на инфо
    public void onClickSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Информация")
                .setMessage("автор: " + getString(R.string.cauthor) + "\n" + getString(R.string.cversion))
                        //.setIcon(R.drawable.)
                .setCancelable(false)
                .setNegativeButton("Закрыть",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void proverka() {

        Toast toast = Toast.makeText(getApplicationContext(), "Вы не ввели хозяина рабочего места...", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void entvvod(String scomment) {
        Toast toast = Toast.makeText(getApplicationContext(), scomment, Toast.LENGTH_SHORT);
        toast.show();
    }

    public int dbread(SQLiteDatabase db, String tbl) {
        Cursor c = db.query(tbl, null, null, null, null, null, null);
        int nint = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    nint = 1;
                } while (c.moveToNext());
            }
            c.close();
        }
        return nint;
    }
    // заполняем базу
    private void dbsynchro(SQLiteDatabase db, String res) {
       // Cursor c = db.query(tbl, null, null, null, null, null, null);
        int clearCount;
        int nint = 0;
        JSONObject dataJsonObj= null;
        try {
            dataJsonObj = new JSONObject(res);
            JSONArray datanum = dataJsonObj.getJSONArray("number");
            JSONArray dataname = dataJsonObj.getJSONArray("name");
            JSONArray dataean = dataJsonObj.getJSONArray("ean");
            for(int i=0;i<dataname.length();i++ ){
                ContentValues cv = new ContentValues();
                cv.put("name", dataname.getString(i).trim());
                cv.put("kod", dataean.getString(i).trim());
                cv.put("inum", datanum.getString(i).trim());
                cv.put("kol", 1);
                cv.put("name2", ""); // МОЛ
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
                nint = nint + 1;
                db.insert("mytablebase", null, cv);
                //Log.d("RESIMP", dataname.getString(i).trim());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void dbreadclear(SQLiteDatabase db, String tbl) {
        Cursor c = db.query(tbl, null, null, null, null, null, null);
        int clearCount;
        int nint = 0;
        if (c.moveToFirst()) {
            // создаем объект для данных
            do {
                ContentValues cv = new ContentValues();
                cv.put("name", c.getString(c.getColumnIndex("name")));
                cv.put("kod", c.getString(c.getColumnIndex("kod")));
                cv.put("kol", 1);
                cv.put("name2", cquery); // МОЛ
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
                nint = nint + 1;
                db.insert("mytable", null, cv);
            } while (c.moveToNext());
            //   entvvod(" По " + cquery + " сохранено" + nint + " данные  \n Сохранено.");
            c.close();
            clearCount = db.delete(tbl, null, null);
            Log.d("datete", "deleted rows count = " + clearCount);
            entvvod(" По " + cquery + " сохранено" + nint + " данные  \n Сохранено." + clearCount + " записей");
            //entvvod(" По "+cquery + " сохранено" + nint+" данные  \n Сохранено."+"\n Чистка -  "+cquery+ clearCount + " записей");
        } else
            Log.d("0", "0 rows");

    }


    // Кнопка "Сохранение"
    public void onClick(View view) {
        int nsumma = 0;
        int nint = 0;
        int clearCount;
        EditText editText2 = (EditText) findViewById(R.id.editText2);

        cquery = editText2.getText().toString();
        cquery = cquery.replaceAll(" ", "");

        if ("".equals(cquery)) {
            proverka();
        } else {
            // подключаемся к БД считываем темповую и записываем в основную
            db = dbHelper.getWritableDatabase();
            dbreadclear(db, "mytabletmp");
            dbHelper.close();
        }

        editText2.setText(""); // Записали и чистим лицо
    }

    public void OnClick2(View view) {
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        cquery = editText2.getText().toString();
        cquery = cquery.replaceAll(" ", "");
        switch (view.getId()) {
            case R.id.button2:
                Task2 tt2 = new Task2();
                tt2.execute();
                // TODO Call second activity


                break;
            default:
                break;
        }


    }

    public void onClick4(View view) {
        int flag = 0;
        EditText editText2 = (EditText) findViewById(R.id.editText2);
        cquery = editText2.getText().toString();
        if ("".equals(cquery)) {
            proverka();
        } else {
            switch (view.getId()) {
                case (R.id.button4):

                    Log.d("LOG_4", "Старт");
                    // Проверяем наличие не сохраненной операции
                    db = dbHelper.getWritableDatabase();
                    flag = dbread(db, "mytabletmp");
                    dbHelper.close();
                    // TODO Call second activity
                    // если есть не сохраненные данные, предупреждение
                    if (flag == 1) {
                        entvvod(getString(R.string.noclear));
                    } else {
                        Intent intent = new Intent(MainActivity.this, Main4Activity.class);
                        intent.putExtra("lico", cquery);
                        startActivity(intent);
                    }


                    break;
                default:
                    break;
            }

        }


    }

    // Нажали все удалить, всю базу
    public void OnClick3(View view) {

        EditText editText = (EditText) findViewById(R.id.editText2);
        //EditText editkol=(EditText)findViewById(R.id.editkol);
        cquery = editText.getText().toString();
        cquery = cquery.replaceAll(" ", "");

        switch (view.getId()) {
            case R.id.button3:
                // Предупреждение об удалении
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_about_title);
                builder.setMessage(R.string.dialog_about_message);
                builder.setCancelable(true);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                        db = dbHelper.getWritableDatabase();
                        int clearCount = db.delete("mytable", null, null);
                        entvvod("Удалено  " + clearCount + " записей");
                        aspisinvent.clear(); // чистим коллекцию
                        dbHelper.close();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                // AlertDialog dialog = builder.create();
                builder.show();


                break;
            // вызов 3его активити

            default:
                break;
        }


    }
// Сохраняем файл на сервере
    public void onClickSave(View view) {
        EditText editText = (EditText) findViewById(R.id.fname);
        cquery = editText.getText().toString();
        cquery = cquery.replaceAll(" ", "");

        Task3 tt3 = new Task3();
        tt3.execute();
    }

    public void onClickSynch(View view) {
        Task4 tt4 = new Task4();
        tt4.execute();


    }

    public void onClickEraseBase(View view) {
        switch (view.getId()) {
            case R.id.button10:
                // Предупреждение об удалении
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_about_title);
                builder.setMessage(R.string.dialog_about_message);
                builder.setCancelable(true);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                        db = dbHelper.getWritableDatabase();
                        int clearCount = db.delete("mytablebase", null, null);
                        entvvod("Удалено  " + clearCount + " записей");
                        //aspisinvent.clear(); // чистим коллекцию
                        dbHelper.close();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                // AlertDialog dialog = builder.create();
                builder.show();


                break;
            // вызов 3его активити

            default:
                break;
        }

    }

    //для показывание записей, смотрим базу
    //**************************************** Второе активитиВсе записи**********************************************
    class Task2 extends AsyncTask<Void, Void, String> {
        private ProgressDialog spinner;
        private String responseString = "";

        @Override
        protected void onPreExecute() {
            spinner = new ProgressDialog(MainActivity.this);
            spinner.setMessage("Идет загрузка...");
            spinner.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... urls) {


            return responseString;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            boolean bFlag = false;
            spinner.dismiss();
            super.onPostExecute(result);
            ArrayList anames = new ArrayList();
            int i = 0;
            ContentValues cv = new ContentValues();

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor c = db.query("mytable", null, null, null, null, null, null);
            if (c.moveToFirst()) {

                // определяем номера столбцов по имени в выборке
                int idColIndex = c.getColumnIndex("id");
                int nameCol = c.getColumnIndex("name");
                int nameMOL = c.getColumnIndex("name2");
                int KolCol = c.getColumnIndex("kol");

                do {
                    anames.add("№" + c.getInt(idColIndex) +
                            " Лицо: " + c.getString(nameMOL) +
                            ", код " + c.getString(c.getColumnIndex("kod")) +
                            ", наименование = " + c.getString(nameCol) +
                            ", количество = " + c.getString(KolCol));


                    // получаем значения по номерам столбцов и пишем все в лог

                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то false - выходим из цикла
                    //Log.d("KOL", c.getString(nameCol));
                } while (c.moveToNext());
            } else
                Log.d("0", "0 rows");
            c.close();
            dbHelper.close();


            if (anames.size() == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Нет записей.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                // intent.putExtra(EXTRA_QUERY, cquery);
                intent.putExtra("anames", anames);
                startActivity(intent);


            }


        }
    }
// Заносим в arraylist результаты для передачи на сервер
    public void baseinmas() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameCol = c.getColumnIndex("name");
            int nameMOL = c.getColumnIndex("name2");
            int KolCol = c.getColumnIndex("kol");

            do {
                aspisinvent.add("|" + c.getInt(idColIndex) +
                        "|" + c.getString(nameMOL) +
                        "|" + c.getString(c.getColumnIndex("kod")) +
                        "|" + c.getString(nameCol) +
                        "|" + c.getString(KolCol)+"|#");


                // получаем значения по номерам столбцов и пишем все в лог

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d("0", "0 rows");
        c.close();
        dbHelper.close();

    }



    //***************************************************************
    class Task3 extends AsyncTask<Void, Void, String> {
        private ProgressDialog spinner;
        private String responseString = "";

        @Override
        protected void onPreExecute() {
            spinner = new ProgressDialog(MainActivity.this);
            spinner.setMessage("Идет загрузка...");
            spinner.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... urls) {

            // заносим наш результат
            baseinmas();
            String Smap = aspisinvent.toString();

            Log.d("TAGSMAP", "ddd" + Smap);

            HttpPost httppost = new HttpPost(getString(R.string.cConnectServ));
            List nameValuePairs = new ArrayList(1);
            HttpClient httpclient = new DefaultHttpClient();




            try {

                // кодируем для передачи post
                nameValuePairs.add(new BasicNameValuePair("Phonebook", URLEncoder.encode(Smap, "UTF-8")));
                nameValuePairs.add(new BasicNameValuePair("filename", URLEncoder.encode(cquery, "UTF-8")));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                responseString = httpclient.execute(httppost, new BasicResponseHandler());

                Log.d("TAGHO", responseString);
                Log.d("TAGH11111111", cquery);
                responseString="Файл сохранен.";
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                responseString="error";
                e.printStackTrace();
            }

            return responseString;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            spinner.dismiss();
            super.onPostExecute(result);
            Toast toast = Toast.makeText(getApplicationContext(),result, Toast.LENGTH_SHORT);
            toast.show();

            Log.d("TGS", result);


        }
    }



    class Task4 extends AsyncTask<Void, Void, String> {
        private ProgressDialog spinner;
        private String responseString = "";

        @Override
        protected void onPreExecute() {
            spinner = new ProgressDialog(MainActivity.this);
            spinner.setMessage("Идет загрузка...");
            spinner.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... urls) {





            HttpGet httppost = new HttpGet(getString(R.string.cConnectSync));
           // List nameValuePairs = new ArrayList(1);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;




            try {

                /* кодируем для передачи post
                nameValuePairs.add(new BasicNameValuePair("Phonebook", URLEncoder.encode(Smap, "UTF-8")));
                nameValuePairs.add(new BasicNameValuePair("filename", URLEncoder.encode(cquery, "UTF-8")));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                responseString = httpclient.execute(httppost, new BasicResponseHandler());
                */
                response = httpclient.execute(httppost);
                responseString = EntityUtils.toString(response.getEntity(), "UTF-8");//response.toString() ;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                responseString="error";
                e.printStackTrace();
            }

            return responseString;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            db = dbHelper.getWritableDatabase();
            dbsynchro(db,result);
            spinner.dismiss();
            dbHelper.close();
            Log.d("TGS", result);


        }
    }


}
