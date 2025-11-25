package org.example.crud;

import org.example.domain.Funcionario;

import java.util.Optional;
import java.util.Scanner;
import static org.example.crud.CriarFuncionario.funcionarios;


public class ModificarFuncionario {
    public static void modificarFuncionario(int id, String nome, String cpf, double salario) {
        Optional<Funcionario> funcionarioOptional = getFuncionarioById(id);
        Scanner sc = new Scanner(System.in);

        if(funcionarioOptional.isPresent()) {
            Funcionario funcionario = funcionarioOptional.get();
            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setSalario(salario);
        }

    }

    private static Optional<Funcionario> getFuncionarioById(int id) {
        return funcionarios.stream().filter(f -> f.getId() == id).findFirst();
    }
}
