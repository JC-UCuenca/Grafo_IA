package main;

import controller.CtrlPrincipal;
import view.VPrincipal;

public class MainWindow {
    public static void main(String[] args) {
        VPrincipal view = new VPrincipal();
        CtrlPrincipal ctrlPrincipal = new CtrlPrincipal(view);
        view.show();

    }
}
