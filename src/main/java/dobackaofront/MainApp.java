package dobackaofront;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.collections.FXCollections;

public class MainApp extends Application {

    private ArrayList<ComboBox<Cabana>> cabanaComboBoxes = new ArrayList<>();

    private Hotel hotel = Persistencia.carregarHotel("hotel.json");

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Sistema de Hotel");

        // -------------------- CAMPOS DA ABA DE HÓSPEDE --------------------
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome completo");

        TextField idadeField = new TextField();
        idadeField.setPromptText("Idade");

        TextField cpfField = new TextField();
        cpfField.setPromptText("CPF");

        DatePicker dataChegadaPicker = new DatePicker();
        dataChegadaPicker.setPromptText("Data de chegada");

        DatePicker dataSaidaPicker = new DatePicker();
        dataSaidaPicker.setPromptText("Data de saída");

        ComboBox<Cabana> cabanaComboBox = new ComboBox<>();
        cabanaComboBox.getItems().addAll(hotel.getCabanas());
        cabanaComboBox.setPromptText("Selecionar cabana");

        cabanaComboBoxes.add(cabanaComboBox);

        Button adicionarBtn = new Button("Adicionar Hóspede");
        Button salvarBtn = new Button("Salvar no JSON");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(10);

        adicionarBtn.setOnAction(e -> {
            String nome = nomeField.getText().trim();
            String idadeStr = idadeField.getText().trim();
            String cpf = cpfField.getText().trim();
            Cabana cabana = cabanaComboBox.getValue();
            LocalDate dataChegada = dataChegadaPicker.getValue();
            LocalDate dataSaida = dataSaidaPicker.getValue();

            if (dataSaida.isBefore(dataChegada)) {
                outputArea.setText("❌ Data de saída não pode ser antes da data de chegada.");
                return;
            }

            if (nome.trim().isEmpty() || idadeStr.trim().isEmpty() || cpf.trim().isEmpty() ||
                    cabana == null || dataChegada == null || dataSaida == null) {
                outputArea.setText("❌ Preencha todos os campos e selecione uma cabana e as datas.");
                return;
            }

            try {
                int idade = Integer.parseInt(idadeStr);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dataChegadaStr = dataChegada.format(formatter);
                String dataSaidaStr = dataSaida.format(formatter);

                if (!validarCPF(cpf)) {
                    outputArea.setText("❌ CPF inválido.");
                    return;
                }

                if (buscarHospedePorCPF(cpf) != null) {
                    outputArea.setText("❌ Já existe um hóspede com esse CPF.");
                    return;
                }

                Hospede novoHospede = new Hospede(cpf, nome, idade, dataChegadaStr, dataSaidaStr);
                cabana.adicionarHospede(novoHospede);
                outputArea.setText("✅ Hóspede adicionado com sucesso!\n\n" + hotel);

                nomeField.clear();
                idadeField.clear();
                cpfField.clear();
                dataChegadaPicker.setValue(null);
                dataSaidaPicker.setValue(null);
                cabanaComboBox.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                outputArea.setText("❌ Idade inválida.");
            }
        });

        salvarBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Salvamento");
            confirm.setHeaderText("Você deseja salvar os dados no JSON?");
            confirm.setContentText("Isso substituirá o conteúdo anterior do arquivo 'hotel.json'.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Persistencia.salvarHotel(hotel, "hotel.json");
                    outputArea.setText("💾 Dados salvos com sucesso!");
                }
            });
        });

        VBox cadastroHospedePane = new VBox(10,
                nomeField, idadeField, cpfField,
                dataChegadaPicker, dataSaidaPicker,
                cabanaComboBox, adicionarBtn,
                salvarBtn, outputArea
        );
        cadastroHospedePane.setPadding(new Insets(15));


        // -------------------- ABA DE CADASTRO DE CABANAS --------------------

        TextField nomeCabanaField = new TextField();
        nomeCabanaField.setPromptText("Nome da cabana");

        TextField qtdCamasField = new TextField();
        qtdCamasField.setPromptText("Quantidade de camas");

        ComboBox<TipoCama> camaBox = new ComboBox<>();
        camaBox.getItems().addAll(TipoCama.values());
        camaBox.setPromptText("Tipo de cama");

        ListView<TipoCama> listaCamas = new ListView<>();
        listaCamas.setPrefHeight(100);

        Button adicionarCamaBtn = new Button("Adicionar cama");
        adicionarCamaBtn.setOnAction(e -> {
            TipoCama tipo = camaBox.getValue();
            if (tipo != null) {
                listaCamas.getItems().add(tipo);
            }
        });

        Button criarCabanaBtn = new Button("Criar Cabana");
        TextArea resultadoCabanaArea = new TextArea();
        resultadoCabanaArea.setEditable(false);
        resultadoCabanaArea.setPrefRowCount(5);

        criarCabanaBtn.setOnAction(e -> {
            String nomeCabana = nomeCabanaField.getText();
            String qtdStr = qtdCamasField.getText();
            ArrayList<TipoCama> tipos = new ArrayList<>(listaCamas.getItems());

            if (nomeCabana.isEmpty() || qtdStr.isEmpty() || tipos.isEmpty()) {
                resultadoCabanaArea.setText("❌ Preencha todos os campos e adicione pelo menos um tipo de cama.");
                return;
            }

            try {
                int qtd = Integer.parseInt(qtdStr);
                Cabana novaCabana = new Cabana(nomeCabana, qtd, tipos);
                hotel.adicionarCabana(novaCabana);

                // Atualizar combos:
                cabanaComboBox.getItems().add(novaCabana);

                resultadoCabanaArea.setText("✅ Cabana criada com sucesso:\n" + novaCabana.toString());

                nomeCabanaField.clear();
                qtdCamasField.clear();
                listaCamas.getItems().clear();
                camaBox.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                resultadoCabanaArea.setText("❌ Quantidade inválida.");
            }
        });

        Tab tabEditarHospede = tabEditarHospede();
        Tab tabEditarCabana = tabEditarCabana();

        VBox cadastroCabanaPane = new VBox(10,
                nomeCabanaField, qtdCamasField, camaBox,
                adicionarCamaBtn, listaCamas, criarCabanaBtn, resultadoCabanaArea
        );
        cadastroCabanaPane.setPadding(new Insets(15));


        // -------------------- TABS --------------------

        TabPane tabPane = new TabPane();
        Tab tabHospede = new Tab("Cadastro de Hóspede", cadastroHospedePane);
        Tab tabCabana = new Tab("Cadastro de Cabana", cadastroCabanaPane);
        Tab tabExcluir = tabExcluir();
        Tab tabBuscarHospede = tabBuscarHospede();
        Tab tabListarHospedesCabana = tabListarHospedesCabana();
        Tab tabListarCabanas = tabListarCabanas();

        tabHospede.setClosable(false);
        tabCabana.setClosable(false);
        tabEditarHospede.setClosable(false);
        tabEditarCabana.setClosable(false);
        tabExcluir.setClosable(false);
        tabBuscarHospede.setClosable(false);
        tabListarHospedesCabana.setClosable(false);
        tabListarCabanas.setClosable(false);
        tabPane.getTabs().addAll(tabHospede, tabCabana, tabEditarHospede, tabEditarCabana, tabExcluir, tabBuscarHospede, tabListarHospedesCabana, tabListarCabanas);

        // -------------------- MENU AJUDA --------------------

        MenuBar menuBar = new MenuBar();
        Menu menuAjuda = new Menu("Ajuda");
        MenuItem sobreItem = new MenuItem("Sobre");
        sobreItem.setOnAction(e -> showAlert("Sistema de Hotel\nFeito por João Vitor Spdo Coelho Targueta\nLinkedIn: https://www.linkedin.com/in/joaocoelhot/"));
        menuAjuda.getItems().add(sobreItem);
        menuBar.getMenus().add(menuAjuda);

        VBox layoutFinal = new VBox(menuBar, tabPane);

        Scene scene = new Scene(layoutFinal, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sobre");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void fadeSuccess(String mensagem, Pane container) {
        Label label = new Label(mensagem);
        label.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        StackPane toast = new StackPane(label);
        toast.setStyle("-fx-background-color: #e0ffe0; -fx-border-color: green; -fx-padding: 10; -fx-background-radius: 8;");
        toast.setOpacity(0);

        container.getChildren().add(toast); // ADICIONA direto no layout que já existe na tela

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> container.getChildren().remove(toast));

        fadeIn.play();
    }

    private ComboBox<Hospede> criarComboHospede(ComboBox<Cabana> cabanaComboBox) {
        ComboBox<Hospede> combo = new ComboBox<>();
        cabanaComboBox.setOnAction(e -> {
            Cabana selecionada = cabanaComboBox.getValue();
            if (selecionada != null) {
                combo.getItems().clear();
                combo.getItems().addAll(selecionada.getHospedes());
            }
        });
        return combo;
    }

    private void atualizarCabanaComboBoxes() {
        for (ComboBox<Cabana> cb : cabanaComboBoxes) {
            cb.getItems().setAll(hotel.getCabanas());
        }
    }

    private Tab tabEditarHospede() {
        ComboBox<Cabana> cabanaBox = new ComboBox<>();
        cabanaBox.getItems().addAll(hotel.getCabanas());
        cabanaBox.setPromptText("Escolha a cabana");

        cabanaComboBoxes.add(cabanaBox);

        ComboBox<Hospede> hospedeBox = criarComboHospede(cabanaBox);
        hospedeBox.setPromptText("Escolha o hóspede");

        TextField novoNome = new TextField();
        novoNome.setPromptText("Novo nome");

        TextField novaIdade = new TextField();
        novaIdade.setPromptText("Nova idade");

        DatePicker novaChegada = new DatePicker();
        DatePicker novaSaida = new DatePicker();

        Button salvar = new Button("Salvar alterações");
        TextArea resultado = new TextArea();
        resultado.setEditable(false);

        salvar.setOnAction(e -> {
            Hospede h = hospedeBox.getValue();
            if (h == null) {
                resultado.setText("❌ Selecione um hóspede.");
                return;
            }
            if (!novoNome.getText().isEmpty()) h.setNomeCompleto(novoNome.getText());
            if (!novaIdade.getText().isEmpty()) {
                try {
                    int idade = Integer.parseInt(novaIdade.getText());
                    h.setIdade(idade);
                } catch (NumberFormatException ex) {
                    resultado.setText("❌ Idade inválida.");
                    return;
                }
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (novaChegada.getValue() != null) h.setDataChegada(novaChegada.getValue().format(fmt));
            if (novaSaida.getValue() != null) h.setDataSaida(novaSaida.getValue().format(fmt));

            resultado.setText("✅ Alterações aplicadas!");
        });

        VBox layoutExcluir  = new VBox(10, cabanaBox, hospedeBox, novoNome, novaIdade,
                novaChegada, novaSaida, salvar, resultado);
        layoutExcluir .setPadding(new Insets(15));

        return new Tab("Editar Hóspede", layoutExcluir );
    }

    private Tab tabEditarCabana() {
        ComboBox<Cabana> cabanaBox = new ComboBox<>();
        cabanaBox.getItems().addAll(hotel.getCabanas());
        cabanaBox.setPromptText("Escolha a cabana");

        cabanaComboBoxes.add(cabanaBox);

        TextField novoNome = new TextField();
        novoNome.setPromptText("Novo nome");

        TextField novaQtd = new TextField();
        novaQtd.setPromptText("Nova quantidade de camas");

        ComboBox<TipoCama> camaTipoBox = new ComboBox<>();
        camaTipoBox.getItems().addAll(TipoCama.values());

        ListView<TipoCama> listaCamas = new ListView<>();
        listaCamas.setPrefHeight(100);

        Button adicionarCama = new Button("Adicionar tipo de cama");
        adicionarCama.setOnAction(e -> {
            TipoCama tipo = camaTipoBox.getValue();
            if (tipo != null) listaCamas.getItems().add(tipo);
        });

        Button salvar = new Button("Salvar alterações");
        TextArea resultado = new TextArea();
        resultado.setEditable(false);

        salvar.setOnAction(e -> {
            Cabana c = cabanaBox.getValue();
            if (c == null) {
                resultado.setText("❌ Selecione uma cabana.");
                return;
            }

            if (!novoNome.getText().isEmpty()) c.setNome(novoNome.getText());

            if (!novaQtd.getText().isEmpty()) {
                try {
                    int qtd = Integer.parseInt(novaQtd.getText());
                    c.setQuantidadeCamas(qtd);
                } catch (NumberFormatException ex) {
                    resultado.setText("❌ Quantidade inválida.");
                    return;
                }
            }

            if (!listaCamas.getItems().isEmpty()) {
                c.setTipoDasCamas(new ArrayList<>(listaCamas.getItems()));
            }

            atualizarCabanaComboBoxes();
            resultado.setText("✅ Alterações aplicadas à cabana!");
        });

        VBox layout = new VBox(10, cabanaBox, novoNome, novaQtd,
                camaTipoBox, adicionarCama, listaCamas, salvar, resultado);
        layout.setPadding(new Insets(15));

        return new Tab("Editar Cabana", layout);
    }

    private Tab tabExcluir() {
        VBox layoutExcluir = new VBox(10);
        layoutExcluir.setPadding(new Insets(15));

        Label labelCabana = new Label("Escolha a cabana:");
        ComboBox<Cabana> cabanaBox = new ComboBox<>();
        cabanaComboBoxes.add(cabanaBox);

        cabanaBox.getItems().addAll(hotel.getCabanas());

        Label labelHospede = new Label("Escolha o hóspede:");
        ComboBox<Hospede> hospedeBox = criarComboHospede(cabanaBox);

        Button excluirHospedeBtn = new Button("Excluir Hóspede");
        excluirHospedeBtn.setOnAction(e -> {
            Cabana c = cabanaBox.getValue();
            Hospede h = hospedeBox.getValue();
            if (c == null || h == null) {
                showAlert("❌ Selecione uma cabana e um hóspede.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmação");
            confirm.setHeaderText("Deseja realmente excluir este hóspede?");
            confirm.setContentText("Nome: " + h.getNomeCompleto());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    c.removerHospede(h);
                    hospedeBox.getItems().remove(h);
                    fadeSuccess("✅ Hóspede removido com sucesso.", layoutExcluir);
                }
            });
        });

        Separator separador = new Separator();

        // ---------- NOVA SEÇÃO: EXCLUIR POR CPF ou NOME ----------
        Label labelBusca = new Label("Excluir hóspede por CPF ou Nome:");
        TextField campoBusca = new TextField();
        campoBusca.setPromptText("Digite CPF ou Nome");

        Button buscarExcluirBtn = new Button("Buscar e Excluir");
        TextArea resultadoBusca = new TextArea();
        resultadoBusca.setEditable(false);

        buscarExcluirBtn.setOnAction(e -> {
            String busca = campoBusca.getText().trim();
            if (busca.isEmpty()) {
                resultadoBusca.setText("❌ Digite um CPF ou nome.");
                return;
            }

            final Hospede[] encontrado = {null};
            final Cabana[] cabanaDoHospede = {null};

            for (Cabana c : hotel.getCabanas()) {
                for (Hospede h : c.getHospedes()) {
                    if (h.getCpf().equalsIgnoreCase(busca) || h.getNomeCompleto().equalsIgnoreCase(busca)) {
                        encontrado[0] = h;
                        cabanaDoHospede[0] = c;
                        break;
                    }
                }
                if (encontrado[0] != null) break;
            }

            if (encontrado[0] == null) {
                resultadoBusca.setText("❌ Hóspede não encontrado.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmação");
            confirm.setHeaderText("Deseja excluir este hóspede?");
            confirm.setContentText("Nome: " + encontrado[0].getNomeCompleto() + "\nCPF: " + encontrado[0].getCpf());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    cabanaDoHospede[0].removerHospede(encontrado[0]);
                    resultadoBusca.setText("✅ Hóspede removido com sucesso.");
                    campoBusca.clear();
                    // Atualizar combos, se necessário
                    cabanaBox.getItems().clear();
                    cabanaBox.getItems().addAll(hotel.getCabanas());
                    hospedeBox.getItems().clear();
                }
            });
        });

        VBox secaoBuscaDireta = new VBox(5, labelBusca, campoBusca, buscarExcluirBtn, resultadoBusca);

        // ---------- EXCLUIR CABANA ----------
        Label labelExcluirCabana = new Label("Excluir uma cabana inteira:");
        ComboBox<Cabana> cabanaExcluirBox = new ComboBox<>();
        cabanaExcluirBox.getItems().addAll(hotel.getCabanas());

        cabanaComboBoxes.add(cabanaExcluirBox);

        Button excluirCabanaBtn = new Button("Excluir Cabana");
        excluirCabanaBtn.setOnAction(e -> {
            Cabana c = cabanaExcluirBox.getValue();
            if (c == null) {
                showAlert("❌ Selecione uma cabana.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmação");
            confirm.setHeaderText("Deseja realmente excluir esta cabana?");
            confirm.setContentText("Nome: " + c.getNome());
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    hotel.getCabanas().remove(c);
                    cabanaBox.getItems().remove(c);
                    cabanaExcluirBox.getItems().remove(c);
                    fadeSuccess("✅ Cabana removida com sucesso.", layoutExcluir);
                }
            });
        });

        layoutExcluir.getChildren().addAll(
                labelCabana, cabanaBox,
                labelHospede, hospedeBox, excluirHospedeBtn,
                separador,
                secaoBuscaDireta,
                new Separator(),
                labelExcluirCabana, cabanaExcluirBox, excluirCabanaBtn
        );

        return new Tab("Excluir", layoutExcluir);
    }

    private Tab tabBuscarHospede() {
        TextField buscarCpfField = new TextField();
        buscarCpfField.setPromptText("Digite o CPF para buscar");

        Button buscarBtn = new Button("Buscar hóspede");
        TextArea resultadoBusca = new TextArea();
        resultadoBusca.setEditable(false);
        resultadoBusca.setPrefRowCount(5);

        buscarBtn.setOnAction(e -> {
            String cpfBusca = buscarCpfField.getText();
            if (!validarCPF(cpfBusca)) {
                resultadoBusca.setText("❌ CPF inválido para busca.");
                return;
            }

            Hospede hospede = buscarHospedePorCPF(cpfBusca);
            if (hospede == null) {
                resultadoBusca.setText("❌ Hóspede não encontrado.");
            } else {
                resultadoBusca.setText("✅ Hóspede encontrado:\n" + hospede.toString());
            }
        });

        VBox layout = new VBox(10, buscarCpfField, buscarBtn, resultadoBusca);
        layout.setPadding(new Insets(15));
        return new Tab("Buscar Hóspede", layout);
    }

    private Tab tabListarHospedesCabana(){
        ComboBox<Cabana> cabanaListarBox = new ComboBox<>();
        cabanaListarBox.getItems().addAll(hotel.getCabanas());
        cabanaListarBox.setPromptText("Selecione a cabana");
        cabanaComboBoxes.add(cabanaListarBox);

        TextArea listaHospedesArea = new TextArea();
        listaHospedesArea.setEditable(false);

        cabanaListarBox.setOnAction(e -> {
            Cabana selecionada = cabanaListarBox.getValue();
            if (selecionada != null) {
                StringBuilder sb = new StringBuilder();
                for (Hospede h : selecionada.getHospedes()) {
                    sb.append(h.toString()).append("\n\n");
                }
                listaHospedesArea.setText(sb.toString());
            }
        });

        VBox layout = new VBox(10, cabanaListarBox, listaHospedesArea);
        layout.setPadding(new Insets(15));

        return new Tab("Listar Hospedes por Cabana", layout);
    }

    private Tab tabListarCabanas(){
        TextArea listaCabanasArea = new TextArea();
        listaCabanasArea.setEditable(false);

        Button atualizarCabanasBtn = new Button("Listar Cabanas");
        atualizarCabanasBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Cabana c : hotel.getCabanas()) {
                sb.append(c.toString()).append("\n\n");
            }
            listaCabanasArea.setText(sb.toString());
        });
        VBox layout = new VBox(10, atualizarCabanasBtn, listaCabanasArea);
        layout.setPadding(new Insets(15));

        return new Tab("Listar Cabanas", layout);
    }

    private boolean validarCPF(String cpf) {
        // Remove tudo que não for dígito
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) return false;

        // Verifica se todos os dígitos são iguais (ex: 11111111111)
        if (cpf.chars().distinct().count() == 1) return false;

        // Calcula os dígitos verificadores
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstCheckDigit = 11 - (sum % 11);
            if (firstCheckDigit >= 10) firstCheckDigit = 0;
            if (firstCheckDigit != Character.getNumericValue(cpf.charAt(9))) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondCheckDigit = 11 - (sum % 11);
            if (secondCheckDigit >= 10) secondCheckDigit = 0;
            if (secondCheckDigit != Character.getNumericValue(cpf.charAt(10))) return false;

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Hospede buscarHospedePorCPF(String cpf) {
        for (Cabana c : hotel.getCabanas()) {
            for (Hospede h : c.getHospedes()) {
                if (h.getCpf().equals(cpf)) {
                    return h;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}