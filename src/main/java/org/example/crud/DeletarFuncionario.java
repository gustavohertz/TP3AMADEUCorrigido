package org.example.crud;

import org.example.domain.Funcionario;
import java.util.Optional;
import java.util.Scanner;
import static org.example.crud.CriarFuncionario.funcionarios;
import static org.example.crud.VisualizarFuncionario.visualizarFuncionario;

public class DeletarFuncionario {
    static Scanner sc = new Scanner(System.in);
    public static void deletarFuncionario() {
        visualizarFuncionario();
        System.out.println("Digite o ID do funcionário que deseja deletar:");
        int id = sc.nextInt();

        Optional<Funcionario> funcionarioOptional = getFuncionarioById(id);

        if (funcionarioOptional.isPresent()) {
            funcionarios.remove(funcionarioOptional.get());
            System.out.println("Funcionário removido com sucesso!");
        } else {
            System.out.println("Funcionário não encontrado.");
        }
    }

    private static Optional<Funcionario> getFuncionarioById(int id) {
        return funcionarios.stream()
                .filter(f -> f.getId() == id)
                .findFirst();
    }
}
