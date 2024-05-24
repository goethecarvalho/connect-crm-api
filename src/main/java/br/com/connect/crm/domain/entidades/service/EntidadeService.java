package br.com.connect.crm.domain.entidades.service;

import br.com.connect.crm.domain.RegraDeNegocioException;
import br.com.connect.crm.domain.entidades.entity.Entidade;
import br.com.connect.crm.domain.entidades.repository.EntidadeRepository;
import br.com.connect.crm.domain.entidades.vo.DadosDetalheEntidade;
import br.com.connect.crm.domain.entidades.vo.DadosEntidade;
import br.com.connect.crm.domain.usuario.entity.Usuario;
import br.com.connect.crm.domain.usuario.repository.UsuarioRepository;
import br.com.connect.crm.domain.usuario.vo.DadosDetalheUsuario;
import br.com.connect.crm.domain.usuario.vo.DadosUsuario;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EntidadeService {

    private final EntidadeRepository repository;

    public EntidadeService(EntidadeRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "listaEntidades")
    public Page<DadosDetalheEntidade> listar(Pageable paginacao) {
        return repository.findAll(paginacao).map(DadosDetalheEntidade::new);
    }

    @CacheEvict(value = "listaEntidades", allEntries = true)
    public DadosDetalheEntidade cadastrarEntidade(DadosEntidade dados) {
        if (dados.nome() == null || dados.nome().isEmpty()) {
            throw new RegraDeNegocioException("O nome deve estar preenchido!");
        }

        var entidade = new Entidade(dados);

        repository.save(entidade);

        return new DadosDetalheEntidade(entidade);
    }

    @CacheEvict(value = "listaEntidades", allEntries = true)
    public DadosDetalheEntidade atualizarEntidade(Long id, DadosEntidade dados) {
        var entidade = repository.findById(id).orElseThrow(() -> new RuntimeException("Entidade não encontrada"));

        DadosEntidade entidadeAtualizada = new DadosEntidade(
                dados.id(),
                dados.nome(),
                dados.tipo()
        );

        entidade.atualizarDados(entidadeAtualizada);

        repository.save(entidade);

        return new DadosDetalheEntidade(entidade);
    }

    public DadosDetalheEntidade detalhar(Long id) {
        var entidade = repository.findById(id).get();
        return new DadosDetalheEntidade(entidade);
    }

    @CacheEvict(value = "listaEntidades", allEntries = true)
    public void deletarEntidade(Long id) {
        var entidade = repository.findById(id).orElseThrow(() -> new RuntimeException("Entidade não encontrada"));
        repository.delete(entidade);
    }
}