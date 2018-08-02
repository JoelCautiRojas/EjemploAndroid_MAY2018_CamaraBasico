package com.programadoresperuanos.www.ejemploandroid_may2018_camara_basico;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Main2Activity extends AppCompatActivity {

    // Layout Views
    Snackbar snak1, snak2;
    RelativeLayout l;
    ImageView fotografia_mostrada;
    ImageButton botonCamara;
    TextView mostrarDetallesDeImagen;
    SeekBar sb_progreso;
    EditText porcentaje;
    RadioGroup formatos_imgs;
    Switch sw;
    // Objects
    File archivoImagenTemp;
    File archivoImagen;
    File carpeta;
    Bitmap bitmapImagen;
    OutputStream transmisionImagen = null;
    Uri uri_imagen;
    Bitmap.CompressFormat com_format = Bitmap.CompressFormat.JPEG;;
    // Auxiliares
    String nombreImagen;
    String rutaImagen;
    int calidadImagen = 100;
    String formatoImagen = ".jpg";
    // Constantes
    public static final int REQUEST_IMAGE_CAPTURE = 100 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Find Views
        l = findViewById(R.id.milayout);
        sw = findViewById(R.id.switch1);
        fotografia_mostrada = findViewById(R.id.imageView);
        mostrarDetallesDeImagen = findViewById(R.id.textView);
        sb_progreso = findViewById(R.id.seekBar);
        porcentaje = findViewById(R.id.editText);
        formatos_imgs = findViewById(R.id.formatos);
        botonCamara = findViewById(R.id.imageButton);

        calidadImagen = Integer.parseInt(porcentaje.getText().toString());
        botonCamara.setEnabled(false);
        // Eventos
        formatos_imgs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i)
                {
                    case R.id.radioButton:
                        formatoImagen = ".jpg";
                        com_format = Bitmap.CompressFormat.JPEG;
                        break;
                    case R.id.radioButton2:
                        formatoImagen = ".png";
                        com_format = Bitmap.CompressFormat.PNG;
                        break;
                    case R.id.radioButton3:
                        formatoImagen = ".webp";
                        com_format = Bitmap.CompressFormat.WEBP;
                        break;
                }
            }
        });
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
            }
        });
        porcentaje.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (Integer.parseInt(porcentaje.getText().toString()) <= 100 && Integer.parseInt(porcentaje.getText().toString()) >= 0)
                {
                    if(i == EditorInfo.IME_ACTION_DONE ||
                            i == EditorInfo.IME_ACTION_SEARCH ||
                            keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                    keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        sb_progreso.setProgress(Integer.parseInt(porcentaje.getText().toString()));
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getString(R.string.recurso_java8),Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        sb_progreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                porcentaje.setText(String.valueOf(seekBar.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        snak1 = Snackbar.make(l,getString(R.string.recurso_java5),Snackbar.LENGTH_INDEFINITE);
        snak1.setAction(getString(R.string.recurso_java6), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hacerPeticion();
            }
        });
        snak2 = Snackbar.make(l,getString(R.string.recurso_java7),Snackbar.LENGTH_LONG);
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
        calidadImagen = Integer.parseInt(porcentaje.getText().toString());
        // Crear carpeta Pictures en Proveedores de contenido
        carpeta = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        boolean estado_carpeta = carpeta.exists();
        if(!estado_carpeta){
            estado_carpeta = carpeta.mkdirs();
        }
        if(estado_carpeta){
            nombreImagen = String.valueOf(System.currentTimeMillis()/1000);
            // Crear imagen en blanco
            archivoImagen = new File(carpeta,nombreImagen+formatoImagen);
            // Solicitar Uri del Proveedor de contenido
            uri_imagen = FileProvider.getUriForFile(this,"com.programadoresperuanos.www.android.fileprovider",archivoImagen);
            rutaImagen = archivoImagen.getAbsolutePath();
        }
        PackageManager administrador = getApplicationContext().getPackageManager();
        if(administrador.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // "android.media.action.IMAGE_CAPTURE"
            camara.putExtra(MediaStore.EXTRA_OUTPUT,uri_imagen);
            if(camara.resolveActivity(getPackageManager()) != null)
            {
                startActivityForResult(camara,REQUEST_IMAGE_CAPTURE);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),getString(R.string.recurso_java4),Toast.LENGTH_LONG).show();
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

    @SuppressLint({"SetTextI18n", "ShowToast"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE:
                    // Obtener datos al retorno de la camara
                    bitmapImagen = BitmapFactory.decodeFile(rutaImagen);
                    int anchoImagen = bitmapImagen.getWidth();
                    int altoImagen = bitmapImagen.getHeight();
                    BitmapFactory.Options opciones = new BitmapFactory.Options();
                    opciones.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(rutaImagen,opciones);
                    int anchoFoto = opciones.outWidth;
                    int altoFoto = opciones.outHeight;
                    int scaleFactor = 100/calidadImagen;
                    opciones.inJustDecodeBounds = false;
                    opciones.inSampleSize = scaleFactor;
                    opciones.inPurgeable = true;
                    Bitmap imagenEscalada = BitmapFactory.decodeFile(rutaImagen,opciones);
                    fotografia_mostrada.setImageBitmap(bitmapImagen);
                    // Compartir en Galeria
                    if(sw.isChecked())
                    {
                        Intent mediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScan.setData(Uri.fromFile(archivoImagen));
                        this.sendBroadcast(mediaScan);
                    }
                    mostrarDetallesDeImagen.setText(
                        getString(R.string.recurso_java1)+
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES)+
                        "\r\n"+getString(R.string.recurso_java2)+
                        archivoImagen.getName()+"\r\n"+getString(R.string.recurso_java10)+String.valueOf(anchoImagen)+"px X "+String.valueOf(altoImagen)+"px"+"\r\n"+
                        getString(R.string.recurso_java10)+String.valueOf(imagenEscalada.getWidth())+"px X "+String.valueOf(imagenEscalada.getHeight())+"px"
                    );
                    break;
            }
        }
    }
}
