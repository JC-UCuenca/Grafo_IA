package model.grafo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Persona {
    private String nombre;
    private Integer hijos;
    private String cedula;

    public Persona(String nombre, Integer hijos, String cedula) {
        this.nombre = nombre;
        this.hijos = hijos;
        this.cedula = cedula;
    }
}
