package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.ProjetoListaDto;
import br.gov.es.siscap.exception.ProjetoNaoEncontradoException;
import br.gov.es.siscap.exception.SisCapServiceException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.form.ProjetoUpdateForm;
import br.gov.es.siscap.models.Entidade;
import br.gov.es.siscap.models.Microrregiao;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

    private final ProjetoRepository repository;
    private final EntidadeService entidadeService;
    private final MicrorregiaoService microrregiaoService;
    private final Logger logger = LogManager.getLogger(ProjetoService.class);

    @Transactional
    public ProjetoDto salvar(ProjetoForm form) {
        logger.info("Cadatrar novo projeto: {}.", form);
        List<String> erros = new ArrayList<>();
        if (!entidadeService.existePorId(form.idEntidade())) {
            erros.add("Erro ao encontrar entidade com id " + form.idEntidade());
        }
        for (Long id : form.idMicrorregioes()) {
            if (!microrregiaoService.existePorId(id))
                erros.add("Erro ao encontrar microrregião com id " + id);
        }
        if (!erros.isEmpty()) {
            erros.forEach(logger::warn);
            throw new SisCapServiceException(erros);
        }
        Projeto projeto = repository.save(new Projeto(form));
        logger.info("Cadastro de projeto finalizado!");
        return new ProjetoDto(projeto);
    }

    public Page<ProjetoListaDto> listarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(ProjetoListaDto::new);
    }

    @Transactional
    public ProjetoDto atualizar(Long id, ProjetoUpdateForm form) {
        logger.info("Atualizar projeto de id {}: {}.", id, form);
        Projeto projeto = buscarPorId(id);
        atualizarProjeto(projeto, form);
        return new ProjetoDto(projeto);
    }

    @Transactional
    public void excluir(Long id) {
        logger.info("Excluir projeto {}.", id);
        Projeto projeto = buscarPorId(id);
        repository.deleteById(id);
        projeto.setAtualizadoEm(LocalDateTime.now());
        logger.info("Exclusão do projeto com id {} finalizada!", id);
    }

    public ProjetoDto buscar(Long id) {
        return new ProjetoDto(buscarPorId(id));
    }

    private Projeto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
    }

    private void atualizarProjeto(Projeto projeto, ProjetoUpdateForm form) {
        if (form.sigla() != null)
            projeto.setSigla(form.sigla());
        if (form.titulo() != null)
            projeto.setTitulo(form.titulo());
        if (form.idEntidade() != null)
            projeto.setEntidade(new Entidade(form.idEntidade()));
        if (form.valorEstimado() != null)
            projeto.setValorEstimado(form.valorEstimado());
        if (form.idMicrorregioes() != null && !form.idMicrorregioes().isEmpty())
            projeto.setMicrorregioes(form.idMicrorregioes()
                    .stream().map(Microrregiao::new).toList());
        if (form.objetivo() != null)
            projeto.setObjetivo(form.objetivo());
        if (form.objetivoEspecifico() != null)
            projeto.setObjetivoEspecifico(form.objetivoEspecifico());
        if (form.situacaoProblema() != null)
            projeto.setSituacaoProblema(form.situacaoProblema());
        if (form.solucoesPropostas() != null)
            projeto.setSolucoesPropostas(form.solucoesPropostas());
        if (form.impactos() != null)
            projeto.setImpactos(form.impactos());
        if (form.arranjosInstitucionais() != null)
            projeto.setArranjosInstitucionais(form.arranjosInstitucionais());
        projeto.setAtualizadoEm(LocalDateTime.now());
    }

}
