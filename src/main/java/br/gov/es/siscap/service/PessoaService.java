package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.dto.SelectDto;
import br.gov.es.siscap.dto.acessocidadaoapi.AgentePublicoACDto;
import br.gov.es.siscap.dto.listagem.PessoaListaDto;
import br.gov.es.siscap.exception.OrganizacaoSemResponsavelException;
import br.gov.es.siscap.exception.UsuarioSemAutorizacaoException;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.PessoaNaoEncontradoException;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.form.PessoaFormUpdate;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.PessoaOrganizacao;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PessoaService {

    private final PessoaRepository repository;
    private final ImagemPerfilService imagemPerfilService;
    private final UsuarioService usuarioService;
    private final AcessoCidadaoService acessoCidadaoService;
    private final PessoaOrganizacaoService pessoaOrganizacaoService;
    private OrganizacaoService organizacaoService;
    private final Logger logger = LogManager.getLogger(PessoaService.class);

    @Autowired
    protected void setPessoaService(@Lazy OrganizacaoService organizacaoService) {
        this.organizacaoService = organizacaoService;
    }

    @Transactional
    public PessoaDto salvar(PessoaForm form) throws IOException {
        logger.info("Cadatrar nova pessoa: {}.", form);
        validarPessoa(form);
        String nomeImagem = imagemPerfilService.salvar(form.imagemPerfil());
        logger.info("Imagem de perfil para nova pessoa salva: {}.", form.imagemPerfil());
        Pessoa pessoa = repository.save(new Pessoa(form, nomeImagem));
        logger.info("Cadastro de nova pessoa finalizado com sucesso!");
        return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
    }

    public PessoaDto buscar(Long id) throws IOException {
        logger.info("Buscar pessoa com id [{}]", id);
        Pessoa pessoa = buscarPorId(id);
        Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorPessoa(pessoa);
        return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
    }

    public Page<PessoaListaDto> listarTodos(Pageable pageable) {
        logger.info("Listar todas pessoas");
        return repository.findAll(pageable).map(pessoa -> {
            try {
                return new PessoaListaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
            } catch (IOException e) {
                throw new SiscapServiceException(Collections.singletonList(e.getMessage()));
            }
        });
    }

    public SelectDto buscarResponsavelPorIdOrganizacao(Long orgId) {
        Set<PessoaOrganizacao> pessoaOrganizacaoSet = pessoaOrganizacaoService.buscarPorIdOrganizacao(orgId);

        Pessoa responsavel = pessoaOrganizacaoService.buscarResponsavelOrganizacao(pessoaOrganizacaoSet);

        if(responsavel != null) {
            return new SelectDto(responsavel);
        } else {
            throw new OrganizacaoSemResponsavelException();
        }

//        PessoaOrganizacao pessoaOrganizacao = this.organizacaoService.buscarPorId(orgId).buscarResponsavel();
//
//        if (pessoaOrganizacao != null && pessoaOrganizacao.getResponsavel()) {
//            return new SelectDto(pessoaOrganizacao.getPessoa());
//        } else {
//            throw new OrganizacaoSemResponsavelException();
//        }
    }

    @Transactional
    public PessoaDto atualizar(Long id, PessoaFormUpdate form, Authentication auth) throws IOException {
        if (auth != null && !buscarPorId(id).getSub().equals(((Usuario) auth.getPrincipal()).getSub())) {
            throw new UsuarioSemAutorizacaoException();
        }
        logger.info("Atualizar pessoa de id {}: {}.", id, form);
        Pessoa pessoa = buscarPorId(id);
        pessoa.atualizar(form);
        if (form.imagemPerfil() != null)
            pessoa.atualizarImagemPerfil(imagemPerfilService.atualizar(pessoa.getNomeImagem(), form.imagemPerfil()));
        else {
            imagemPerfilService.apagar(pessoa.getNomeImagem());
            pessoa.atualizarImagemPerfil(null);
        }
        repository.save(pessoa);
        logger.info("Atualização de pessoa finalizado com sucesso!");
        return new PessoaDto(pessoa, getImagemNotNull(pessoa.getNomeImagem()));
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluir pessoa {}.", id);
        Pessoa pessoa = buscarPorId(id);
        imagemPerfilService.apagar(pessoa.getNomeImagem());
        pessoa.apagar();
        usuarioService.excluirPorPessoa(pessoa.getId());
        repository.saveAndFlush(pessoa);
        repository.deleteById(id);
        logger.info("Exclusão de pessoa com id {} finalizada com sucesso!", id);
    }

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    public List<SelectDto> buscarSelect() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "nome")).stream().map(SelectDto::new).toList();
    }

    public Pessoa buscarPorSub(String sub) {
        return repository.findBySub(sub).orElseThrow(() -> new PessoaNaoEncontradoException(sub));
    }

    @Transactional
    public Pessoa salvarNovaPessoaAcessoCidadao(Pessoa pessoa) {
        return repository.save(pessoa);
    }

    public AgentePublicoACDto buscarPessoaNoAcessoCidadaoPorCpf(String cpf) {
        return acessoCidadaoService.buscarPessoaPorCpf(cpf);
    }

    @Transactional
    public void validarSub(String sub, Long idPessoa) {
        Pessoa pessoa = buscarPorId(idPessoa);
        if (pessoa.getSub() == null) {
            pessoa.setSub(sub);
            repository.saveAndFlush(pessoa);
        } else if (!pessoa.getSub().equals(sub))
            throw new SiscapServiceException(Collections.singletonList("Falha na integridade do Sub do usuário"));
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

    private void validarPessoa(PessoaForm form) {
        List<String> erros = new ArrayList<>();
        if (repository.existsByEmail(form.email()))
            erros.add("Já existe uma pessoa cadastrada com esse email.");

        if (form.cpf() != null && repository.existsByCpf(form.cpf()))
            erros.add("Já existe uma pessoa cadastrada com esse cpf.");

        if (form.idOrganizacao() != null && !organizacaoService.existePorId(form.idOrganizacao()))
            erros.add("Erro ao encontrar organização com o id " + form.idOrganizacao());

        if (!erros.isEmpty()) {
            erros.forEach(logger::error);
            throw new ValidacaoSiscapException(erros);
        }
    }

}
