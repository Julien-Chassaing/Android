package com.losrobertoshermanos.ppe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cashcash.R;

public class Insertion extends AppCompatActivity {
    Button b1;
    DatabaseHelper db;
    EditText e1;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_insertion);
        final String mat = getIntent().getStringExtra("matricule");
        this.db = new DatabaseHelper(this);
        this.e1 = (EditText) findViewById(R.id.nbMachine);
        Button button = (Button) findViewById(R.id.validation);
        this.b1 = button;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nbMat = Insertion.this.e1.getText().toString();
                if (nbMat.equals("") || nbMat.equals("0")) {
                    Toast.makeText(Insertion.this.getApplicationContext(), "Veuillez renseigner le nombre de machine", 0).show();
                    return;
                }
                Intent i = new Intent(Insertion.this, Validation.class);
                i.putExtra("matricule", mat);
                i.putExtra("nbMat", nbMat);
                Insertion.this.startActivity(i);
            }
        });
    }
}
