package com.programadoresperuanos.www.ejemploandroid_may2018_camara_basico;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity {

    TextView firma_titulo, firma_enlaceweb, firma_repositorio;
    ImageView firma_logo;
    CardView firma_autor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        firma_titulo = findViewById(R.id.firma_titulo);
        firma_enlaceweb = findViewById(R.id.firma_enlaceweb);
        firma_repositorio = findViewById(R.id.firma_repositorio);
        firma_logo = findViewById(R.id.firma_logo);
        firma_autor = findViewById(R.id.firma_autor);

        firma_titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_enlaceWeb();
            }
        });
        firma_enlaceweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_enlaceWeb();
            }
        });
        firma_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_enlaceWeb();
            }
        });
        firma_repositorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_repositorio();
            }
        });
        firma_autor.setClickable(true);
        firma_autor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_autorEnlace();
            }
        });
    }

    private void open_autorEnlace() {
        Uri uriautor = Uri.parse(getString(R.string.firma_autorEnlace));
        startActivity(new Intent(Intent.ACTION_VIEW,uriautor));
    }

    private void open_repositorio() {
        Uri urirep = Uri.parse(getString(R.string.firma_repositorio));
        startActivity(new Intent(Intent.ACTION_VIEW,urirep));
    }

    private void open_enlaceWeb() {
        Uri uriweb = Uri.parse(getString(R.string.firma_enlaceWeb));
        startActivity(new Intent(Intent.ACTION_VIEW,uriweb));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mimenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.opcion1:
                startActivity(new Intent(this,MainActivity.class));
                return true;
            case R.id.opcion2:
                startActivity(new Intent(this,Main2Activity.class));
                return true;
            case R.id.opcion3:
                startActivity(new Intent(this,Main3Activity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
