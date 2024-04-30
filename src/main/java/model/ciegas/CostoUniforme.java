package model.ciegas;

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

        Queue<Tupla<Nodo, Integer>> cola = new LinkedList<>();
        List<Tupla> visitados = new ArrayList<>();
        Tupla tuplaActual;
        Nodo nodoActual;
        Integer pesoActual;

        cola.add(new Tupla<>(inicio, 0));
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


            List<Tupla<Nodo, Integer>> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().map(x -> new Tupla<>(x.getHijo(), x.getPeso())).toList());

            hijos.removeAll(visitados);
            hijos.removeAll(cola);

            Integer finalPesoActual = pesoActual;
            cola.addAll(hijos.stream().map(x -> new Tupla<>(x.nodo, x.acumulado + finalPesoActual)).toList());

            List<Tupla<Nodo, Integer>> tempList = new ArrayList<>(cola);
            cola.clear();
            tempList.sort(Comparator.comparing(t -> t.acumulado));
            cola.addAll(tempList);

            tabla.add(new String[]{String.join(", ", cola.stream()
                    .map(Tupla::toString).toList()), nodoActual.getNombre()});

            if (objetivos.isEmpty())
                cola.clear();

//            System.out.println("Cola: " + String.join(", ", cola.stream()
//                    .map(Tupla::toString).toList()) + "***Actual: " + tuplaActual.toString());
//            System.out.println("");
        }

        return tabla;
    }

    private class Tupla<A, B> implements Comparable<Tupla> {
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

        @Override
        public int compareTo(Tupla otraTupla) {
            return Integer.compare((Integer) getAcumulado(), (Integer) otraTupla.getAcumulado());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tupla tupla = (Tupla) o;
            return getAcumulado() == tupla.getAcumulado() &&
                    Objects.equals(getNodo(), tupla.getNodo());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getNodo(), getAcumulado());
        }
    }

}
