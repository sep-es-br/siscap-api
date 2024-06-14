package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.ProjetoPessoaSelectDto;
import br.gov.es.siscap.dto.listagem.ProjetoListaDto;
import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.form.ProjetoFormUpdate;
import br.gov.es.siscap.form.ProjetoPessoaFormUpdate;
import br.gov.es.siscap.form.interfaces.IProjetoForm;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

    private final ProjetoRepository repository;
    private final OrganizacaoService organizacaoService;
    private final MicrorregiaoService microrregiaoService;
    private final ProjetoPessoaService projetoPessoaService;
    private final Logger logger = LogManager.getLogger(ProjetoService.class);

    @Transactional
    public ProjetoDto salvar(ProjetoForm form) {
        logger.info("Cadatrar novo projeto: {}.", form);
        validarProjeto(form);
        Projeto projeto = repository.save(new Projeto(form));
        salvarProjetoPessoa(form.equipeElab(), projeto);
        logger.info("Cadastro de projeto finalizado!");
        return new ProjetoDto(projeto);
    }

    public Page<ProjetoListaDto> listarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(ProjetoListaDto::new);
    }

    @Transactional
    public ProjetoDto atualizar(Long id, ProjetoFormUpdate form) {
        logger.info("Atualizar projeto de id {}: {}.", id, form);
        validarProjeto(form);
        Projeto projeto = buscarPorId(id);
        projetoPessoaService.excluirAoAlterar(form.equipeElab()
                        .stream().map(ProjetoPessoaFormUpdate::id).collect(Collectors.toSet()), projeto.getId());
        salvarProjetoPessoa(form.equipeElab(), projeto);
        projeto.atualizarProjeto(form);
        repository.save(projeto);
        return new ProjetoDto(projeto);
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluir projeto {}.", id);
        Projeto projeto = buscarPorId(id);
        projeto.apagar();
        repository.saveAndFlush(projeto);
        repository.deleteById(id);
        logger.info("Exclusão do projeto com id {} finalizada!", id);
    }

    public ProjetoDto buscar(Long id) {
        return new ProjetoDto(buscarPorId(id));
    }

    public String gerarNomeArquivo(Integer idProjeto) {
        Projeto projeto = buscarPorId(Long.valueOf(idProjeto));

        String cnpj = formatarCnpj(projeto.getOrganizacao().getCnpj());

        return "PROJETO n. " +
                projeto.getId() + "/" +
                projeto.getCriadoEm().getYear() + "-" +
                projeto.getOrganizacao().getNomeFantasia() + "-" +
                cnpj;
    }

    public int buscarQuantidadeProjetos() {
        return Integer.parseInt(String.valueOf((repository.count())));
    }

    public BigDecimal buscarSomatorioValorEstimadoProjetos() {
        return repository.somarValorEstimadoTodosProjetos();
    }

    public List<ProjetoPessoaSelectDto> buscarSelectProjetoPessoa() {
        return projetoPessoaService.buscarSelect();
    }

    private Projeto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
    }

    private void validarProjeto(IProjetoForm form) {
        List<String> erros = new ArrayList<>();

        if (!organizacaoService.existePorId(form.idOrganizacao())) {
            erros.add("Erro ao encontrar Organização com id " + form.idOrganizacao());
        }
        form.idMicrorregioes().forEach(id -> {
            if (!microrregiaoService.existePorId(id))
                erros.add("Erro ao encontrar Microrregião com id " + id);
        });

        if (form instanceof ProjetoForm projetoForm && repository.existsBySigla(projetoForm.sigla()))
            erros.add("Já existe um projeto cadastrado com essa sigla.");

        if (!erros.isEmpty()) {
            erros.forEach(logger::error);
            throw new ValidacaoSiscapException(erros);
        }
    }

    private String formatarCnpj(String cnpj) {
        return cnpj.replaceAll("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})$", "$1.$2.$3/$4-$5");
    }

    private <T> void salvarProjetoPessoa(List<T> forms, Projeto projeto) {
        forms.forEach(projetoPessoa -> projetoPessoaService.salvar(projetoPessoa, projeto.getId()));
    }

}
