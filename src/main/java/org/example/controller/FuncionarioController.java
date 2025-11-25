package org.example.controller;

import org.example.domain.Funcionario;
import org.example.service.FuncionarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Funcionario> listar() {
        return service.listarTodos();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Funcionario novoFuncionario) {
        try {
            Funcionario salvo = service.salvar(novoFuncionario);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (IllegalArgumentException e) {
            // Captura erros de negócio e retorna 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable int id) {
        if (service.excluir(id)) {
            return ResponseEntity.ok("Removido com sucesso.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado.");
    }
}