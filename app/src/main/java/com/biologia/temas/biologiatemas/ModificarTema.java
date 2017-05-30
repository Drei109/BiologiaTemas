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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ModificarTema extends AppCompatActivity {

    private static final int PHOTO_REQUEST = 9001;

    private FirebaseDatabase bdTemas;
    private DatabaseReference refTemas;
    private StorageReference mStorageRef;

    private FloatingActionButton btnPick;
    private Button btnSave;
    private EditText etTitulo, etDescripcion, etHecho1, etHecho2;
    private ImageView imageView;
    private String imagenUrl;
    private Uri photoUri;
    Tema nuevoTema;

    Context context;

    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_tema);

        context = this;

        mStorageRef = FirebaseStorage.getInstance().getReference();
        bdTemas = FirebaseDatabase.getInstance();
        refTemas = bdTemas.getReference("temas");

        etTitulo = (EditText) findViewById(R.id.etTituloMod);
        etDescripcion = (EditText) findViewById(R.id.etDescripcionMod);
        etHecho1 = (EditText) findViewById(R.id.etHecho1Mod);
        etHecho2 = (EditText) findViewById(R.id.etHecho2Mod);
        btnSave = (Button) findViewById(R.id.btnGuardarMod);
        btnPick = (FloatingActionButton) findViewById(R.id.fabImagenMod);
        imageView = (ImageView) findViewById(R.id.imgTemaMod);

        id = getIntent().getStringExtra("ID");

        refTemas.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etTitulo.setText(dataSnapshot.getValue(Tema.class).getTitulo());
                etDescripcion.setText(dataSnapshot.getValue(Tema.class).getDescripcion());
                if(!dataSnapshot.getValue(Tema.class).getHecho_relevante_1().isEmpty()){
                    etHecho1.setText(dataSnapshot.getValue(Tema.class).getHecho_relevante_1());
                }
                if(!dataSnapshot.getValue(Tema.class).getHecho_relevante_2().isEmpty()){
                    etHecho2.setText(dataSnapshot.getValue(Tema.class).getHecho_relevante_2());
                }

                Picasso.with(context).load(dataSnapshot.getValue(Tema.class).getImagen()).into(imageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        if(photoUri != null ){
            if(!TextUtils.isEmpty(titulo) && !TextUtils.isEmpty(descripcion)){
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
                                refTemas.child(id).child("titulo").setValue(titulo);
                                refTemas.child(id).child("descripcion").setValue(descripcion);
                                refTemas.child(id).child("hecho_relevante_1").setValue(hecho_relevante_1);
                                refTemas.child(id).child("hecho_relevante_2").setValue(hecho_relevante_2);
                                refTemas.child(id).child("imagen").setValue(imagenUrl);

                                Intent intent = new Intent(context,MainActivity.class);
                                context.startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Tema Guardado", Toast.LENGTH_LONG).show();
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
            }
            else{
                Toast.makeText(getApplicationContext(), "Debe completar todos los datos antes de guardar", Toast.LENGTH_LONG).show();
            }
        }
        else{
            if(!TextUtils.isEmpty(titulo) && !TextUtils.isEmpty(descripcion)){
                refTemas.child(id).child("titulo").setValue(titulo);
                refTemas.child(id).child("descripcion").setValue(descripcion);
                refTemas.child(id).child("hecho_relevante_1").setValue(hecho_relevante_1);
                refTemas.child(id).child("hecho_relevante_2").setValue(hecho_relevante_2);

                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);
                Toast.makeText(getApplicationContext(), "Tema Guardado", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Debe completar todos los datos antes de guardar", Toast.LENGTH_LONG).show();
            }
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
