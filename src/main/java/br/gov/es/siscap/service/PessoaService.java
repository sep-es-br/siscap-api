package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.dto.listagem.PessoaListaDto;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.exception.service.ServiceSisCapException;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaService {

    private final PessoaRepository repository;
    private final ImagemPerfilService imagemPerfilService;
    private final Logger logger = LogManager.getLogger(PessoaService.class);

    @Transactional
    public PessoaDto salvar(PessoaForm form) throws IOException {
        logger.info("Cadatrar nova pessoa: {}.", form);
        String nomeImagem = imagemPerfilService.salvar(form.imagemPerfil());
        logger.info("Imagem de perfil para nova pessoa salva: {}.", form.imagemPerfil());
        Pessoa pessoa = repository.save(new Pessoa(form, nomeImagem));
        logger.info("Cadastro de nova pessoa finalizado com sucesso!");
        return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
    }

    public PessoaDto buscar(Long id) throws IOException {
        logger.info("Buscar pessoa com id [{}]", id);
        Pessoa pessoa = buscarPorId(id);
        return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
    }

    public Page<PessoaListaDto> listarTodos(Pageable pageable) {
        logger.info("Listar todas pessoas");
        return repository.findAll(pageable).map(pessoa -> {
            try {
                return new PessoaListaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
            } catch (IOException e) {
                throw new ServiceSisCapException(Collections.singletonList(e.getMessage()));
            }
        });
    }

    @Transactional
    public PessoaDto atualizar(Long id, PessoaUpdateForm form) throws IOException {
        logger.info("Atualizar pessoa de id {}: {}.", id, form);
        Pessoa pessoa = buscarPorId(id);
        pessoa.atualizar(form);
        if (form.imagemPerfil() != null)
            pessoa.atualizarImagemPerfil(imagemPerfilService.atualizar(pessoa.getNomeImagem(), form.imagemPerfil()));
        repository.save(pessoa);
        logger.info("Atualização de pessoa finalizado com sucesso!");
        return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluir pessoa {}.", id);
        Pessoa pessoa = buscarPorId(id);
        pessoa.apagar();
        repository.saveAndFlush(pessoa);
        repository.deleteById(id);
        imagemPerfilService.apagar(pessoa.getNomeImagem());
        logger.info("Exclusão de pessoa com id {} finalizada com sucesso!", id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<SelectDto> buscarSelect() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream().map(SelectDto::new).toList();
    }

    public Pessoa buscarPorEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new PessoaNaoEncontradoException(email));
    }

    public Pessoa salvarNovaPessoaAcessoCidadao(Pessoa pessoa){
        return repository.save(pessoa);
    }

    private Pessoa buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new PessoaNaoEncontradoException(id));
    }

    private byte[] getImagemNotNull(String nomeImagem) throws IOException {
        if (nomeImagem == null)
            return null;
        Resource imagemPerfil = imagemPerfilService.buscar(nomeImagem);
        byte[] conteudoImagem = null;
        if (imagemPerfil != null)
            conteudoImagem = imagemPerfil.getContentAsByteArray();
        return conteudoImagem;
    }
}
