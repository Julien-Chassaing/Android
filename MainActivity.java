package com.losrobertoshermanos.ppe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.cashcash.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button b1;
    Button b2;
    Button b3;
    Button b4;
    DatabaseHelper db;
    EditText e1;
    EditText e2;
    /* access modifiers changed from: private */
    public MediaPlayer mediaPlayer;
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        this.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gta_song);
        this.db = new DatabaseHelper(this);
        this.e1 = (EditText) findViewById(R.id.username);
        this.e2 = (EditText) findViewById(R.id.mdp);
        this.b1 = (Button) findViewById(R.id.login);
        this.b4 = (Button) findViewById(R.id.refresh);
        
        this.b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String login = MainActivity.this.e1.getText().toString();
                String mdp = MainActivity.this.e2.getText().toString();
                Boolean Chkeloginmdp = MainActivity.this.db.loginmdp(login, mdp);
                String matricule = MainActivity.this.db.matricule(login, mdp);
                if (Chkeloginmdp.booleanValue() && MainActivity.this.db.inter(matricule).booleanValue()) {
                    Context applicationContext = MainActivity.this.getApplicationContext();
                    Toast.makeText(applicationContext, "Connexion réussie " + matricule + " ", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, Insertion.class);
                    i.putExtra("matricule", matricule);
                    MainActivity.this.startActivity(i);
                } else if (!Chkeloginmdp.booleanValue()) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Mauvais login ou mot de passe", Toast.LENGTH_SHORT).show();
                } else if (Chkeloginmdp.booleanValue() && !MainActivity.this.db.inter(matricule).booleanValue()) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Aucune intervention en cours", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerReceiver(new NetworkStateChecker(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.b4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.syncSQLiteMySQLDB();
                Log.i("Data", "jcp");
            }
        });
    }

    public void syncSQLiteMySQLDB() {
        new AsyncHttpClient().post("https://testandroid.mtxserv.com/android/getusers.php", new RequestParams(), new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "OKok", Toast.LENGTH_SHORT);
                MainActivity.this.updateSQLite(response);
            }

            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateSQLite(String response) {
        ArrayList<HashMap<String, String>> usersynclist = new ArrayList<>();
        Gson gson = new GsonBuilder().create();
        try {
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("numero_intervention"));
                    System.out.println(obj.get("date_visite"));
                    System.out.println(obj.get("heure_visite"));
                    System.out.println(obj.get("matricule_technicien"));
                    System.out.println(obj.get("numero_client"));
                    System.out.println(obj.get("validation"));
                    HashMap<String, String> hashMap = new HashMap<>();
                    this.queryValues = hashMap;
                    hashMap.put("numero_intervention", obj.get("numero_intervention").toString());
                    this.queryValues.put("date_visite", obj.get("date_visite").toString());
                    this.queryValues.put("heure_visite", obj.get("heure_visite").toString());
                    this.queryValues.put("matricule_technicien", obj.get("matricule_technicien").toString());
                    this.queryValues.put("numero_client", obj.get("numero_client").toString());
                    this.queryValues.put("validation", obj.get("validation").toString());
                    this.db.insertUser(this.queryValues);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("numero_intervention", obj.get("numero_intervention").toString());
                    map.put(NotificationCompat.CATEGORY_STATUS, "1");
                    usersynclist.add(map);
                }
                updateMySQLSyncSts(gson.toJson((Object) usersynclist));
                reloadActivity();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateMySQLSyncSts(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        client.post("https://testandroid.mtxserv.com/android/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Interventions mises à jours", Toast.LENGTH_LONG).show();
            }

            public void onFailure(int statusCode, Throwable error, String content) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reloadActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
