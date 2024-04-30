package controller;

import model.figure.Linea;
import model.figure.Rectangulo;
import model.grafo.Arista;
import model.grafo.Nodo;
import view.VPrincipal;
import model.grafo.Grafo;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class CtrlPrincipal implements ActionListener {
    VPrincipal principal;
    Grafo grafo;

    public CtrlPrincipal(VPrincipal principal) {
        this.principal = principal;
        this.grafo = new Grafo();
        initComponents();
    }

    private void initComponents() {
        principal.getBtnArchivo().addActionListener(this);
        principal.getBtnEjecutar().addActionListener(this);
        principal.getBtnGraficar().addActionListener(this);
        principal.getTxtInicio().setText("arad");
        principal.getTxtFin().setText("bucharest");
        principal.getChkCostoUni().setSelected(true);
        //grafo.cargarGrafo(".\\CSV files\\ejemplo2.csv");

        principal.getPanelGrafo().setLayout(null);
        principal.getPanelTabla().setLayout(new BoxLayout(principal.getPanelTabla(), BoxLayout.Y_AXIS));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object component = e.getSource();

        if (component == principal.getBtnArchivo()) {
            String filePath = selectCSVFile();
            if (filePath != null) {
                grafo.getNodos().clear();
                grafo.cargarGrafo(filePath);
                System.out.println(filePath);
                principal.getLblArchivo().setText(
                        filePath.substring(filePath.lastIndexOf("\\") + 1)
                );
            } else {
                principal.getLblArchivo().setText("Archivo no seleccionado");
            }

        } else if (component == principal.getBtnEjecutar()) {
            if (!principal.getLblArchivo().getText().equals("Archivo no seleccionado"))
                search();

        } else if (component == principal.getBtnGraficar()) {
            if (principal.getLblArchivo().getText().equals("Archivo no seleccionado"))
                return;

            drawGraph();
            System.out.println(principal.getPanelGrafo().getHeight());
            System.out.println(principal.getPanelGrafo().getWidth());
            principal.getPanelGrafo().revalidate();
            principal.getPanelGrafo().repaint();
        }
    }


    private String selectCSVFile() {
        String currentPath = new File("").getAbsolutePath();

        // Crear un JFileChooser
        JFileChooser fileChooser = new JFileChooser(currentPath);

        // Filtrar para mostrar solo archivos CSV
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV", "csv");
        fileChooser.setFileFilter(filter);

        // Mostrar el diálogo de selección de archivo
        int result = fileChooser.showOpenDialog(null);

        // Verificar si se seleccionó un archivo
        if (result == JFileChooser.APPROVE_OPTION) {
            // Obtener el archivo seleccionado
            File selectedFile = fileChooser.getSelectedFile();

            // Verificar si es un archivo CSV
            if (selectedFile.getName().toLowerCase().endsWith(".csv")) {
                // Devolver la ruta absoluta del archivo
                return selectedFile.getAbsolutePath();
            } else {
                // Si no es un archivo CSV, mostrar un mensaje de error
                JOptionPane.showMessageDialog(null, "Selecciona un archivo CSV válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else {
            return null; // No se seleccionó ningún archivo
        }
    }

    private String[] searchMethods() {
        StringBuilder methods = new StringBuilder();
        JCheckBox chk;

        for (Component c : principal.getJPanel2().getComponents()) {
            chk = (JCheckBox) c;
            if (chk.isSelected())
                methods.append(chk.getText()).append(",");
        }

        return methods.toString().split(",");
    }

    private void search() {
        String inicio = principal.getTxtInicio().getText().trim();
        String[] metas = principal.getTxtFin().getText().split(",");
        ArrayList<String[]> tableData = null;

        principal.getPanelTabla().removeAll();

        String[][] methodsTime = new String[searchMethods().length][2];
        int index = 0;
        int width = principal.getPanelTabla().getWidth() - 30;

        for (String method : searchMethods()) {
            if (method.isEmpty()) continue;

            long tiempoInicial = System.nanoTime();

            tableData = switch (method) {
                case "Amplitud" -> grafo.amplitud(inicio, metas);
                case "Profundidad" -> grafo.profundidad(inicio, metas);
                case "Bidireccional" -> grafo.bidireccional(inicio, metas[0]);
                case "Profundidad Iterativa" -> grafo.profundidadIterativa(inicio, metas);
                case "Costo Uniforme" -> grafo.costoUniforme(inicio, metas);
                case "Gradiente" -> grafo.gradiente(inicio, metas);
                case "Primero el Mejor" -> grafo.primeroElMejor(inicio, metas);
                case "A*" -> grafo.AEstrella(inicio, metas);
                default -> tableData;
            };

            double tiempo = (System.nanoTime() - tiempoInicial) / 1e9;

            System.out.println(method + ": " + tiempo);
            principal.getPanelTabla().add(new JLabel(method));
//            principal.getPanelTabla().add(new JLabel("Tiempo: " + tiempo));
            methodsTime[index][0] = method;
            methodsTime[index][1] = Double.toString(tiempo);
            index++;
            if (tableData != null) {
                JScrollPane scrollPane = new JScrollPane();
                JTable table = createTable(tableData);
                int height = (int) (table.getPreferredSize().getHeight() +
                        table.getTableHeader().getPreferredSize().getHeight());

                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setPreferredSize(new Dimension(width, height));
                scrollPane.setViewportView(table);

                principal.getPanelTabla().add(scrollPane);
            }
        }

        JTable table = createTable(methodsTime, "Método", "Tiempo (s)");
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        int height = (int) (table.getPreferredSize().getHeight() +
                table.getTableHeader().getPreferredSize().getHeight());
        scrollPane.setPreferredSize(new Dimension(width, height));
        scrollPane.setViewportView(table);

        principal.getPanelTabla().add(scrollPane);
        principal.getSPanelTabla().revalidate();
        principal.getSPanelTabla().repaint();
    }

    private JTable createTable(ArrayList<String[]> data) {
        String[] headers = switch (data.getFirst().length) {
            case 3 -> new String[]{"Nivel", "Cola", "Extracción"};
            case 4 -> new String[]{"Extracción", "Cola", "Extracción", "Cola"};
            default -> new String[]{"Cola", "Extracción"};
        };

        DefaultTableModel model = new DefaultTableModel(data.toArray(new String[0][]), headers);
        return new JTable(model);
    }

    private JTable createTable(String[][] data, String... headers) {
        DefaultTableModel model = new DefaultTableModel(data, headers);
        return new JTable(model);
    }

    private void drawGraph() {
        principal.getPanelGrafo().removeAll();

        int size = 10;
        int level = 1;
        HashMap<Nodo, Integer[]> created = new HashMap<>();

        int dx = 50;
        int x = level * dx;
        int y = principal.getSPanelGrafo().getHeight() / 2;

        Queue<Nodo> cola = new LinkedList<>();
        Nodo nodoActual;
        cola.add((Nodo) grafo.getNodos().values().toArray()[0]);
        int levelNodes = 0;
        int tempY = 0;

        int children = -1;
        while (!cola.isEmpty()) {
            System.out.println(Arrays.toString(cola.toArray()));
            nodoActual = cola.poll();
            created.put(nodoActual, new Integer[]{x, y});

            List<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().map(Arista::getHijo).toList());

            hijos.removeAll(cola);
            hijos.removeAll(created.keySet());
            cola.addAll(hijos);

            principal.getPanelGrafo().add(new Rectangulo(x, y, size, size));
            System.out.println("Nodo: " + nodoActual.getNombre() + " Coordenadas: " + x + ", " + y);


            JLabel label = new JLabel(nodoActual.getNombre());
            label.setBounds(x, y - size * 2, 50, size * 2);
            principal.getPanelGrafo().add(label);

            if (!created.keySet().isEmpty()) {
                Nodo actualAux = nodoActual;
                ArrayList<Nodo> padres = new ArrayList<>(created.keySet().stream()
                        .filter(c -> c.getAristas().stream().map(Arista::getHijo).toList()
                                .contains(actualAux)).toList());
                int finalX = x;
                int finalY = y;
                padres.forEach(p -> {
                    Integer[] inicio = created.get(p);
                    Integer[] fin = new Integer[]{finalX, finalY};
                    principal.getPanelGrafo().add(new Linea(inicio, fin));
                });
                children--;
            }

            if (children == 0 && !cola.isEmpty()) {
                Nodo father = created.keySet().stream().filter(
                        n -> n.getAristas().stream().map(Arista::getHijo).toList().contains(cola.peek())).toList().getFirst();
                children = father.getAristas().size();
            }

            if (levelNodes <= 1 && !cola.isEmpty()) {
                levelNodes = cola.size();
                tempY = principal.getSPanelGrafo().getHeight() / (2 * levelNodes);
                y = tempY;
                level++;
                x = level * dx;

                Nodo father = created.keySet().stream().filter(
                        n -> n.getAristas().stream().map(Arista::getHijo).toList().contains(cola.peek())).toList().getFirst();
                children = father.getAristas().size();

            } else {
                levelNodes--;
                y += tempY * 2;

            }

        }
        System.out.println(level);
        principal.getPanelGrafo().setPreferredSize(new Dimension(x + dx, 50));

        principal.getSPanelGrafo().revalidate();
        principal.getSPanelGrafo().repaint();
    }

}
