package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ProjetoDto;
import br.gov.es.siscap.dto.ProjetoListaDto;
import br.gov.es.siscap.exception.naoencontrado.ProjetoNaoEncontradoException;
import br.gov.es.siscap.exception.service.ServiceSisCapException;
import br.gov.es.siscap.form.ProjetoForm;
import br.gov.es.siscap.form.ProjetoUpdateForm;
import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.repository.ProjetoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjetoServiceTest {

    @Mock
    private ProjetoRepository repository;
    @Mock
    private OrganizacaoService organizacaoService;
    @Mock
    private MicrorregiaoService microrregiaoService;
    @Mock
    private AreaService areaService;
    @InjectMocks
    private ProjetoService service;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Deve salvar um projeto corretamente")
    void salvar() {
        ProjetoForm form = getProjetoForm();
        var projeto = new Projeto(form);

        when(organizacaoService.existePorId(1L)).thenReturn(true);
        when(microrregiaoService.existePorId(1L)).thenReturn(true);
        when(areaService.existePorId(1L)).thenReturn(true);

        when(repository.save(any())).thenReturn(projeto);

        assertThat(service.salvar(form)).isEqualTo(new ProjetoDto(projeto));
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Validar todas mensagens de erro das validações de projetos")
    void salvarComErrosDeValidacao() {
        ProjetoForm form = getProjetoForm();

        when(organizacaoService.existePorId(1L)).thenReturn(false);
        when(microrregiaoService.existePorId(1L)).thenReturn(false);
        when(areaService.existePorId(1L)).thenReturn(false);

        try {
            service.salvar(form);
            fail("É preciso lançar todas exceptions de validação de projetos.");
        } catch (ServiceSisCapException e) {
            assertThat(e.getErros()).contains("Erro ao encontrar Organização com id " + 1L,
                    "Erro ao encontrar Microrregião com id " + 1L);
        }
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar")
    void salvarComErro() {
        ProjetoForm form = getProjetoForm();

        when(organizacaoService.existePorId(1L)).thenReturn(false);
        when(microrregiaoService.existePorId(1L)).thenReturn(false);
        when(areaService.existePorId(1L)).thenReturn(false);

        doThrow(new RuntimeException("Erro ao salvar no repositório")).when(repository).save(any());

        assertThrows(ServiceSisCapException.class, () -> service.salvar(form));
    }

    @Test
    @DisplayName("Deve listar corretamente")
    void listar() {
        PageRequest pageRequest = PageRequest.of(0, 15);
        when(repository.findAll(pageRequest)).thenReturn(new PageImpl<>(getProjetoList()));

        Page<ProjetoListaDto> projetos = service.listarTodos(pageRequest);

        assertThat(projetos).hasSize(20);
    }

    @Test
    @DisplayName("Deve listar corretamente")
    void listarPaginacao() {
        List<Projeto> projetos = getProjetoList();
        Pageable pageable = PageRequest.of(1, 10);
        Page<Projeto> projetoPage = new PageImpl<>(projetos, pageable, projetos.size());
        when(repository.findAll(any(Pageable.class))).thenReturn(projetoPage);

        Page<ProjetoListaDto> retorno = service.listarTodos(pageable);

        assertThat(retorno.getTotalPages()).isEqualTo(2);
        assertThat(retorno.getTotalElements()).isEqualTo(20);
    }

    @Test
    @DisplayName("Deve retornar um page vazio ao nao encontrar nenhum projeto")
    void listarVazio() {
        Pageable pageable = PageRequest.of(1, 10);
        when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<ProjetoListaDto> retorno = service.listarTodos(pageable);

        assertThat(retorno).isEmpty();
    }

    @Test
    @DisplayName("Deve excluir um projeto corretamente")
    void excluir() {
        var projeto = new Projeto(getProjetoForm());
        when(repository.findById(1L)).thenReturn(Optional.of(projeto));

        service.excluir(1L);

        assertThat(projeto.getSigla()).isNull();
        verify(repository, times(1)).saveAndFlush(projeto);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao não conseguir encontrar o projeto")
    void excluirNaoEncontrarProjeto() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjetoNaoEncontradoException.class, () -> service.excluir(1L));
        verify(repository, never()).deleteById(1L);
    }

    @Test
    @DisplayName("Deve atualizar um projeto corretamente")
    void atualizar() {
        var projeto = new Projeto(getProjetoForm());

        when(repository.findById(1L)).thenReturn(Optional.of(projeto));

        ProjetoDto dto = service.atualizar(1L, getProjetoUpdateForm());

        verify(repository, times(1)).save(projeto);
        assertThat(dto.sigla()).isEqualTo("SISCAPA");
    }

    @Test
    @DisplayName("Deve lançar erro ao não encontrar projeto")
    void atualizarNaoEncontrarProjeto() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        try{
            service.atualizar(1L, getProjetoUpdateForm());
        } catch (RuntimeException e) {
            assertThat(e).isInstanceOf(ProjetoNaoEncontradoException.class);
        }
        verify(repository, never()).save(any(Projeto.class));
    }

    private ProjetoForm getProjetoForm() {
        return new ProjetoForm("SISCAP", "Sis Cap", 1L, new BigDecimal(182),
                List.of(1L), "siscap", "siscap", "siscap", "siscap",
                "siscap", "siscap", List.of(1L));
    }

    private ProjetoUpdateForm getProjetoUpdateForm() {
        return new ProjetoUpdateForm("SISCAPA", "Sis Cap ATUALIZADO", 2L, new BigDecimal(182),
                List.of(1L), "siscap ATUALIZADO", "siscap ATUALIZADO",
                "siscap ATUALIZADO", "siscap ATUALIZADO",
                "siscap ATUALIZADO", "siscap ATUALIZADO", List.of(2L));
    }

    private List<Projeto> getProjetoList() {
        List<Projeto> projetos = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            projetos.add(new Projeto(getProjetoForm()));
        return projetos;
    }

}
