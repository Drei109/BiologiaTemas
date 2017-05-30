package com.biologia.temas.biologiatemas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.myHolder>{

    List<Tema> list;
    List<String> idList;
    Context context;
    String hecho_relevante_nuevo;

    private FirebaseDatabase bdTemas;
    private DatabaseReference refTemas;

    public RecyclerAdapter(List<Tema> list, List<String> idList,Context context) {
        this.list = list;
        this.idList = idList;
        this.context = context;
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        myHolder myHolder = new myHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final myHolder holder, int position) {
        Tema mylist = list.get(position);
        holder.titulo.setText(mylist.getTitulo());
        holder.descripcion.setText(mylist.getDescripcion());
        holder.hecho1.setText(mylist.getHecho_relevante_1());
        holder.hecho2.setText(mylist.getHecho_relevante_2());
        Picasso.with(context).load(mylist.getImagen()).into(holder.imgTemaMain);

        final String temaID = idList.get(position);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.overflow, temaID);
            }
        });

    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(list.size()==0){
                arr = 0;
            }
            else{
                arr=list.size();
            }
        }catch (Exception e){
        }
        return arr;
    }

    class myHolder extends RecyclerView.ViewHolder{
        TextView titulo,descripcion,hecho1,hecho2;
        ImageView imgTemaMain, overflow;

        public myHolder(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.tvTitulo);
            descripcion= (TextView) itemView.findViewById(R.id.tvDescripcion);
            hecho1= (TextView) itemView.findViewById(R.id.tvHecho1);
            hecho2= (TextView) itemView.findViewById(R.id.tvHecho2);
            imgTemaMain =  (ImageView) itemView.findViewById(R.id.imgTemaMain);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);

        }
    }

    private void showPopupMenu(View view, String id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_tema, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(id));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        String id;

        public MyMenuItemClickListener(String id) {
            this.id = id;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.mnuEditar:
                    Intent intent = new Intent(context, ModificarTema.class);
                    intent.putExtra("ID",id);
                    context.startActivity(intent);
                    return true;
                case R.id.mnuAgregarHecho:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Ingrese un nuevo hecho relevante");

                    // Set up the input
                    final EditText input = new EditText(context);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            actualizarHecho(id,input.getText().toString());
                            hecho_relevante_nuevo = input.getText().toString();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                    Toast.makeText(context, "Agregar hecho", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    private void actualizarHecho(final String id, final String hecho_nuevo) {
        bdTemas = FirebaseDatabase.getInstance();
        refTemas = bdTemas.getReference("temas");

        refTemas.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String hechoaux = dataSnapshot.getValue(Tema.class).getHecho_relevante_2();
                if(hechoaux.isEmpty()){
                    refTemas.child(id).child("hecho_relevante_2").setValue(hecho_nuevo);
                }else{
                    refTemas.child(id).child("hecho_relevante_1").setValue(hechoaux);
                    refTemas.child(id).child("hecho_relevante_2").setValue(hecho_nuevo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}