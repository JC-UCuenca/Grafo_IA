package model.heuristicas;

import model.grafo.Nodo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AEstrella {

    public ArrayList<String[]> buscar(Nodo inicio, List<Nodo> objetivos) {
        if (inicio==null)
            return null;

        if (objetivos==null)
            objetivos = new ArrayList<>() {{ add(null); }};

        Queue<Tupla<Nodo, Integer>> cola = new LinkedList<>();
        List<Nodo> visitados = new ArrayList<>();
        Tupla tuplaActual;
        Nodo nodoActual;

        cola.add(new Tupla<>(inicio, inicio.getPeso()));
        ArrayList<String[]> tabla = new ArrayList<>() {{
            add(new String[]{inicio.getNombre()+"(0)", ""});
        }};
        boolean encontrado = false;

        while(!cola.isEmpty()){
            tuplaActual = cola.stream().min((n1, n2) -> Integer.compare(n1.acumulado, n2.acumulado)).get();
            cola.remove(tuplaActual);
            nodoActual = (Nodo) tuplaActual.getNodo();
            visitados.add(nodoActual);

            if(!objetivos.contains(nodoActual)){
                Nodo aux = nodoActual;
                List<Tupla<Nodo, Integer>> hijos = new ArrayList<>(nodoActual.getAristas()
                        .stream().map(x -> new Tupla<>(x.getHijo(), x.getPeso() + x.getHijo().getPeso())).toList());

                hijos = hijos.stream().filter(x-> !visitados.contains(x.nodo)).toList();

                cola.addAll(hijos);

            }else{
                encontrado = true;
            }

            tabla.add(new String[]{String.join(", ",cola.stream().map(Tupla::toString).toList()),
                    tuplaActual.toString()});
            if(encontrado) break;
        }

        return tabla;
    }

    private class Tupla<A, B> {
        private final A nodo;
        private final B acumulado;

        public Tupla(A first, B second) {
            this.nodo = first;
            this.acumulado = second;
        }

        public A getNodo() {
            return nodo;
        }

        public B getAcumulado() {
            return acumulado;
        }

        @Override
        public String toString() {
            return nodo + "(" + acumulado + ")";
        }
    }
    
}
