package dobackaofront;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;

@JsonIgnoreProperties(ignoreUnknown = true)

// Um hospede fica hospedado em algum lugar, seja apartamento, um quarto...Logo,
// devemos criar um sistema que hospede as pessoas em cabanas. Cada cabam vai ter um hospede,
// ou um conjunto de hospedes.

// A primeira coisa que precisamos saber na orientação a objetos é vincular classes.
// Nesse caso, ou seja, o que uma classe tá interagindo com a outra, o que uma classe tá
// trocando informação com a outra.
// Logo, cada hospede vai precisar fazer parte de uma cabana.

public class Hospede {
    private String cpf;
    private String nomeCompleto;
    private int idade;

    @JsonBackReference
    private Cabana cabana;
    private String dataChegada;
    private String dataSaida;

    // Serve para quando eu criar um objeto, eu saiba qual o cpf dele, nomeCompleto, idade, e cabana que
    // ele vai estar armazenado.

    public Hospede() {
        // Jackson precisa disso para reconstruir objetos
    }

    public Hospede(String cpf, String nomeCompleto, int idade, String dataChegada, String dataSaida) {
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.idade = idade;
        this.cabana = null;
        this.dataChegada = dataChegada;
        this.dataSaida = dataSaida;
    }

    // Pra manipular as informações do hospede:

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Cabana getCabana() {
        return cabana;
    }

    public void setCabana(Cabana cabana) {
        this.cabana = cabana;
    }

    public String getDataChegada() {
        return dataChegada;
    }

    public void setDataChegada(String dataChegada) {
        this.dataChegada = dataChegada;
    }

    public String getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public int calcularDiasHospedado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate chegada = LocalDate.parse(dataChegada, formatter);
        LocalDate saida = LocalDate.parse(dataSaida, formatter);
        return Period.between(chegada, saida).getDays();
    }

    // Para apresentar os dados do jeito que eu precisar, mostrando os dados do hospede:
    @Override
    public String toString() {
        return nomeCompleto + " (" + cpf + ")";
    }
}