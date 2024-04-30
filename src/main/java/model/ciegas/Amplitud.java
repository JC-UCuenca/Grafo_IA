package model.ciegas;

import model.grafo.Arista;
import model.grafo.Nodo;

import java.util.*;

public class Amplitud {

    public ArrayList<String[]> buscar(Nodo inicio, List<Nodo> objetivos) {
        if (inicio == null)
            return null;

        if (objetivos == null || objetivos.isEmpty())
            objetivos = new ArrayList<>() {{ add(null); }};

        Queue<Nodo> cola = new LinkedList<>();
        ArrayList<Nodo> visitados = new ArrayList<>();
        Nodo nodoActual;

        cola.add(inicio);
        ArrayList<String[]> tabla = new ArrayList<>() {{
            add(new String[]{inicio.getNombre(), ""});
        }};

        while (!cola.isEmpty()) {
            nodoActual = cola.poll();
            visitados.add(nodoActual);

            objetivos.remove(nodoActual);

            List<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().map(Arista::getHijo).toList());

            hijos.removeAll(cola);
            hijos.removeAll(visitados);

            Collections.sort(hijos, Comparator.comparing(Nodo::getNombre));

            cola.addAll(hijos);

            tabla.add(new String[]{String.join(", ", cola.stream()
                    .map(Nodo::getNombre).toList()), nodoActual.getNombre()});

            if (objetivos.isEmpty())
                cola.clear();
        }

        return tabla;
    }
}
