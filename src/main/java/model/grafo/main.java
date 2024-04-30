package model.grafo;

public class main {
    public static void main(String[] args) {
        Persona p1 = new Persona("Juan", 2, "0123456789");
        p1.getCedula();
        p1.setCedula("03");
        System.out.println(p1.getCedula());
    }
}
