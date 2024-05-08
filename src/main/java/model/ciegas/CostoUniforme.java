package model.ciegas;

import model.estructura.Tupla;
import model.grafo.Nodo;

import java.util.*;

public class CostoUniforme {

    public ArrayList<String[]> buscar(Nodo inicio, List<Nodo> objetivos) {
        if (inicio == null)
            return null;

        if (objetivos == null)
            objetivos = new ArrayList<>() {{
                add(null);
            }};

        PriorityQueue<Tupla> cola = new PriorityQueue<>();
        List<Tupla> visitados = new ArrayList<>();
        Tupla tuplaActual;
        Nodo nodoActual;
        Integer pesoActual;

        cola.add(new Tupla(inicio, 0));
        ArrayList<String[]> tabla = new ArrayList<>() {{
            add(new String[]{cola.peek().toString(), ""});
        }};

        while (!cola.isEmpty()) {
            tuplaActual = cola.poll();
            nodoActual = (Nodo) tuplaActual.getNodo();
            pesoActual = (Integer) tuplaActual.getAcumulado();
            visitados.add(tuplaActual);

            objetivos.remove(nodoActual);

            List<Tupla> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().map(x -> new Tupla(x.getHijo(), x.getPeso())).toList());

            hijos.removeAll(visitados);
            hijos.removeAll(cola);

            Integer finalPesoActual = pesoActual;
            cola.addAll(hijos.stream().map(x -> new Tupla(x.getNodo(), x.getAcumulado() + finalPesoActual)).toList());

            tabla.add(new String[]{String.join(", ", cola.stream()
                    .map(Tupla::toString).toList()), nodoActual.getNombre() + "("+pesoActual+")"});

            if (objetivos.isEmpty())
                cola.clear();
        }

        return tabla;
    }

}
