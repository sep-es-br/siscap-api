package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.PessoaListaDto;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.form.PessoaUpdateForm;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaService {

    private final PessoaRepository repository;
    private final ImagemPerfilService imagemPerfilService;
    private final Logger logger = LogManager.getLogger(PessoaService.class);

    @Transactional
    public PessoaDto salvar(PessoaForm form) {
        logger.info("Cadatrar nova pessoa: {}.", form);
        String nomeImagem = imagemPerfilService.salvar(form.imagemPerfil());
        logger.info("Imagem de perfil para nova pessoa salva: {}.", form.imagemPerfil());
        Pessoa pessoa = repository.save(new Pessoa(form, nomeImagem));
        logger.info("Cadastro de nova pessoa finalizado com sucesso!");
        return new PessoaDto(pessoa);
    }

    public PessoaDto buscar(Long id) {
        logger.info("Buscar pessoa com id [{}]", id);
        Pessoa pessoa = buscarPorId(id);
        return new PessoaDto(pessoa);
    }

    public Page<PessoaListaDto> listarTodos(Pageable pageable) {
        logger.info("Listar todas pessoas");
        return repository.findAll(pageable).map(PessoaListaDto::new);
    }

    public Resource buscarImagemPerfil(Long id) {
        logger.info("Buscar imagem de perfil da pessoa com id [{}]", id);
        Pessoa pessoa = buscarPorId(id);
        return imagemPerfilService.buscar(pessoa.getNomeImagem());
    }

    @Transactional
    public PessoaDto atualizar(Long id, PessoaUpdateForm form) {
        logger.info("Atualizar pessoa de id {}: {}.", id, form);
        String nomeImagem;
        Pessoa pessoa = buscarPorId(id);
        pessoa.atualizarPessoa(form);
        if (form.imagemPerfil() != null) {
            imagemPerfilService.apagar(pessoa.getNomeImagem());
            nomeImagem = imagemPerfilService.salvar(form.imagemPerfil());
            pessoa.atualizarImagemPerfil(nomeImagem);
        }
        logger.info("Atualização de pessoa finalizado com sucesso!");
        return new PessoaDto(pessoa);
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluir pessoa {}.", id);
        Pessoa pessoa = buscarPorId(id);
        repository.deleteById(id);
        imagemPerfilService.apagar(pessoa.getNomeImagem());
        pessoa.setAtualizadoEm(LocalDateTime.now());
        logger.info("Exclusão de pessoa com id {} finalizada com sucesso!", id);
    }

    private Pessoa buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new PessoaNaoEncontradoException(id));
    }
}
