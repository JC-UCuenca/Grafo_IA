package model.figure;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Rectangulo extends JPanel {

    private int x, y, width, height;

    public Rectangulo(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setBounds(x, y, width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}
