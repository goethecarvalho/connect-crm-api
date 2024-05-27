package br.com.connect.crm.controller;

import br.com.connect.crm.domain.entidades.service.EntidadeService;
import br.com.connect.crm.domain.entidades.vo.DadosDetalheEntidade;
import br.com.connect.crm.domain.propostas.service.PropostaService;
import br.com.connect.crm.domain.propostas.vo.DadosDetalheProposta;
import br.com.connect.crm.domain.propostas.vo.DadosProposta;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("propostas")
public class PropostaController {

    @Autowired
    private final PropostaService propostaService;

    @Autowired
    private final EntidadeService entidadeService;

    public PropostaController(PropostaService propostaService, EntidadeService entidadeService) {
        this.propostaService = propostaService;
        this.entidadeService = entidadeService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosProposta dados, UriComponentsBuilder uriBuilder){
        DadosDetalheEntidade entidade = entidadeService.detalhar(dados.id());
        var dadosPropostaCadastrada = propostaService.cadastrarProposta(dados);
        var uri = uriBuilder.path("propostas/{id}").buildAndExpand(dadosPropostaCadastrada.id()).toUri();
        return ResponseEntity.created(uri).body(dadosPropostaCadastrada);
    }

    @GetMapping
    public ResponseEntity<Page<DadosDetalheProposta>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var proposta = propostaService.listar(paginacao);
        return ResponseEntity.ok(proposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalheProposta> detalhar(@PathVariable Long id) {
        var proposta = propostaService.detalhar(id);
        return ResponseEntity.ok(proposta);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalheProposta> atualizar(@PathVariable Long id, @RequestBody @Valid DadosProposta dados) {
        var propostaAtualizada = propostaService.atualizarProposta(id, dados);
        return ResponseEntity.ok(propostaAtualizada);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        propostaService.deletarProposta(id);
        return ResponseEntity.noContent().build();
    }

}