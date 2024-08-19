package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.dto.listagem.OrganizacaoListaDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.OrganizacaoNaoEncontradaException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.OrganizacaoForm;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.repository.OrganizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizacaoService {

    private final OrganizacaoRepository repository;
    private final CidadeService cidadeService;
    private final PessoaOrganizacaoService pessoaOrganizacaoService;
    private PessoaService pessoaService;
    private final PaisService paisService;
    private final TipoOrganizacaoService tipoOrganizacaoService;
    private final ImagemPerfilService imagemPerfilService;
    private final Logger logger = LogManager.getLogger(OrganizacaoService.class);

    @Autowired
    private void setPessoaService(@Lazy PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<SelectDto> buscarSelect() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream().map(SelectDto::new).toList();
    }

    public Page<OrganizacaoListaDto> listarTodos(Pageable pageable) {
        logger.info("Buscar todas organizações.");
        return repository.findAll(pageable).map(organizacao -> {
            try {
                return new OrganizacaoListaDto(organizacao, getImagemNotNull(organizacao.getNomeImagem()));
            } catch (IOException e) {
                throw new SiscapServiceException(Collections.singletonList(e.getMessage()));
            }
        });
    }

    @Transactional
    public OrganizacaoDto salvar(OrganizacaoForm form) throws IOException {
        logger.info("Cadastrar nova organização: {}", form);

        validarOrganizacao(form, true);

        String nomeImagem = imagemPerfilService.salvar(form.imagemPerfil());
        Organizacao organizacao = repository.save(new Organizacao(form, nomeImagem));

        if(form.idPessoaResponsavel() != null) {
            Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.salvarPorOrganizacao(organizacao, form.idPessoaResponsavel());
            logger.info("Cadastro de organização finalizado!");
            return new OrganizacaoDto(organizacao, getImagemNotNull(organizacao.getNomeImagem()), pessoaOrganizacaoSet);
        }

        logger.info("Cadastro de organização finalizado!");
        return new OrganizacaoDto(organizacao, getImagemNotNull(organizacao.getNomeImagem()));
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluir organização {}.", id);
        Organizacao organizacao = buscarPorId(id);
        imagemPerfilService.apagar(organizacao.getNomeImagem());
        organizacao.apagar();
        repository.saveAndFlush(organizacao);
        repository.deleteById(organizacao.getId());

        pessoaOrganizacaoService.excluirPorOrganizacao(organizacao);

        logger.info("Exclusão da organização com id {} finalizada!", id);
    }

    @Transactional
    public OrganizacaoDto atualizar(Long id, OrganizacaoForm form) throws IOException {
        logger.info("Atualizar organização de id {}: {}", id, form);
        validarOrganizacao(form, false);
        Organizacao organizacao = buscarPorId(id);
        organizacao.atualizar(form);
        if (form.imagemPerfil() != null)
            organizacao.atualizarImagemPerfil(imagemPerfilService.atualizar(organizacao.getNomeImagem(), form.imagemPerfil()));
        Organizacao organizacaoResult = repository.save(organizacao);

        Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.atualizarPorOrganizacao(organizacaoResult, form.idPessoaResponsavel());

        logger.info("Atualização da organização {} finalizada!", id);
        return new OrganizacaoDto(organizacao, getImagemNotNull(organizacao.getNomeImagem()), pessoaOrganizacaoSet);
    }

    public OrganizacaoDto buscar(Long id) throws IOException {
        logger.info("Buscar pessoa {}.", id);
        Organizacao organizacao = buscarPorId(id);
        Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorOrganizacao(organizacao);
        return new OrganizacaoDto(organizacao, getImagemNotNull(organizacao.getNomeImagem()), pessoaOrganizacaoSet);
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

    public Organizacao buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new OrganizacaoNaoEncontradaException(id));
    }

    private void validarOrganizacao(OrganizacaoForm form, boolean isSalvar) {
        List<String> erros = new ArrayList<>();
        if (form.idCidade() != null && !cidadeService.existePorId(form.idCidade()))
            erros.add("Erro ao encontrar cidade com id " + form.idCidade());

        if (!paisService.existePorId(form.idPais()))
            erros.add("Erro ao encontrar país com id " + form.idPais());

        if (form.idOrganizacaoPai() != null && !repository.existsById(form.idOrganizacaoPai()))
            erros.add("Erro ao encontrar organização pai com id " + form.idOrganizacaoPai());

        if (form.idPessoaResponsavel() != null && !pessoaService.existePorId(form.idPessoaResponsavel()))
            erros.add("Erro ao encontrar pessoa responsável com id " + form.idPessoaResponsavel());

        if (!tipoOrganizacaoService.existePorId(form.idTipoOrganizacao()))
            erros.add("Erro ao encontrar tipo de organização com id " + form.idTipoOrganizacao());

        if(form.cnpj() != null && repository.existsByCnpj(form.cnpj()) && isSalvar)
            erros.add("Já existe uma organização cadastrada com esse CNPJ.");

        if (!erros.isEmpty()) {
            erros.forEach(logger::warn);
            throw new ValidacaoSiscapException(erros);
        }
    }

}
