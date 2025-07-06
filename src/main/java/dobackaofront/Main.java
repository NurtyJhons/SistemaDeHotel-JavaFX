package dobackaofront;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "hotel.json";

        // Carregar dados anteriores, se existirem
        Hotel hotel = Persistencia.carregarHotel(caminhoArquivo);

        ArrayList<TipoCama> camasBasica = new ArrayList<>();
        camasBasica.add(TipoCama.SOLTEIRO);
        camasBasica.add(TipoCama.SOLTEIRO);
        camasBasica.add(TipoCama.CASAL);

        ArrayList<TipoCama> camasLuxo = new ArrayList<>();
        camasLuxo.add(TipoCama.CASAL);

        /* Cabana cabana1 = new Cabana("Básica", 3, camasBasica);
        Cabana cabana2 = new Cabana("Luxo", 1, camasLuxo);
        hotel.adicionarCabana(cabana1);
        hotel.adicionarCabana(cabana2); */

        /* Hospede hospede1 = new Hospede("13544873701", "João Vitor", 20, "25/12/2024", "01/01/2025");
        Hospede hospede2 = new Hospede("03791908773", "Monica", 51, "26/12/2024", "02/01/2025");
        hotel.hospedarPessoa(hospede1, cabana2.getCodigo());
        hotel.hospedarPessoa(hospede2, cabana1.getCodigo()); */

        // Mostrar resultado
        HotelView.exibirCabanasDisponiveis(hotel.getCabanas());

        // Salvar novamente
        Persistencia.salvarHotel(hotel, caminhoArquivo);
    }
}