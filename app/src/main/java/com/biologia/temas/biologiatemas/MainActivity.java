package com.biologia.temas.biologiatemas;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{


    private FloatingActionButton fabAgregarTema;

    FirebaseDatabase bdTemas;
    DatabaseReference refTemas ;
    List<Tema> list;
    List<String> idList;
    RecyclerView recycle;
    private static Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcontext = this;
        recycle = (RecyclerView) findViewById(R.id.recycle);
        bdTemas = FirebaseDatabase.getInstance();
        refTemas = bdTemas.getReference("temas");

        refTemas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<Tema>();
                idList = new ArrayList<String>();

                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                    Tema temaDatos = dataSnapshot1.getValue(Tema.class);
                    Tema temaNuevo = new Tema();

                    String id = dataSnapshot1.getKey();

                    String titulo = temaDatos.getTitulo();
                    String descripcion = temaDatos.getDescripcion();
                    String hecho1 = temaDatos.getHecho_relevante_1();
                    String hecho2 = temaDatos.getHecho_relevante_2();
                    String imagen = temaDatos.getImagen();

                    temaNuevo.setTitulo(titulo);
                    temaNuevo.setDescripcion(descripcion);
                    temaNuevo.setHecho_relevante_1(hecho1);
                    temaNuevo.setHecho_relevante_2(hecho2);
                    temaNuevo.setImagen(imagen);
                    list.add(temaNuevo);
                    idList.add(id);
                }
                updateRecycler();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });



        fabAgregarTema = (FloatingActionButton) findViewById(R.id.fabAgregarTema);
        fabAgregarTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, AgregarTema.class);
                mcontext.startActivity(intent);
            }
        });

    }

    private void updateRecycler() {
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,idList,MainActivity.this);
        //RecyclerView.LayoutManager recyce = new GridLayoutManager(MainActivity.this,2);
        RecyclerView.LayoutManager recyce = new LinearLayoutManager(MainActivity.this);
        //recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        //RecyclerView.LayoutManager recyce = new StaggeredGridLayoutManager(2,1);
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
