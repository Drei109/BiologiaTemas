package com.biologia.temas.biologiatemas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AgregarTema extends AppCompatActivity {

    private static final int PHOTO_REQUEST = 9002;
    private static final int REQUEST_READ_PERMISSION = 9003;

    private FloatingActionButton btnPick;
    private Button btnSave;
    private ImageView imageView;
    private EditText etTitulo, etDescripcion, etHecho1, etHecho2;
    private String imagenUrl;

    private Uri photoUri;

    private Tema temaNuevo;
    Context context;

    private StorageReference mStorageRef;
    FirebaseDatabase bdTemas;
    DatabaseReference refTemas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tema);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        bdTemas = FirebaseDatabase.getInstance();
        refTemas = bdTemas.getReference("temas");


        context=this;

        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
        etHecho1 = (EditText) findViewById(R.id.etHecho1);
        etHecho2 = (EditText) findViewById(R.id.etHecho2);
        btnPick = (FloatingActionButton) findViewById(R.id.fabImagen);
        btnSave = (Button) findViewById(R.id.btnGuardar);
        imageView = (ImageView) findViewById(R.id.imgTema);


        //attaching listener
        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePicker();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

    }


    private void showFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PHOTO_REQUEST);
    }

    private void uploadFile() {

        final String titulo = etTitulo.getText().toString().trim();
        final String descripcion = etDescripcion.getText().toString().trim();
        final String hecho_relevante_1 = etHecho1.getText().toString().trim();
        final String hecho_relevante_2 = etHecho2.getText().toString().trim();

        if (photoUri != null && !TextUtils.isEmpty(titulo) && !TextUtils.isEmpty(descripcion)) {

            final String id = refTemas.push().getKey();

            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Guardando");
            progressDialog.show();

            StorageReference riversRef = mStorageRef.child("images/" + id + ".jpg");
            riversRef.putFile(photoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            imagenUrl = taskSnapshot.getDownloadUrl()!= null ? taskSnapshot.getDownloadUrl().toString() : "";
                            temaNuevo = new Tema(titulo,descripcion,hecho_relevante_1,hecho_relevante_2,imagenUrl);
                            refTemas.child(id).setValue(temaNuevo);

                            Toast.makeText(getApplicationContext(), "Tema Guardado", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context,MainActivity.class);
                            context.startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Guardando " + ((int) progress) + "%...");
                        }
                    });

            /*temaNuevo = new Tema(titulo,descripcion,hecho_relevante_1,hecho_relevante_2,imagenUrl);
            refTemas.child(id).setValue(temaNuevo);*/
        }
        else {
            Toast.makeText(getApplicationContext(), "Debe completar todos los datos antes de guardar", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
