package org.example.crud;

import org.example.domain.Funcionario;

import static org.example.crud.CriarFuncionario.funcionarios;

public class VisualizarFuncionario {
    public static void visualizarFuncionario(){
        System.out.println("\n--- LISTA DE FUNCIONÁRIOS ---");
        if (funcionarios.isEmpty()) {
            System.out.println("Nenhum funcionário cadastrado.");
        } else {
            for (Funcionario f : funcionarios) {
                System.out.println(f.toString());
            }
        }
        System.out.println("----------------------------\n");
    }
}
