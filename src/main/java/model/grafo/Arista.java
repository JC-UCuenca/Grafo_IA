package model.grafo;

public class Arista {

    private Nodo padre;
    private Nodo hijo;
    private Integer peso;

    public Arista(Nodo padre, Nodo hijo, Integer peso) {
        this.padre = padre;
        this.hijo = hijo;
        this.peso = peso;
    }

    public Nodo getPadre() {
        return padre;
    }

    public Nodo getHijo() {
        return hijo;
    }

    public Integer getPeso() {
        return peso;
    }
}
