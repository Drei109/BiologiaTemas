package com.biologia.temas.biologiatemas;

/**
 * Created by Drei on 28/05/2017.
 */

public class Tema {

    private String titulo;
    private String descripcion;
    private String hecho_relevante_1;
    private String hecho_relevante_2;
    private String imagen;

    public Tema(String titulo, String descripcion, String hecho_relevante_1, String hecho_relevante_2, String imagen) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.hecho_relevante_1 = hecho_relevante_1;
        this.hecho_relevante_2 = hecho_relevante_2;
        this.imagen = imagen;
    }

    public Tema() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHecho_relevante_1() {
        return hecho_relevante_1;
    }

    public void setHecho_relevante_1(String hecho_relevante_1) {
        this.hecho_relevante_1 = hecho_relevante_1;
    }

    public String getHecho_relevante_2() {
        return hecho_relevante_2;
    }

    public void setHecho_relevante_2(String hecho_relevante_2) {
        this.hecho_relevante_2 = hecho_relevante_2;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
