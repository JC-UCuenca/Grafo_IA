package model.ciegas;

import model.grafo.Arista;
import model.grafo.Nodo;

import java.util.*;

public class Profundidad {

    public ArrayList<String[]> buscar(Nodo inicio, List<Nodo> objetivos) {
        if (inicio == null)
            return null;

        if (objetivos == null || objetivos.isEmpty())
            objetivos = new ArrayList<>() {{ add(null); }};

        LinkedList<Nodo> pila = new LinkedList<>();
        ArrayList<Nodo> visitados = new ArrayList<>();
        Nodo nodoActual;

        pila.add(inicio);
        ArrayList<String[]> tabla = new ArrayList<>() {{
            add(new String[]{inicio.getNombre(), ""});
        }};

        while (!pila.isEmpty()) {
            nodoActual = pila.poll();
            visitados.add(nodoActual);

            objetivos.remove(nodoActual);

            List<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().map(Arista::getHijo).toList());

            hijos.removeAll(pila);
            hijos.removeAll(visitados);

            Collections.sort(hijos, Comparator.comparing(Nodo::getNombre));

            pila.addAll(0, hijos);

            tabla.add(new String[]{String.join(", ", pila.stream()
                    .map(Nodo::getNombre).toList()), nodoActual.getNombre()});

            if (objetivos.isEmpty())
                pila.clear();
        }

        return tabla;
    }
}
