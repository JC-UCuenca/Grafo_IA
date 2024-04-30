package model.grafo;

import java.util.ArrayList;

public class Nodo {
    private final String nombre;
    private Integer peso;
    private final ArrayList<Arista> aristas;

    public Nodo(String nombre, Integer peso) {
        this.nombre = nombre;
        this.peso = peso;
        this.aristas = new ArrayList<>();
    }

    public void addArista(Nodo nodoHijo, Integer peso) {
        aristas.add(new Arista(this, nodoHijo, peso));
    }

    public void setPeso(Integer peso){
        this.peso = peso;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getPeso() {
        return peso;
    }

    public ArrayList<Arista> getAristas() {
        return aristas;
    }

    @Override
    public String toString() {
        return  nombre;
    }
}
