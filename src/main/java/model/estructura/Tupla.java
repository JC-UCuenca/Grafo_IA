package model.estructura;

import lombok.Getter;
import model.grafo.Nodo;

import java.util.Objects;

@Getter
public class Tupla implements Comparable<Tupla> {
    private final Nodo nodo;
    private final Integer acumulado;

    public Tupla(Nodo nodo, Integer acumulado) {
        this.nodo = nodo;
        this.acumulado = acumulado;
    }


    @Override
    public String toString() {
        return nodo + "(" + acumulado + ")";
    }

    @Override
    public int compareTo(Tupla otraTupla) {
        return acumulado.compareTo(otraTupla.acumulado);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tupla tupla = (Tupla) o;
        return acumulado.equals(tupla.getAcumulado()) &&
                nodo.equals(tupla.getNodo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodo(), getAcumulado());
    }
}
