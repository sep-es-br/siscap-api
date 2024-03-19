package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.ProjetoListaDto;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.exception.service.ServiceSisCapException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.form.ProjetoUpdateForm;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            erros.add("Erro ao encontrar Entidade com id " + form.idEntidade());
        }
        form.idMicrorregioes().forEach(id -> {
            if (!microrregiaoService.existePorId(id))
                erros.add("Erro ao encontrar Microrregião com id " + id);
        });

        if (!erros.isEmpty()) {
            erros.forEach(logger::error);
            throw new ServiceSisCapException(erros);
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

    private Projeto buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProjetoNaoEncontradoException(id));
    }

}
