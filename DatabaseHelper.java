package com.losrobertoshermanos.ppe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "cashcash.db", (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table IF NOT EXISTS utilisateur (id int(11) CONSTRAINT AUTO_INCREMENT NOT NULL, login varchar(50), mdp varchar(50), statut varchar(50), matricule varchar(50), PRIMARY KEY (id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS intervention (numero_intervention, date_visite, heure_visite, matricule_technicien, numero_client, validation , PRIMARY KEY (numero_intervention))");
        db.execSQL("CREATE TABLE IF NOT EXISTS controler (numero_serie, numero_intervention, temps_passer, commentaire, PRIMARY KEY (numero_serie))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists utilisateur");
        db.execSQL("drop table if exists intervention");
        db.execSQL("drop table if exists controler");
    }

    public Boolean loginmdp(String login, String mdp) {
        if (getReadableDatabase().rawQuery("select * from utilisateur where login=? and mdp=?", new String[]{login, mdp}).getCount() > 0) {
            return true;
        }
        return false;
    }

    public String matricule(String login, String mdp) {
        Cursor cursor = getReadableDatabase().rawQuery("select matricule from utilisateur where login=? and mdp=?", new String[]{login, mdp});
        String matricule = "";
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                matricule = cursor.getString(0);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return matricule;
    }

    public Boolean inter(String matricule) {
        if (getReadableDatabase().rawQuery("select * from intervention where validation = '0' and matricule_technicien =?", new String[]{matricule}).getCount() > 0) {
            return true;
        }
        return false;
    }

    public void insert(String numSerie, String commentaire, String heure, String numInter) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Insert into controler (numero_serie, numero_intervention, temps_passer, commentaire) values ('" + numSerie + "','" + numInter + "','" + heure + "','" + commentaire + "')");
    }

    public String numInter(String matricule) {
        String numInter = null;
        Cursor cursor = getReadableDatabase().rawQuery("Select numero_intervention from intervention where validation = '0' and matricule_technicien = ?", new String[]{matricule});
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                numInter = cursor.getString(0);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return numInter;
    }

    public void updateInter(String matricule) {
        getWritableDatabase().execSQL("UPDATE intervention SET validation= '1' Where validation = '0' and matricule_technicien = ?", new String[]{matricule});
    }

    public List<Controler> readLesControles() {
        List<Controler> lesControles = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("Select * from controler", (String[]) null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                lesControles.add(new Controler(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return lesControles;
    }

    public void deleteControle(Controler unControle) {
        String numSerie = unControle.getN_serie().replace("'", "''");
        getWritableDatabase().execSQL("DELETE FROM controler WHERE numero_serie = '" + numSerie + "' AND numero_intervention = '" + unControle.getId_intervention() + "'");
        Log.i("DATABASE", "delete");
    }

    public void insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("numero_intervention", queryValues.get("numero_intervention"));
        values.put("date_visite", queryValues.get("date_visite"));
        values.put("heure_visite", queryValues.get("heure_visite"));
        values.put("matricule_technicien", queryValues.get("matricule_technicien"));
        values.put("numero_client", queryValues.get("numero_client"));
        values.put("validation", queryValues.get("validation"));
        database.insert("intervention", (String) null, values);
        database.close();
    }
}
