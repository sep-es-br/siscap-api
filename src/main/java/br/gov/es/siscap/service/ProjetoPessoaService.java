package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoPessoaSelectDto;
import br.gov.es.siscap.exception.service.SiscapServiceException;
import br.gov.es.siscap.form.ProjetoPessoaForm;
import br.gov.es.siscap.form.ProjetoPessoaFormUpdate;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoPessoa;
import br.gov.es.siscap.repository.ProjetoPessoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjetoPessoaService {

    private final ProjetoPessoaRepository repository;
    private final PessoaService pessoaService;

    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    @Transactional
    public <T> ProjetoPessoa salvar(T form, Long idProjeto) {
        ProjetoPessoa projetoPessoa;
        if (form instanceof ProjetoPessoaFormUpdate update) {
            if (!pessoaService.existePorId(update.idPessoa())) {
                throw new SiscapServiceException(Collections.singletonList("Pessoa inexistente."));
            }
            Optional<ProjetoPessoa> optional = repository.findById(update.id());
            if (optional.isPresent()) {
                projetoPessoa = optional.get();
                projetoPessoa.atualizar(update);
            } else {
                projetoPessoa = new ProjetoPessoa(update, idProjeto);
            }
        } else {
            projetoPessoa = new ProjetoPessoa((ProjetoPessoaForm) form, idProjeto);
        }
        return repository.saveAndFlush(projetoPessoa);
    }

    /**
     * Excluir quando a pessoa foi removida do projeto.
     * @param idsProjetoPessoa
     * @param idProjeto
     */
    @Transactional
    public void excluirAoAlterar(Set<Long> idsProjetoPessoa, Long idProjeto) {
        repository.deleteByIdNotInAndProjetoIs(idsProjetoPessoa, new Projeto(idProjeto));
    }

    public List<ProjetoPessoaSelectDto> buscarSelect() {
        return repository.findAll().stream().map(ProjetoPessoaSelectDto::new).toList();
    }
}
