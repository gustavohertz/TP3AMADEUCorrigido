package org.example.domain;

import org.example.domain.CPF;

public class Funcionario {
    private int id;
    private String nome;
    private CPF cpf; // Agora usa o Objeto, não String
    private double salario;

    public Funcionario() {} // Necessário para Jackson/Spring

    public Funcionario(int id, String nome, String cpfString, double salario) {
        this.id = id;
        this.nome = nome;
        this.cpf = new CPF(cpfString);
        this.salario = salario;
    }

    // Getters e Setters adaptados
    public String getCpf() { return cpf != null ? cpf.getValor() : null; }
    public void setCpf(String cpf) { this.cpf = new CPF(cpf); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
}