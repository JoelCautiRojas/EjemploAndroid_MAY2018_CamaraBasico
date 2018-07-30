package com.programadoresperuanos.www.ejemploandroid_may2018_camara_basico;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Main2Activity extends AppCompatActivity {

    Snackbar snak1, snak2;
    RelativeLayout l;
    ImageView fotografia;
    ImageButton botonCamara;
    TextView mostrarDetallesDeImagen;

    String nombreImagen;
    String rutaImagen;

    // Uri uriImagen;

    File archivoImagenTemp;
    Bitmap bitmapImagen;
    OutputStream transmisionImagen;
    int calidadImagen = 85;

    public static final String APP_DIRECTORY = "EjemploCamara/";
    public static final String MEDIA_DIRECTORY = APP_DIRECTORY+"Capturas";
    public static final int REQUEST_IMAGE_CAPTURE = 100 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        l = findViewById(R.id.milayout);
        fotografia = findViewById(R.id.imageView);
        botonCamara = findViewById(R.id.imageButton);
        mostrarDetallesDeImagen = findViewById(R.id.textView);
        botonCamara.setEnabled(false);
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
            }
        });
        snak1 = Snackbar.make(l,"Necesitas otorgar permisos para acceder a la camara.",Snackbar.LENGTH_INDEFINITE);
        snak1.setAction("solicitar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerPeticion();
            }
        });
        snak2 = Snackbar.make(l,"La camara ya se encuentra lista para iniciar.",Snackbar.LENGTH_LONG);
        if(verificarPermisos()){
            iniciarCamara();
        }else{
            justificarSolicitud();
        }
    }

    private void justificarSolicitud() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,WRITE_EXTERNAL_STORAGE)){
            snak1.show();
        }else{
            hacerPeticion();
        }
    }

    private void iniciarCamara() {
        botonCamara.setEnabled(true);
        snak2.show();
    }

    private boolean verificarPermisos() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        if(ActivityCompat.checkSelfPermission(this,CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void hacerPeticion() {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE},1);
    }

    private void tomarFoto() {
        PackageManager administrador = getApplicationContext().getPackageManager();
        if(administrador.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //Tambien se puede usar: Intent camara = new Intent("android.media.action.IMAGE_CAPTURE");
            if(camara.resolveActivity(getPackageManager()) != null)
            {
                //Anterior, no valido para android 7 en adelante: camara.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(archivoImagen));
                startActivityForResult(camara,REQUEST_IMAGE_CAPTURE);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Tu dispositivo no cuenta con un manejador de camaras compatible.",Toast.LENGTH_LONG).show();
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    snak2.show();
                    botonCamara.setEnabled(true);
                }else{
                    snak1.show();
                    botonCamara.setEnabled(false);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE:
                    File carpeta = new File(Environment.getExternalStorageDirectory(),MEDIA_DIRECTORY);
                    boolean estado_carpeta = carpeta.exists();
                    if(!estado_carpeta){
                        estado_carpeta = carpeta.mkdirs();
                    }
                    if(estado_carpeta){
                        nombreImagen = String.valueOf(System.currentTimeMillis()/1000);
                        try {
                            archivoImagenTemp = File.createTempFile(
                                    nombreImagen,
                                    ".jpg",
                                    carpeta
                            );
                            //Tambien puede usar: File Imagen = new File(carpeta,Environment.getExternalStorageDirectory()+File.separator+MEDIA_DIRECTORY+File.separator+nombreImagen+".jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        rutaImagen = archivoImagenTemp.getAbsolutePath();
                        //Tambien puede usar: rutaImagen = Environment.getExternalStorageDirectory()+File.separator+MEDIA_DIRECTORY+File.separator+nombreImagen+".jpg";
                        transmisionImagen = null;
                        try {
                            transmisionImagen = new FileOutputStream(archivoImagenTemp);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                    Bundle extras = data.getExtras();
                    bitmapImagen = (Bitmap) extras.get("data"); //BitmapFactory.decodeFile(rutaImagen);
                    fotografia.setImageBitmap(bitmapImagen);
                    bitmapImagen.compress(Bitmap.CompressFormat.JPEG,calidadImagen,transmisionImagen);
                    try {
                        transmisionImagen.flush();
                        transmisionImagen.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mostrarDetallesDeImagen.setText("Ubicacion: "+rutaImagen+"\r\n"+"Nombre de la Imagen: "+nombreImagen);
                    break;
            }
        }
    }


}
