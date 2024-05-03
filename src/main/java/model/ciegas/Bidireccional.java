package model.ciegas;

import lombok.Getter;
import model.grafo.Arista;
import model.grafo.Nodo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Bidireccional implements Runnable {

    private static final ArrayList<Nodo> nodosExistentes = new ArrayList<>();
    @Getter
    private static final ArrayList<ArrayList<String>> tabla = new ArrayList<>();

    private final Nodo inicio, objetivo;

    public Bidireccional(Nodo inicio, Nodo objetivo) {
        this.inicio = inicio;
        this.objetivo = objetivo;
    }

    @Override
    public void run() {
        if (inicio == null || objetivo == null)
            return;

        Queue<Nodo> cola = new LinkedList<>();
        ArrayList<Nodo> extraccion = new ArrayList<>();
        Nodo nodoActual;

        cola.add(inicio);
        extraccion.add(null);
        List<String> colaHistorial = new ArrayList<>(cola.stream().map(Nodo::getNombre).toList());

        while (!cola.isEmpty()) {
            nodoActual = cola.poll();
            extraccion.add(nodoActual);

            synchronized (Bidireccional.class) {
                if (!nodosExistentes.contains(nodoActual)) {
                    nodosExistentes.add(nodoActual);
                }
            }

            Nodo aux = nodoActual;
            List<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().filter(a -> a.getPadre().equals(aux))
                    .map(Arista::getHijo).toList());

            hijos.removeAll(cola);
            hijos.removeAll(extraccion);

            cola.addAll(hijos);

            colaHistorial.add(String.join(", ", cola.stream().map(Nodo::getNombre).toList()));

            synchronized (Bidireccional.class) {
//                System.out.println(
//                        Thread.currentThread().getName() + "\n" +
//                                "Nodo actual: " + nodoActual + "\n" +
//                                "Hijos: " + hijos + "\n" +
//                                "Cola: " + cola + "\n" +
//                                "Existentes: " + nodosExistentes + "\n" +
//                                "Coincidencia: " + nodosExistentes.stream().anyMatch(hijos::contains) + "\n"
//                );

                if (nodosExistentes.stream().noneMatch(hijos::contains)) {
                    nodosExistentes.addAll(hijos);
                } else {
                    System.out.println(
                            Thread.currentThread().getName() + " BREAK"
                    );
                    break;
                }
            }
        }

        synchronized (Bidireccional.class) {
            if (Thread.currentThread().getName().equals("Thread-0")) {
                ArrayList<String> row = new ArrayList<>(4);
                for (int i = 0; i < colaHistorial.size(); i++) {
                    row.clear();
                    row.add((i == 0 ? "" : extraccion.get(i).getNombre()));
                    row.add(colaHistorial.get(i));
                    row.add("");
                    row.add("");
                    tabla.add(new ArrayList<>(row));
                }
                Bidireccional.class.notify();
            }
        }

        if (Thread.currentThread().getName().equals("Thread-1")) {
            synchronized (Bidireccional.class) {
                try {
                    Bidireccional.class.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 0; i < colaHistorial.size(); i++) {
                    if (i >= tabla.size())
                        break;
                    nameBuilder.setLength(0);
                    nameBuilder.append((i == 0) ? "" : extraccion.get(i).getNombre());
                    tabla.get(i).set(2, nameBuilder.toString());
                    tabla.get(i).set(3, colaHistorial.get(i));
                }
            }
        }
    }
}
