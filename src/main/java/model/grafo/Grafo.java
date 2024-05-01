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

                for (int i = 0; i < datos.length; i++) {
                    if(datos[i].isEmpty() || datos[i].equals("0")){
                        datos[i] = "0";
                    }
                }

//                System.out.println(Arrays.toString(datos));
                String pesoNodo = "0";
                String pesoArista = "0";
                if(datos.length>=3) pesoNodo = datos[2];
                if(datos.length>=4) pesoArista = datos[3];
                if(!nodos.containsKey(datos[0])){
                    nodos.put(datos[0], new Nodo(datos[0], Integer.parseInt(pesoNodo)));

                } else if (nodos.get(datos[0]).getPeso() == 0) {
                    nodos.get(datos[0]).setPeso(Integer.parseInt(pesoNodo));
                }

                if(!nodos.containsKey(datos[1])){
                    nodos.put(datos[1], new Nodo(datos[1], 0));
                }


                if(datos.length==4){
                    pesoArista = datos[3];
                }
                nodos.get(datos[0]).addArista(nodos.get(datos[1]), Integer.parseInt(pesoArista));

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

        hilo1.start();
        hilo2.start();

        try {
            hilo1.join();
            hilo2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(Bidireccional.getTabla()
                .stream().map(lista -> lista.toArray(new String[0]))
                .collect(Collectors.toList()));
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

    public int obtenerNivelProfundidad(Nodo nodo) {
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

    public int obtenerMaximaCantidadHijos(Nodo nodoInicial) {
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
            maxHijos = Math.max(maxHijos, cantidadHijos);
        }

        return maxHijos;
    }

    public int calcularNumeroAristas(Nodo nodoInicial) {
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
                if (!visitados.contains(hijo)) {
                    cola.offer(hijo);
                    numAristas++;
                }
            }
        }

        return numAristas;
    }

    public int calcularNumeroNodos(Nodo nodoInicial) {
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
                if (!visitados.contains(hijo)) {
                    cola.offer(hijo);
                }
            }
        }

        return numNodos;
    }

}
