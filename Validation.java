package com.losrobertoshermanos.ppe;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cashcash.R;

import java.util.Calendar;

public class Validation extends AppCompatActivity {
    Button b1;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    DatabaseHelper db;
    EditText e1;
    EditText e2;
    EditText heure;
    TimePickerDialog timePickerDialog;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_validation);
        Intent intent = getIntent();
        int nombreMat = Integer.parseInt(intent.getStringExtra("nbMat"));
        String mat = intent.getStringExtra("matricule");
        this.db = new DatabaseHelper(this);
        this.heure = (EditText) findViewById(R.id.heure);
        this.e1 = (EditText) findViewById(R.id.comValidation);
        this.e2 = (EditText) findViewById(R.id.numSerie);
        this.b1 = (Button) findViewById(R.id.validationMat);
        final int[] page = {1};
        String numInter = this.db.numInter(mat);
        this.heure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Validation.this.calendar = Calendar.getInstance();
                Validation.this.currentHour = 11;
                Validation.this.currentMinute = 12;
                Validation.this.timePickerDialog = new TimePickerDialog(Validation.this, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        EditText editText = Validation.this.heure;
                        editText.setText(hourOfDay + ":" + minute);
                    }
                }, Validation.this.currentHour, Validation.this.currentMinute, true);
                Validation.this.timePickerDialog.show();
            }
        });
        final int i = nombreMat;
        final String str = mat;
        final String str2 = numInter;
        this.b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comInter = Validation.this.e1.getText().toString();
                String numSerie = Validation.this.e2.getText().toString();
                String h = Validation.this.heure.getText().toString();
                if (page[0] == i) {
                    Validation.this.db.updateInter(str);
                    Validation.this.startActivity(new Intent(Validation.this, MainActivity.class));
                }
                if (numSerie.matches("") || h.matches("") || comInter.matches("")) {
                    Toast.makeText(Validation.this, "Veuillez saisir toutes les informations nécessaires !", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Validation.this.db.insert(numSerie, comInter, h, str2);
                    Validation.this.e2.setText("");
                    Validation.this.heure.setText("");
                    Validation.this.e1.setText("");
                    int[] iArr = page;
                    iArr[0] = iArr[0] + 1;
                } catch (Exception e) {
                    Toast.makeText(Validation.this, "Saisie Incorrecte", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
