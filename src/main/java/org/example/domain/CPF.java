package org.example.domain;

public class CPF {
    private final String valor;

    public CPF(String valor) {
        if (valor == null || !valor.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new IllegalArgumentException("CPF inválido: Formato deve ser 000.000.000-00");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    // Importante para comparar objetos corretamente
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CPF cpf = (CPF) o;
        return valor.equals(cpf.valor);
    }
}