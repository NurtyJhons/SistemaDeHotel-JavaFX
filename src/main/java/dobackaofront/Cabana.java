package dobackaofront;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@JsonIgnoreProperties(ignoreUnknown = true)

// Essa é a orientação a objetos. Estamos definindo os atributos da classe Cabana, pra então começar
// a criar os construtores.

public class Cabana {
    private static int proximoCodigo = 1;
    private int codigo;
    private String nome;
    private int quantidadeCamas;
    private ArrayList<TipoCama> tipoDasCamas;

    @JsonManagedReference
    private ArrayList<Hospede> hospedes;

    // O hospede não pode ser hospedado em uma cabana sem essa cabana ter sido criada antes, logo,
    // o sistema já vai ter um conjunto de cabanas criado. Esse conjunto de cabanas tem o seguinte construtor:

    public Cabana() {
        this.hospedes = new ArrayList<>();
        this.tipoDasCamas = new ArrayList<>();
    }

    public Cabana(String nome, int quantidadeCamas, ArrayList<TipoCama> tipoDasCamas) {
        this.codigo = proximoCodigo++;
        this.nome = nome;
        this.quantidadeCamas = quantidadeCamas;
        this.tipoDasCamas = tipoDasCamas;
        // Quando eu for criar os hospedes eu vou adicionando eles um a um a medida que o sistema é usado,
        // então quando formos adicionar os hospedes, não colocamos como coleção:
        // this.hospedes = hospedes;
        // Mas sim eu vou CRIAR essa coleção, criar um espaço na memória do computador pra armazenar eles. Assim:
        this.hospedes = new ArrayList<>();
    }

    // Criando métodos Getter Setter para cada um dos atributos da minha classe:

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidadeCamas() {
        return quantidadeCamas;
    }

    public void setQuantidadeCamas(int quantidadeCamas) {
        this.quantidadeCamas = quantidadeCamas;
    }

    public ArrayList<TipoCama> getTipoDasCamas() {
        return tipoDasCamas;
    }

    public void setTipoDasCamas(ArrayList<TipoCama> tipoDasCamas) {
        this.tipoDasCamas = tipoDasCamas;
    }

    public ArrayList<Hospede> getHospedes() {
        return hospedes;
    }

    public void setHospedes(ArrayList<Hospede> hospedes) {
        this.hospedes = hospedes;
    }

    // Ação de adicionar um hospede por vez:

    public void adicionarHospede(Hospede hospede) {
        if (hospedes.size() >= quantidadeCamas) {
            System.out.println("Não é possível adicionar mais hóspedes. Cabana cheia.");
            return;
        }
        hospedes.add(hospede);
        hospede.setCabana(this); // Adiciona a referência da cabana ao hóspede
        System.out.println("Hospede " + hospede.getNomeCompleto() + " adicionado à cabana " + this.nome);
    }

    public void removerHospede(Hospede hospede) {
        hospedes.remove(hospede);
    }

    @Override
    public String toString() {
        return "Cabana código " + codigo + ": " + nome;
    }
}