package model.grafo;

import lombok.Getter;
import model.ciegas.*;
import model.heuristicas.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Grafo {
    private final HashMap<String, Nodo> nodos;

    public Grafo(){
        this.nodos = new LinkedHashMap<>();
    }

    public void cargarGrafo(String ruta){
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(",");

                if(!nodos.containsKey(datos[0])){
                    nodos.put(datos[0], new Nodo(datos[0], Integer.parseInt(datos[2])));
                }

                if(!nodos.containsKey(datos[1])){
                    nodos.put(datos[1], new Nodo(datos[1], Integer.parseInt(datos[3])));
                }

                nodos.get(datos[0]).addArista(nodos.get(datos[1]), Integer.parseInt(datos[4]));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private void validarMetas(String... metas){
        if (metas == null){
            metas = new String[]{""};
        }

    }

    public ArrayList<String[]> amplitud(String partida, String... metas){
        validarMetas(metas);

        Nodo origen = nodos.get(partida);
        ArrayList<Nodo> objetivos = new ArrayList<>(Arrays.stream(metas).map(nodos::get).toList());

        return new Amplitud().buscar(origen, objetivos);
    }

    public ArrayList<String[]> profundidad(String partida, String... metas){
        validarMetas(metas);

        Nodo origen = nodos.get(partida);
        ArrayList<Nodo> objetivos = new ArrayList<>(Arrays.stream(metas).map(nodos::get).toList());

        return new Profundidad().buscar(origen, objetivos);
    }

    public ArrayList<String[]> bidireccional(String partida, String meta){
        Nodo origen = nodos.get(partida);
        Nodo objetivo = nodos.get(meta);

        Bidireccional bidireccional1 = new Bidireccional(origen, objetivo);
        Bidireccional bidireccional2 = new Bidireccional(objetivo, origen);

        Thread hilo1 = new Thread(bidireccional1);
        Thread hilo2 = new Thread(bidireccional2);

        hilo1.setName("Hilo 1");
        hilo2.setName("Hilo 2");

        hilo1.start();
        hilo2.start();

        try {
            hilo1.join();
            hilo2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Bidireccional.getTabla();
    }

    public ArrayList<String[]> profundidadIterativa(String partida, String... metas){
        validarMetas(metas);

        Nodo origen = nodos.get(partida);
        ArrayList<Nodo> objetivos = new ArrayList<>(Arrays.stream(metas).map(nodos::get).toList());

        return new ArrayList<>(new ProfundidadIterativa().buscar(origen, objetivos));
    }

    public ArrayList<String[]> costoUniforme(String partida, String... metas){
        validarMetas(metas);

        Nodo origen = nodos.get(partida);
        ArrayList<Nodo> objetivos = new ArrayList<>(Arrays.stream(metas).map(nodos::get).toList());

        return new CostoUniforme().buscar(origen, objetivos);
    }

    public ArrayList<String[]> gradiente(String partida, String... metas){
        validarMetas(metas);

        Nodo origen = nodos.get(partida);
        ArrayList<Nodo> objetivos = new ArrayList<>(Arrays.stream(metas).map(nodos::get).toList());

        return new Gradiente().buscar(origen, objetivos);
    }

    public ArrayList<String[]> primeroElMejor(String partida, String... metas){
        validarMetas(metas);

        Nodo origen = nodos.get(partida);
        ArrayList<Nodo> objetivos = new ArrayList<>(Arrays.stream(metas).map(nodos::get).toList());

        return new PrimeroElMejor().buscar(origen, objetivos);
    }

    public ArrayList<String[]> AEstrella(String partida, String... metas){
        validarMetas(metas);

        Nodo origen = nodos.get(partida);
        ArrayList<Nodo> objetivos = new ArrayList<>(Arrays.stream(metas).map(nodos::get).toList());

        return new AEstrella().buscar(origen, objetivos);
    }

    public Integer obtenerNivelProfundidad(String inicio) {
        Nodo nodo = nodos.get(inicio);
        if (nodo == null) {
            return -1; // Nodo no encontrado
        }

        Map<Nodo, Integer> niveles = new HashMap<>();
        niveles.put(nodo, 0);

        Deque<Nodo> pila = new ArrayDeque<>();
        pila.push(nodo);

        int maxProfundidad = 0;

        while (!pila.isEmpty()) {
            Nodo actual = pila.pop();
            int nivelActual = niveles.get(actual);

            maxProfundidad = Math.max(maxProfundidad, nivelActual);

            for (Arista arista : actual.getAristas()) {
                Nodo hijo = arista.getHijo();
                if (!niveles.containsKey(hijo)) {
                    niveles.put(hijo, nivelActual + 1);
                    pila.push(hijo);
                }
            }
        }

        return maxProfundidad;
    }

    public Integer obtenerMaximaCantidadHijos(String inicio) {
        Nodo nodoInicial = nodos.get(inicio);
        if (nodoInicial == null) {
            return 0;
        }

        Set<Nodo> visitados = new HashSet<>();
        Deque<Nodo> pila = new ArrayDeque<>();
        pila.push(nodoInicial);
        int maxHijos = 0;

        while (!pila.isEmpty()) {
            Nodo actual = pila.pop();
            visitados.add(actual);
            int cantidadHijos = 0;

            for (Arista arista : actual.getAristas()) {
                Nodo hijo = arista.getHijo();
                // Solo se consideran los nodos que no han sido visitados previamente
                if (!visitados.contains(hijo)) {
                    cantidadHijos++;
                    visitados.add(hijo);
                    pila.push(hijo);
                }
            }
            // Actualizar la máxima cantidad de hijos encontrada
            if (cantidadHijos > maxHijos) {
                maxHijos = cantidadHijos;
            }
        }

        return maxHijos;
    }

    public Integer calcularNumeroAristas(String inicio) {
        Nodo nodoInicial = nodos.get(inicio);
        if (nodoInicial == null) {
            return 0;
        }

        int numAristas = 0;
        Set<Nodo> visitados = new HashSet<>();
        Deque<Nodo> cola = new ArrayDeque<>();
        cola.offer(nodoInicial);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            visitados.add(actual);

            for (Arista arista : actual.getAristas()) {
                Nodo hijo = arista.getHijo();
                // Solo se consideran los nodos que no han sido visitados previamente
                if (visitados.add(hijo)) {
                    cola.offer(hijo);
                    numAristas++;
                }
            }
        }

        return numAristas;
    }

    public Integer calcularNumeroNodos(String inico) {
        Nodo nodoInicial = nodos.get(inico);
        if (nodoInicial == null) {
            return 0;
        }

        int numNodos = 0;
        Set<Nodo> visitados = new HashSet<>();
        Deque<Nodo> cola = new ArrayDeque<>();
        cola.offer(nodoInicial);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            visitados.add(actual);
            numNodos++;

            for (Arista arista : actual.getAristas()) {
                Nodo hijo = arista.getHijo();
                // Solo se consideran los nodos que no han sido visitados previamente
                if (visitados.add(hijo)) {
                    cola.offer(hijo);
                }
            }
        }

        return numNodos;
    }

}
