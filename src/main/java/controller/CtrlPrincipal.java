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
    private final VPrincipal view;
    private final Grafo grafo;
    private boolean fileLoad;


    public CtrlPrincipal(VPrincipal view) {
        this.view = view;
        this.grafo = new Grafo();
        this.fileLoad = false;
        initComponents();
    }

    private void initComponents() {
        view.getBtnArchivo().addActionListener(this);
        view.getBtnEjecutar().addActionListener(this);
        view.getBtnGraficar().addActionListener(this);
        view.getTxtInicio().setText("H");
        view.getTxtFin().setText("L");
        view.getChkCostoUni().setSelected(true);
        grafo.cargarGrafo(".\\CSV_files\\ciudades.csv");
        this.fileLoad = true;

        JCheckBox chk;
        for(Component c: view.getPanelMetodos().getComponents()){
            chk = (JCheckBox) c;
            chk.setSelected(false);
        }
        view.getChkBidireccional().setSelected(true);

        view.getChkBidireccional().setSelected(true);

        view.getPanelGrafo().setLayout(null);
        view.getPanelTabla().setLayout(new BoxLayout(view.getPanelTabla(), BoxLayout.Y_AXIS));

        //principal.setExtendedState(principal.MAXIMIZED_BOTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object component = e.getSource();

        if (component == view.getBtnArchivo()) {
            String filePath = selectCSVFile();
            if (filePath != null) {
                grafo.getNodos().clear();
                grafo.cargarGrafo(filePath);
//                System.out.println(filePath);
                view.getLblArchivo().setText(
                        filePath.substring(filePath.lastIndexOf("\\") + 1)
                );

                this.fileLoad = true;

            } else {
                view.getLblArchivo().setText("Archivo no seleccionado");
            }

        } else if (component == view.getBtnEjecutar()) {
            if (this.fileLoad){
//                System.out.println("Profundiad: " + grafo.obtenerNivelProfundidad(grafo.getNodos().get(view.getTxtInicio().getText())));
//                System.out.println("Maxima cantidad de hijos: " + grafo.obtenerMaximaCantidadHijos(grafo.getNodos().get(view.getTxtInicio().getText())));
//                System.out.println("Numero de aristas: " + grafo.calcularNumeroAristas(grafo.getNodos().get(view.getTxtInicio().getText())));
//                System.out.println("Numero de nodos: " + grafo.calcularNumeroNodos(grafo.getNodos().get(view.getTxtInicio().getText())));
//                System.out.println("------------------------------------");
                search();
            }

        } else if (component == view.getBtnGraficar()) {
            if (!this.fileLoad)
                return;

            drawGraph();
            view.getPanelGrafo().revalidate();
            view.getPanelGrafo().repaint();
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

        for (Component c : view.getPanelMetodos().getComponents()) {
            chk = (JCheckBox) c;
            if (chk.isSelected())
                methods.append(chk.getText()).append(",");
        }

        return methods.toString().split(",");
    }

    private void search() {
        String inicio = view.getTxtInicio().getText().trim();
        String[] metas = view.getTxtFin().getText().split(",");
        ArrayList<String[]> tableData;

        view.getPanelTabla().removeAll();

        String[][] methodsFeatures = new String[searchMethods().length][4];
        int index = 0;
        int width = view.getPanelTabla().getWidth() - 30;

        HashMap<String, Integer> parameters = calculateComplexityParameters(inicio);

        for (String method : searchMethods()) {
//            System.out.println("Método: " + method + "\n");
            if (method.isEmpty()) continue;

            long startTime = System.nanoTime();
            tableData = executeSearchMethod(method, inicio, metas);
            long endTime = System.nanoTime();


            double durationInSeconds = (endTime - startTime) / 1e9;
            String time = String.format("%.5f", durationInSeconds);

            view.getPanelTabla().add(new JLabel(method));

            methodsFeatures[index] = calculateComplexity(method, parameters, tableData);
            methodsFeatures[index][0] = method;
            methodsFeatures[index][1] = time;
            index++;

            if (tableData != null) {
                addTableToPanel(tableData, width);
            }
        }

        addTableToPanel(methodsFeatures, width, "Método", "Tiempo (s)", "O Temporal", "O Espacial");
        view.getSPanelTabla().revalidate();
        view.getSPanelTabla().repaint();
    }

    private ArrayList<String[]> executeSearchMethod(String method, String inicio, String[] metas) {
        return switch (method) {
            case "Amplitud" -> grafo.amplitud(inicio, metas);
            case "Profundidad" -> grafo.profundidad(inicio, metas);
            case "Bidireccional" -> grafo.bidireccional(inicio, metas[0]);
            case "Profundidad Iterativa" -> grafo.profundidadIterativa(inicio, metas);
            case "Costo Uniforme" -> grafo.costoUniforme(inicio, metas);
            case "Gradiente" -> grafo.gradiente(inicio, metas);
            case "Primero el Mejor" -> grafo.primeroElMejor(inicio, metas);
            case "A*" -> grafo.AEstrella(inicio, metas);
            default -> null;
        };

    }

    private HashMap<String, Integer> calculateComplexityParameters(String inicio) {
        HashMap<String, Integer> parameters = new HashMap<>();

        parameters.put("d", grafo.obtenerNivelProfundidad(inicio));
        parameters.put("b", grafo.obtenerMaximaCantidadHijos(inicio));
        parameters.put("E", grafo.calcularNumeroAristas(inicio));
        parameters.put("V", grafo.calcularNumeroNodos(inicio));

        return parameters;
    }

    private String[] calculateComplexity(String method, HashMap<String, Integer> parameters, ArrayList<String[]> tableData) {
    String[] row = new String[4];

    switch (method){
        case "Amplitud":
            row[2] = String.format("%e", Math.pow(parameters.get("b"), parameters.get("d") + 1));
            row[3] = row[2];
            break;
        case "Profundidad":
            row[2] = String.format("%e", Math.pow(parameters.get("b"), parameters.get("d")));
            row[3] = String.format("%e", parameters.get("b").doubleValue() * parameters.get("d"));
            break;
        case "Bidireccional":
            row[2] = String.format("%e", Math.pow(parameters.get("b"), parameters.get("d") / 2));
            row[3] = row[2];
            break;
        case "Profundidad Iterativa":
            row[2] = String.format("%e", Math.pow(parameters.get("b"), parameters.get("d")));
            row[3] = String.format("%e", parameters.get("b").doubleValue() * parameters.get("d"));
            break;
        case "Costo Uniforme":
            String firstTuple = tableData.get(1)[1];
            String lastTuple = tableData.getLast()[1];

            int dC = Integer.parseInt(lastTuple.substring(lastTuple.indexOf("(") + 1, lastTuple.length()-1));
            int epsilon = Integer.parseInt(firstTuple.substring(firstTuple.indexOf("(") + 1, firstTuple.length()-1));
            epsilon = (epsilon!=0) ? epsilon : 1;

            row[2] = String.format("%e", Math.pow(parameters.get("b"), Math.ceil(dC/epsilon)));
            row[3] = row[2];

            break;
        case "Gradiente":
            row[2] = String.format("%e", Math.pow(parameters.get("b"), parameters.get("d")));
            row[3] = "1";
            break;
        case "Primero el Mejor":
            row[2] = String.format("%e", parameters.get("E") * Math.log(parameters.get("V")));
            row[3] = String.format("%e", parameters.get("V").doubleValue() + parameters.get("E"));
            break;
        case "A*":
            row[2] = String.format("%e", parameters.get("E") * Math.log(parameters.get("V")));
            row[3] = String.format("%e", parameters.get("V").doubleValue() + parameters.get("E"));
            break;
        default:
            row[2] = "";
            row[3] = "";
            break;
    }

    return row;
}

    private void addTableToPanel(ArrayList<String[]> data, int width) {
//        System.out.println("Data: ");
//        for (String[] row : data) {
//            System.out.println(Arrays.toString(row));
//        }

        JScrollPane scrollPane = new JScrollPane();
        JTable table = createTable(data);
        int height = (int) (table.getPreferredSize().getHeight() +
                table.getTableHeader().getPreferredSize().getHeight());

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(width, height));
        scrollPane.setViewportView(table);

        view.getPanelTabla().add(scrollPane);
    }

    private void addTableToPanel(String[][] data, int width, String... headers) {
        JScrollPane scrollPane = new JScrollPane();
        JTable table = createTable(data, headers);
        int height = (int) (table.getPreferredSize().getHeight() +
                table.getTableHeader().getPreferredSize().getHeight());

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(width, height));
        scrollPane.setViewportView(table);

        view.getPanelTabla().add(scrollPane);
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
        view.getPanelGrafo().removeAll();

        int size = 10;
        int level = 1;
        HashMap<Nodo, Integer[]> created = new HashMap<>();

        int dx = 50;
        int x = level * dx;
        int y = view.getSPanelGrafo().getHeight() / 2;

        Queue<Nodo> cola = new LinkedList<>();
        Nodo nodoActual;
        cola.add((Nodo) grafo.getNodos().values().toArray()[0]);
        int levelNodes = 0;
        int tempY = 0;

        int children = -1;
        while (!cola.isEmpty()) {
            nodoActual = cola.poll();
            created.put(nodoActual, new Integer[]{x, y});

            List<Nodo> hijos = new ArrayList<>(nodoActual.getAristas()
                    .stream().map(Arista::getHijo).toList());

            hijos.removeAll(cola);
            hijos.removeAll(created.keySet());
            cola.addAll(hijos);

            view.getPanelGrafo().add(new Rectangulo(x, y, size, size));
//            System.out.println("Nodo: " + nodoActual.getNombre() + " Coordenadas: " + x + ", " + y);


            JLabel label = new JLabel(nodoActual.getNombre());
            label.setBounds(x, y - size * 2, 50, size * 2);
            view.getPanelGrafo().add(label);

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
                    view.getPanelGrafo().add(new Linea(inicio, fin,
                            view.getPanelGrafo().getWidth(), view.getPanelGrafo().getHeight()));
//                    System.out.println("Linea: " + Arrays.toString(inicio) +" " + Arrays.toString(fin));
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
                tempY = view.getSPanelGrafo().getHeight() / (2 * levelNodes);
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
        view.getPanelGrafo().setPreferredSize(new Dimension(x + dx, 50));

        view.getSPanelGrafo().revalidate();
        view.getSPanelGrafo().repaint();
    }

}
