package com.example.permisos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int VENGO_DE_LA_CAMARA = 1;
    private static final int PEDIR_PERMISO_DE_ESCRITURA = 1;
    private static final int VENGO_DE_LA_CAMARA_CON_FICHERO = 2;
    Button hacerFoto,hacerfoto2;
    ImageView imageView;
    File fichero;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == VENGO_DE_LA_CAMARA && resultCode==RESULT_OK){

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(bitmap);

        }else if(requestCode==VENGO_DE_LA_CAMARA_CON_FICHERO && resultCode==RESULT_OK){

            imageView.setImageBitmap(BitmapFactory.decodeFile(fichero.getAbsolutePath()));

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    //Truco del almendruco para que los ficheros que yo haga los pueda ver la cámara
        
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        hacerFoto = findViewById(R.id.buttonFoto);
        hacerfoto2 = findViewById(R.id.buttonCamara2);
        imageView = findViewById(R.id.imageView);

        hacerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager())!=null){

                    startActivityForResult(intent,VENGO_DE_LA_CAMARA);

                }
                else{

                    Toast.makeText(MainActivity.this,"Necesitas programa que haga fotos",Toast.LENGTH_LONG);
                }
            }
        });


    hacerfoto2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            pedirPermisoParaFoto();


        }
    });


    }

    private void pedirPermisoParaFoto() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            //Caso: no tengo permisos

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                //Cuando el usuario ha rechazado varias veces el permiso

            }
            else{

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PEDIR_PERMISO_DE_ESCRITURA);

            }

        }else{

            //Caso: Tengo permiso

                hacerFotoAltaResolucion();


        }

    }


    private File crearFicheroDeFoto() throws IOException {

        String fechaYHora = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreFichero = "misFotos_"+fechaYHora;

            File carpetaFotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imagenAltaResolucion = File.createTempFile(nombreFichero,".jpg",carpetaFotos);

            return imagenAltaResolucion;

    }

    private void hacerFotoAltaResolucion() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            fichero = crearFicheroDeFoto();
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fichero));

        if(intent.resolveActivity(getPackageManager())!=null){


            startActivityForResult(intent,VENGO_DE_LA_CAMARA_CON_FICHERO);

        }
        else{

            Toast.makeText(MainActivity.this,"Necesitas programa que haga fotos",Toast.LENGTH_LONG);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if(requestCode==PEDIR_PERMISO_DE_ESCRITURA){

                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    hacerFotoAltaResolucion();

                }
                else{

                    Toast.makeText(this,"Sin permiso de escritura no puedo hacer fotos HD. ",Toast.LENGTH_LONG);


                }

            }

    }
}