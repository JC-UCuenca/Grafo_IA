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

        Queue<Tupla> cola = new LinkedList<>();
        List<Tupla> visitados = new ArrayList<>();
        Tupla tuplaActual;
        Nodo nodoActual;
        Integer pesoActual;

        cola.add(new Tupla(inicio, 0));
        ArrayList<String[]> tabla = new ArrayList<>() {{
            add(new String[]{cola.peek().toString(), ""});
        }};

        while (!cola.isEmpty()) {
            tuplaActual = cola.stream().min(Tupla::compareTo).get();
            cola.remove(tuplaActual);
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

            List<Tupla> tempList = new ArrayList<>(cola);
            cola.clear();
            tempList.sort(Comparator.comparing(t -> t.getAcumulado()));
            cola.addAll(tempList);

            tabla.add(new String[]{String.join(", ", cola.stream()
                    .map(Tupla::toString).toList()), nodoActual.getNombre() + "("+pesoActual+")"});

            if (objetivos.isEmpty())
                cola.clear();

//            System.out.println("Cola: " + String.join(", ", cola.stream()
//                    .map(Tupla::toString).toList()) + "***Actual: " + tuplaActual.toString());
//            System.out.println("");
        }

        return tabla;
    }

}
