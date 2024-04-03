package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.OrganizacaoDto;
import br.gov.es.siscap.form.OrganizacaoForm;
import br.gov.es.siscap.form.OrganizacaoUpdateForm;
import br.gov.es.siscap.models.Organizacao;
import br.gov.es.siscap.repository.OrganizacaoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrganizacaoServiceTest {

    @Mock
    private OrganizacaoRepository repository;
    @Mock
    private PaisService paisService;
    @Mock
    private TipoOrganizacaoService tipoOrganizacaoService;
    @Mock
    private ImagemPerfilService imagemPerfilService;
    @InjectMocks
    private OrganizacaoService service;

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
    @DisplayName("Deve salvar uma organização corretamente")
    void salvar() throws IOException {
        OrganizacaoForm form = getForm();
        Organizacao organizacao = getOrganizacao();

        when(repository.save(any(Organizacao.class))).thenReturn(organizacao);
        when(tipoOrganizacaoService.existePorId(1L)).thenReturn(true);
        when(paisService.existePorId(1L)).thenReturn(true);

        assertThat(service.salvar(form)).isEqualTo(new OrganizacaoDto(organizacao, null));
        verify(repository, times(1)).save(any(Organizacao.class));
        verify(imagemPerfilService, times(1)).salvar(any());
    }

    @Test
    @DisplayName("Deve buscar uma organização corretamente")
    void buscar() throws IOException {
        Organizacao organizacao = getOrganizacao();
        when(repository.findById(1L)).thenReturn(Optional.of(organizacao));

        assertThat(service.buscar(1L)).isEqualTo(new OrganizacaoDto(organizacao, null));
    }

    @Test
    @DisplayName("Deve excluir corretamente")
    void excluir() {
        Organizacao organizacao = new Organizacao(1L);
        organizacao.atualizarImagemPerfil("batata.jpg");

        when(repository.findById(1L)).thenReturn(Optional.of(organizacao));

        service.excluir(1L);

        verify(repository, times(1)).saveAndFlush(organizacao);
        verify(repository, times(1)).deleteById(organizacao.getId());
        verify(imagemPerfilService, times(1)).apagar("batata.jpg");
    }

    @Test
    @DisplayName("Deve atualizar uma organização corretamente")
    void atualizar() throws IOException {
        OrganizacaoUpdateForm form = getUpdateForm();
        Organizacao organizacao = getOrganizacao();

        when(repository.findById(1L)).thenReturn(Optional.of(organizacao));

        assertThat(service.atualizar(1L, form)).isEqualTo(new OrganizacaoDto(organizacao, null));
        verify(imagemPerfilService, times(1)).atualizar(organizacao.getNomeImagem(), form.imagemPerfil());
        verify(repository, times(1)).save(organizacao);
    }

    private Organizacao getOrganizacao() {
        return new Organizacao(getForm(), null);
    }

    private OrganizacaoForm getForm() {
        return new OrganizacaoForm("Secretaria de Economia e Planejamento", "SEP", "27779999999",
                "12341234000112", "27779999999", "123@email.com", null, null, null,
                null, null, 1L, 1L);
    }

    private OrganizacaoUpdateForm getUpdateForm() {
        return new OrganizacaoUpdateForm("Secretaria de Economia e Planejamento", "SEP", "27779999999",
                "12341234000112", "27779999999", "123@email.com",
                getMockMultipartFile(), null,
                null, null, null, 1L, 1L);
    }

    private static MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile("batata.jpg", "batata".getBytes());
    }

}