package model.figure;

import javax.swing.*;
import java.awt.*;

public class Linea extends JComponent {
    private final int x1, y1, x2, y2;

    public Linea(Integer[] inicio, Integer[] fin) {
        this.x1 = inicio[0];
        this.y1 = inicio[1];
        this.x2 = fin[0];
        this.y2 = fin[1];
        setBounds(0, 0, 436, 487);
    }

    public void paint(Graphics g) {

        g.drawLine(x1, y1, x2, y2);
    }
}
