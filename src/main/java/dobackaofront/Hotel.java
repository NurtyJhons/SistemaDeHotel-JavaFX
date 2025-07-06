package dobackaofront;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Hotel {
    private ArrayList<Cabana> cabanas;

    public Hotel() {
        this.cabanas = new ArrayList<>();
    }

    public void adicionarCabana(Cabana cabana) {
        cabanas.add(cabana);
    }

    public void listarCabanas() {
        for (Cabana cabana : cabanas) {
            System.out.println(cabana);
        }
    }

    public void listarCabanasDisponiveis() {
        for (Cabana cabana : cabanas) {
            if (cabana.getHospedes().size() < cabana.getQuantidadeCamas()) {
                System.out.println("Disponível: " + cabana);
            }
        }
    }

    public Cabana buscarCabanaPorCodigo(int codigo) {
        for (Cabana cabana : cabanas) {
            if (cabana.getCodigo() == codigo) {
                return cabana;
            }
        }
        return null;
    }

    public ArrayList<Cabana> getCabanas() {
        return cabanas;
    }

    public void hospedarPessoa(Hospede hospede, int codigoCabana) {
        Cabana cabana = buscarCabanaPorCodigo(codigoCabana);
        if (cabana == null) {
            System.out.println("Cabana com código " + codigoCabana + " não encontrada.");
            return;
        }

        cabana.adicionarHospede(hospede);
    }
}