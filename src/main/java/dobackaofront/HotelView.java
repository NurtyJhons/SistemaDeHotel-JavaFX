package dobackaofront;

import java.util.List;

public class HotelView {

    public static void exibirCabana(Cabana cabana) {
        System.out.println("Cabana:");
        System.out.println("- Código: " + cabana.getCodigo());
        System.out.println("- Nome: " + cabana.getNome());
        System.out.println("- Quantidade de camas: " + cabana.getQuantidadeCamas());
        System.out.println("- Tipos de camas: " + cabana.getTipoDasCamas());
        System.out.println("- Hóspedes:");
        for (Hospede h : cabana.getHospedes()) {
            exibirHospede(h);
        }
        System.out.println();
    }

    public static void exibirHospede(Hospede hospede) {
        System.out.println("  - Nome: " + hospede.getNomeCompleto());
        System.out.println("    CPF: " + hospede.getCpf());
        System.out.println("    Idade: " + hospede.getIdade());
        System.out.println("    Data de Chegada: " + hospede.getDataChegada());
        System.out.println("    Data de Saída: " + hospede.getDataSaida());
        System.out.println("    Dias Hospedado: " + hospede.calcularDiasHospedado());
    }

    public static void exibirCabanasDisponiveis(List<Cabana> cabanas) {
        System.out.println("\nCabanas disponíveis:");
        for (Cabana cabana : cabanas) {
            if (cabana.getHospedes().size() < cabana.getQuantidadeCamas()) {
                exibirCabana(cabana);
            }
        }
    }
}