package org.example.service;

import org.example.domain.Funcionario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {
    // Simulando Repository
    private List<Funcionario> repository = new ArrayList<>();

    public List<Funcionario> listarTodos() {
        return repository;
    }

    public Funcionario salvar(Funcionario f) {
        // Regras de Negócio isoladas aqui
        if (f.getId() <= 0) throw new IllegalArgumentException("ID deve ser positivo.");
        if (f.getSalario() < 1300) throw new IllegalArgumentException("Salário abaixo do mínimo.");

        boolean idExiste = repository.stream().anyMatch(func -> func.getId() == f.getId());
        if (idExiste) throw new IllegalArgumentException("ID já cadastrado.");

        boolean cpfExiste = repository.stream().anyMatch(func -> func.getCpf().equals(f.getCpf()));
        if (cpfExiste) throw new IllegalArgumentException("CPF já cadastrado.");

        repository.add(f);
        return f;
    }

    public boolean excluir(int id) {
        return repository.removeIf(f -> f.getId() == id);
    }
}