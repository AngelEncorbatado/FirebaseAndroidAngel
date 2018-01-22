package com.example.angelus.firebaseandroidangel;

import java.io.Serializable;

/**
 * Created by Angelus on 21/01/2018.
 */

public class Producto implements Serializable {

    private String nombre, descripcion, categoria, precio;

    public Producto(){

    }

    public Producto(String nombre, String descripcion, String categoria, String precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
