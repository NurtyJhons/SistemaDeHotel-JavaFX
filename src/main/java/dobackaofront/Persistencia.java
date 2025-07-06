package dobackaofront;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class Persistencia {

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // Deixa o JSON formatado bonitinho

    public static void salvarHotel(Hotel hotel, String caminhoArquivo) {
        try {
            mapper.writeValue(new File(caminhoArquivo), hotel);
            System.out.println("✅ Hotel salvo com sucesso no arquivo: " + caminhoArquivo);
        } catch (IOException e) {
            System.err.println("❌ Erro ao salvar o hotel: " + e.getMessage());
        }
    }

    public static Hotel carregarHotel(String caminhoArquivo) {
        try {
            return mapper.readValue(new File(caminhoArquivo), Hotel.class);
        } catch (IOException e) {
            System.err.println("⚠️ Erro ao carregar hotel, retornando hotel vazio: " + e.getMessage());
            return new Hotel();
        }
    }
}