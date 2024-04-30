package model.heuristicas;

import model.grafo.Arista;
import model.grafo.Nodo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class PrimeroElMejor {

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
            add(new String[]{inicio.getNombre() +"(" + inicio.getPeso() + ")", ""});
        }};

        boolean encontrado = false;

        while (!cola.isEmpty()) {
            nodoActual = cola.poll();
            visitados.add(nodoActual);

            if (!objetivos.contains(nodoActual)) {
                Nodo aux = nodoActual;
                List<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                        .stream().filter(a -> a.getPadre().equals(aux))
                        .map(Arista::getHijo).toList());

//                hijos.removeAll(cola);
                hijos.removeAll(visitados);

                cola.addAll(hijos);
            } else {
                encontrado = true;
            }

            tabla.add(new String[]{String.join(", ", cola.stream()
                            .map(x -> x.getNombre() + "(" + x.getPeso() + ")").toList()), nodoActual.getNombre()});

            if (encontrado) break;
        }

        return tabla;
    }
}
