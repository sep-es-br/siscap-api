package br.gov.es.siscap.service;

import br.gov.es.siscap.exception.ValidacaoSiscapException;
import br.gov.es.siscap.models.Localidade;
import br.gov.es.siscap.models.LocalidadeQuantia;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.ProjetoAcao;
import br.gov.es.siscap.models.ProjetoAcaoLocalidadeQuantia;
import br.gov.es.siscap.repository.ProjetoRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramaInformacoesDicsServiceTest {
    @Mock
    private ProjetoRepository repository;
    @InjectMocks
    private ProgramaInformacoesDicsService service;

    @Test
    void consolidaRegioesSemDuplicarEOrdenaPorNome() {
        Projeto primeiro = projeto(1L, "Objetivo A");
        Projeto segundo = projeto(2L, "Objetivo B");
        Localidade norte = localidade(10L, "Norte", "Microrregiao");
        Localidade central = localidade(20L, "Central", "Microrregiao");
        primeiro.setProjetoAcoesSet(Set.of(acao(norte), acao(central)));
        segundo.setProjetoAcoesSet(Set.of(acao(norte)));
        when(repository.findAllById(List.of(1L, 2L))).thenReturn(List.of(segundo, primeiro));

        var resultado = service.buscar(List.of(1L, 2L, 1L));

        assertThat(resultado.regioesAtendidas()).extracting(regiao -> regiao.id()).containsExactly(20L, 10L);
        assertThat(resultado.dics()).extracting(dic -> dic.objetivo()).containsExactly("Objetivo A", "Objetivo B");
        assertThat(primeiro.getObjetivo()).isEqualTo("Objetivo A");
    }

    @Test
    void removeSomenteRegioesAusentesDosDicsRestantes() {
        Projeto restante = projeto(2L, "Objetivo B");
        restante.setProjetoAcoesSet(Set.of(acao(localidade(10L, "Norte", "Microrregiao"))));
        when(repository.findAllById(List.of(2L))).thenReturn(List.of(restante));

        assertThat(service.buscar(List.of(2L)).regioesAtendidas())
                .extracting(regiao -> regiao.id()).containsExactly(10L);
    }

    @Test
    void usaRateioLegadoQuandoNaoHaAcoes() {
        Projeto projeto = projeto(1L, null);
        LocalidadeQuantia rateio = new LocalidadeQuantia();
        rateio.setLocalidade(localidade(1L, "Espírito Santo", "Estado"));
        projeto.setLocalidadeQuantiaSet(Set.of(rateio));
        when(repository.findAllById(List.of(1L))).thenReturn(List.of(projeto));

        assertThat(service.buscar(List.of(1L)).regioesAtendidas())
                .extracting(regiao -> regiao.nome()).containsExactly("Todo o Estado");
    }

    @Test
    void priorizaRateioPorAcaoSobreRateioLegado() {
        Projeto projeto = projeto(1L, "Objetivo");
        LocalidadeQuantia legado = new LocalidadeQuantia();
        legado.setLocalidade(localidade(1L, "Espírito Santo", "Estado"));
        projeto.setLocalidadeQuantiaSet(Set.of(legado));
        projeto.setProjetoAcoesSet(Set.of(acao(localidade(10L, "Norte", "Microrregiao"))));
        when(repository.findAllById(List.of(1L))).thenReturn(List.of(projeto));

        assertThat(service.buscar(List.of(1L)).regioesAtendidas())
                .extracting(regiao -> regiao.id()).containsExactly(10L);
    }

    @Test
    void rejeitaDicInexistente() {
        when(repository.findAllById(List.of(99L))).thenReturn(List.of());

        assertThatThrownBy(() -> service.buscar(List.of(99L))).isInstanceOf(ValidacaoSiscapException.class);
    }

    @Test
    void retornaInformacoesVaziasSemDics() {
        when(repository.findAllById(List.of())).thenReturn(List.of());

        var resultado = service.buscar(List.of());

        assertThat(resultado.dics()).isEmpty();
        assertThat(resultado.regioesAtendidas()).isEmpty();
    }

    private Projeto projeto(Long id, String objetivo) {
        Projeto projeto = new Projeto(id);
        projeto.setSigla("DIC" + id);
        projeto.setTitulo("Projeto " + id);
        projeto.setObjetivo(objetivo);
        return projeto;
    }

    private Localidade localidade(Long id, String nome, String tipo) {
        Localidade localidade = new Localidade(id);
        localidade.setNome(nome);
        localidade.setTipo(tipo);
        return localidade;
    }

    private ProjetoAcao acao(Localidade localidade) {
        ProjetoAcao acao = new ProjetoAcao();
        ProjetoAcaoLocalidadeQuantia rateio = new ProjetoAcaoLocalidadeQuantia();
        rateio.setLocalidade(localidade);
        acao.setRateios(Set.of(rateio));
        return acao;
    }
}
