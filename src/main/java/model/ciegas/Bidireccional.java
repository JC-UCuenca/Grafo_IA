package model.ciegas;

import lombok.Getter;
import model.grafo.Arista;
import model.grafo.Nodo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bidireccional implements Runnable {

    private static final ArrayList<Nodo> nodosExistentes = new ArrayList<>();
    @Getter
    private static final ArrayList<String[]> tabla = new ArrayList<>();

    private final Nodo inicio, objetivo;
    private static boolean isThread0 = true;
    private static CountDownLatch latch = new CountDownLatch(1);

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
        List<String> colaHistorial = new ArrayList<>();

        colaHistorial.add(inicio.getNombre());
        extraccion.add(new Nodo("", 0));
        while (!cola.isEmpty()) {
            nodoActual = cola.poll();
            extraccion.add(nodoActual);

            addNodoExistente(nodoActual);

            List<Nodo> hijos = getHijosNoVisitados(nodoActual, cola, extraccion);

            if (existeInterseccion(hijos)) {
//                eliminarExistentes(hijos);
                cola.addAll(hijos);
                colaHistorial.add(cola.stream().map(Nodo::getNombre).collect(Collectors.joining(", ")));
                break;
            }

            cola.addAll(hijos);

            colaHistorial.add(cola.stream().map(Nodo::getNombre).collect(Collectors.joining(", ")));

            String thread = Thread.currentThread().getName();
//            System.out.println(thread + " Nodo actual: " + nodoActual.getNombre());
//            System.out.println(thread + " Cola: " + colaHistorial);
//            System.out.println(thread + " Extracción("+extraccion.size()+"): " + extraccion.stream().map(Nodo::getNombre).toList());
//            System.out.println(thread + " Nodos existentes: " + nodosExistentes.stream().map(Nodo::getNombre).toList());
//            System.out.println(thread + " Hijos: " + hijos.stream().map(Nodo::getNombre).toList());
//            System.out.println(thread + " Cola Historial("+colaHistorial.size()+"): " + colaHistorial);
//            System.out.println();

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


        }
        System.out.println(Thread.currentThread().getName() + " Fin de la búsqueda");
        for (String s : colaHistorial) {
            System.out.println(Thread.currentThread().getName() + " " + s);
        }
        actualizarTabla(colaHistorial, extraccion);
    }

    private void addNodoExistente(Nodo nodo) {
        synchronized (nodosExistentes) {
            if (!nodosExistentes.contains(nodo)) {
                nodosExistentes.add(nodo);
            }
        }
    }

    private List<Nodo> getHijosNoVisitados(Nodo nodo, Queue<Nodo> cola, ArrayList<Nodo> extraccion) {
        List<Nodo> hijos = new ArrayList<>(nodo.getAristas()
                .stream().filter(a -> a.getPadre().equals(nodo))
                .map(Arista::getHijo).toList());

        hijos.removeAll(cola);
        hijos.removeAll(extraccion);

        return hijos;
    }

    private boolean existeInterseccion(List<Nodo> hijos) {
        synchronized (nodosExistentes) {
            if (nodosExistentes.stream().noneMatch(hijos::contains)) {
                nodosExistentes.addAll(hijos);
            } else {
                return true;
            }
        }
        return false;
    }

    private void eliminarExistentes(List<Nodo> hijos) {
        synchronized (nodosExistentes) {
            hijos.removeAll(nodosExistentes);
        }
    }

    private void actualizarTabla(List<String> colaHistorial, ArrayList<Nodo> extraccion) {
        synchronized (Bidireccional.class) {
            if (isThread0) {
                actualizarTablaThread0(colaHistorial, extraccion);
                isThread0 = false;
                latch.countDown(); // Decrementa el contador del latch
            } else {
                try {
                    latch.await(); // Espera hasta que el contador del latch llegue a cero
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                actualizarTablaThread1(colaHistorial, extraccion);
                isThread0 = true;
                latch = new CountDownLatch(1);
            }
        }
        System.out.println(Thread.currentThread().getName() + " Finalizado");
        System.out.println(Thread.currentThread().getName() + " " + colaHistorial.size());
    }

    private void actualizarTablaThread0(List<String> colaHistorial, ArrayList<Nodo> extraccion) {
        tabla.clear();
        for (int i = 0; i < colaHistorial.size(); i++) {
            String[] row = new String[4];
            row[0] = (i == 0 ? "" : extraccion.get(i).getNombre());
            row[1] = colaHistorial.get(i);
            row[2] = "";
            row[3] = "";
            tabla.add(row);
        }
    }

    private void actualizarTablaThread1(List<String> colaHistorial, ArrayList<Nodo> extraccion) {
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < colaHistorial.size(); i++) {
            if (i >= tabla.size())
                break;
            nameBuilder.setLength(0);
            nameBuilder.append((i == 0) ? "" : extraccion.get(i).getNombre());
            tabla.get(i)[2] = nameBuilder.toString();
            tabla.get(i)[3] = colaHistorial.get(i);
        }
        System.out.println("Tabla actualizada");
        tabla.forEach(row -> System.out.println(Stream.of(row).collect(Collectors.joining(", "))));
        nodosExistentes.clear();
    }
}
