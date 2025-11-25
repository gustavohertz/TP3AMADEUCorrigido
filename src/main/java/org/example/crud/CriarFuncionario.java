package org.example.crud;

import org.example.domain.Funcionario;
import java.util.*;

public class CriarFuncionario {
    // Lista em memória simulando o banco de dados
    public static List<Funcionario> funcionarios = new ArrayList<>();
    static Scanner digitar = new Scanner(System.in);

    public static void criarFuncionario() {
        try {
            System.out.println("--- NOVO FUNCIONÁRIO ---");

            int id = lerId();
            String nome = lerNome();
            String cpf = lerCpf();
            double salario = lerSalario();

            // Instancia o objeto com o novo construtor (ID, Nome, CPF, Salario)
            Funcionario novoFuncionario = new Funcionario(id, nome, cpf, salario);

            funcionarios.add(novoFuncionario);

            System.out.println("\n Funcionário cadastrado com sucesso!");
            System.out.println("Dados: " + novoFuncionario.toString());
            System.out.println("Total de funcionários no sistema: " + funcionarios.size());
            System.out.println("--------------------------------\n");

        } catch (IllegalArgumentException e) {
            System.err.println(" Erro de validação: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println(" Entrada inválida. Verifique o tipo de dado digitado.");
            digitar.nextLine(); // Limpa o buffer do scanner em caso de erro
        } catch (Exception e) {
            System.err.println(" Erro inesperado: " + e.getMessage());
        }
    }

    private static int lerId() {
        System.out.print("Digite o ID (número): ");
        int id = digitar.nextInt();
        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser um número positivo.");
        }
        // Verifica se ID já existe na lista
        if (funcionarios.stream().anyMatch(f -> f.getId() == id)) {
            throw new IllegalArgumentException("Já existe um funcionário com este ID: " + id);
        }
        return id;
    }

    private static String lerNome() {
        digitar.nextLine(); // Consome a quebra de linha pendente do nextInt() anterior
        System.out.print("Digite o Nome Completo: ");
        String nome = digitar.nextLine().trim();

        if (nome.isEmpty() || nome.length() < 3) {
            throw new IllegalArgumentException("O nome deve ter pelo menos 3 caracteres.");
        }
        return nome;
    }

    private static String lerCpf() {
        // Não precisa de nextLine() aqui pois o anterior foi um nextLine() também
        System.out.print("Digite o CPF (apenas números ou com pontuação): ");
        String cpf = digitar.nextLine().trim();

        // Remove caracteres não numéricos para validação e armazenamento limpo
        String cpfLimpo = cpf.replaceAll("\\D", "");

        // Validação simples de tamanho (11 dígitos)
        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos.");
        }

        // Verifica duplicidade de CPF (regra de negócio importante)
        if (funcionarios.stream().anyMatch(f -> f.getCpf().equals(cpf))) {
            throw new IllegalArgumentException("Este CPF já está cadastrado no sistema.");
        }

        return cpf; // Retorna o CPF digitado (ou poderia retornar cpfLimpo se preferir padronizar)
    }

    private static double lerSalario() {
        System.out.print("Digite o Salário: ");
        // Tratamento para aceitar vírgula ou ponto dependendo do locale do sistema
        double salario = digitar.nextDouble();

        if (salario < 1300.0) {
            throw new IllegalArgumentException("O salário deve ser maior ou igual ao mínimo (R$ 1300.00).");
        }
        return salario;
    }
}