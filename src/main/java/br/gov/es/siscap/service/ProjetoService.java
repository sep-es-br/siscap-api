package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.exception.ProjetoNaoEncontradoException;
import br.gov.es.siscap.exception.SisCapServiceException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.form.ProjetoUpdateForm;
import br.gov.es.siscap.models.Entidade;
import br.gov.es.siscap.models.Microrregiao;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoService {

    private final ProjetoRepository repository;
    private final EntidadeService entidadeService;
    private final MicrorregiaoService microrregiaoService;

    @Transactional
    public ProjetoDto salvar(ProjetoForm form) {
        List<String> erros = new ArrayList<>();
        if (!entidadeService.existePorId(form.idEntidade())) {
            erros.add("Erro ao encontrar entidade com id " + form.idEntidade());
        }
        for (Long id : form.idMicrorregioes()) {
            if (!microrregiaoService.existePorId(id))
                erros.add("Erro ao encontrar microrregião com id " + id);
        }
        if (!erros.isEmpty())
            throw new SisCapServiceException(erros);
        Projeto projeto = repository.save(new Projeto(form));
        return new ProjetoDto(projeto);
    }

    public Page<ProjetoDto> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable).map(ProjetoDto::new);
    }

    @Transactional
    public ProjetoDto atualizar(Long id, ProjetoUpdateForm form) {
        Projeto projeto = buscarPorId(id);
        atualizarProjeto(projeto, form);
        return new ProjetoDto(projeto);
    }

    @Transactional
    public void excluir(Long id) {
        Projeto projeto = buscarPorId(id);
        repository.deleteById(id);
        projeto.setAtualizadoEm(LocalDateTime.now());
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
                    .stream().map(Microrregiao::new).collect(Collectors.toList()));
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
