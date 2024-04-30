package model.heuristicas;

import model.grafo.Arista;
import model.grafo.Nodo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Gradiente {

    public ArrayList<String[]> buscar(Nodo inicio, List<Nodo> objetivos) {
        if (inicio == null)
            return null;

        if (objetivos == null || objetivos.isEmpty())
            objetivos = new ArrayList<>() {{
                add(null);
            }};

        PriorityQueue<Nodo> cola = new PriorityQueue<>(Comparator.comparingInt(x -> x.getPeso()));
        List<Nodo> visitados = new ArrayList<>();
        Nodo nodoActual;

        cola.add(inicio);
        ArrayList<String[]> tabla = new ArrayList<>() {{
            add(new String[]{"(" + inicio.getNombre() + ")", ""});
        }};

        while (!cola.isEmpty()) {
            nodoActual = cola.poll();
            cola.clear();
            visitados.add(nodoActual);

            objetivos.remove(nodoActual);

            List<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().map(Arista::getHijo).toList());

//                hijos.removeAll(cola);
            hijos.removeAll(visitados);

            cola.addAll(hijos);

            tabla.add(new String[]{String.join(", ", cola.stream()
                    .map(x -> x.getNombre() + "(" + x.getPeso() + ")").toList()), nodoActual.getNombre()});

            if (objetivos.isEmpty()) cola.clear();
        }

        return tabla;
    }
}
