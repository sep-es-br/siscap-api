package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EntidadeDto;
import br.gov.es.siscap.dto.EntidadeListaDto;
import br.gov.es.siscap.dto.EntidadeSelectDto;
import br.gov.es.siscap.exception.naoencontrado.EntidadeNaoEncontradaException;
import br.gov.es.siscap.exception.service.ServiceSisCapException;
import br.gov.es.siscap.form.EntidadeForm;
import br.gov.es.siscap.form.EntidadeUpdateForm;
import br.gov.es.siscap.models.Entidade;
import br.gov.es.siscap.repository.EntidadeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntidadeService {

    private final EntidadeRepository repository;
    private final CidadeService cidadeService;
    private final PessoaService pessoaService;
    private final PaisService paisService;
    private final TipoEntidadeService tipoEntidadeService;
    private final ImagemPerfilService imagemPerfilService;

    private final Logger logger = LogManager.getLogger(EntidadeService.class);

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<EntidadeSelectDto> buscarSelect() {
        return repository.findAll().stream().map(EntidadeSelectDto::new).toList();
    }

    public Page<EntidadeListaDto> listarTodos(Pageable pageable) {
        logger.info("Buscar todas entidades.");
        return repository.findAll(pageable).map(entidade -> {
            try {
                return new EntidadeListaDto(entidade, getImagemNotNull(entidade.getNomeImagem()));
            } catch (IOException e) {
                throw new ServiceSisCapException(Collections.singletonList(e.getMessage()));
            }
        });
    }

    @Transactional
    public EntidadeDto salvar(EntidadeForm form) throws IOException {
        logger.info("Cadastrar nova entidade: {}", form);

        validarForm(form);

        String nomeImagem = form.imagemPerfil() != null ? imagemPerfilService.salvar(form.imagemPerfil()) : null;
        Entidade entidade = repository.save(new Entidade(form, nomeImagem));
        logger.info("Cadastro de entidade finalizado!");
        return new EntidadeDto(entidade, getImagemNotNull(entidade.getNomeImagem()));
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluir entidade {}.", id);
        Entidade entidade = buscarPorId(id);
        entidade.apagar();
        repository.saveAndFlush(entidade);
        repository.deleteById(entidade.getId());
        imagemPerfilService.apagar(entidade.getNomeImagem());
        logger.info("Exclusão da entidade com id {} finalizada!", id);
    }

    @Transactional
    public EntidadeDto atualizar(Long id, EntidadeUpdateForm form) throws IOException {
        logger.info("Atualizar entidade de id {}: {}", id, form);
        Entidade entidade = buscarPorId(id);
        entidade.atualizar(form);
        if (form.imagemPerfil() != null)
            entidade.atualizarImagemPerfil(imagemPerfilService.atualizar(entidade.getNomeImagem(), form.imagemPerfil()));
        repository.save(entidade);
        logger.info("Atualização da entidade {} finalizada!", id);
        return new EntidadeDto(entidade, getImagemNotNull(entidade.getNomeImagem()));
    }

    public EntidadeDto buscar(Long id) throws IOException {
        logger.info("Buscar pessoa {}.", id);
        Entidade entidade = buscarPorId(id);
        return new EntidadeDto(entidade, getImagemNotNull(entidade.getNomeImagem()));
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

    private Entidade buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException(id));
    }

    private void validarForm(EntidadeForm form) {
        List<String> erros = new ArrayList<>();
        if (form.idCidade() != null && !cidadeService.existePorId(form.idCidade()))
            erros.add("Erro ao encontrar cidade com id " + form.idCidade());

        if (!paisService.existePorId(form.idPais()))
            erros.add("Erro ao encontrar país com id " + form.idPais());

        if (form.idEntidadePai() != null && !repository.existsById(form.idEntidadePai()))
            erros.add("Erro ao encontrar entidade pai com id " + form.idEntidadePai());

        if (form.idPessoaResponsavel() != null && !pessoaService.existePorId(form.idPessoaResponsavel()))
            erros.add("Erro ao encontrar pessoa responsável com id " + form.idPessoaResponsavel());

        if (!tipoEntidadeService.existePorId(form.idTipoEntidade()))
            erros.add("Erro ao encontrar tipo de entidade com id " + form.idTipoEntidade());

        if (!erros.isEmpty()) {
            erros.forEach(logger::warn);
            throw new ServiceSisCapException(erros);
        }
    }

}
