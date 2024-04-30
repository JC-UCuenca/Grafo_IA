package model.ciegas;

import model.grafo.Arista;
import model.grafo.Nodo;

import java.util.*;

public class ProfundidadIterativa {

    public ArrayList<String[]> buscar(Nodo inicio, List<Nodo> objetivos) {
        if (inicio == null)
            return null;

        if (objetivos == null || objetivos.isEmpty())
            objetivos = new ArrayList<>() {{
                add(null);
            }};

        LinkedList<Nodo> pila = new LinkedList<>();
        ArrayList<Nodo> extraccion = new ArrayList<>() {{
            add(null);
        }};
        Integer nivel = 0;

        Nodo nodoActual;
        boolean completo = false;
        pila.add(inicio);

        ArrayList<String[]> tabla = new ArrayList<>() {{
            add(new String[]{"0", inicio.getNombre(), ""});
        }};

        int nodosVisitados = 0;
        int nodosTotales = 0;
        Set<Nodo> extraccionNivel = new HashSet();

        while (!objetivos.isEmpty() && !completo) {
            nodoActual = pila.poll();
            nodosVisitados++;
            extraccionNivel.add(nodoActual);

            if (extraccion.contains(nodoActual)) {
                ArrayList<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                        .stream().map(Arista::getHijo).toList());
                hijos.removeAll(pila);
                hijos.removeAll(extraccionNivel);

                pila.addAll(0, hijos);
            }

            tabla.add(new String[]{nivel.toString(), String.join(", ", pila.stream()
                    .map(Nodo::getNombre).toList()), nodoActual.getNombre()});

            extraccion.add(nodoActual);
            objetivos.remove(nodoActual);

            if (pila.isEmpty()) {
                nivel++;
                extraccionNivel.clear();
                pila.add(inicio);

                tabla.add(new String[]{nivel.toString(), inicio.getNombre(), ""});
                if (nodosTotales != nodosVisitados) {
                    nodosTotales = nodosVisitados;
                    nodosVisitados = 0;
                } else completo = true;
            }
        }

        return tabla;
    }

}
