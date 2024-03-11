package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.EntidadeDto;
import br.gov.es.siscap.form.EntidadeForm;
import br.gov.es.siscap.form.EntidadeUpdateForm;
import br.gov.es.siscap.models.Entidade;
import br.gov.es.siscap.repository.EntidadeRepository;
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

class EntidadeServiceTest {

    @Mock
    private EntidadeRepository repository;
    @Mock
    private CidadeService cidadeService;
    @Mock
    private PessoaService pessoaService;
    @Mock
    private PaisService paisService;
    @Mock
    private TipoEntidadeService tipoEntidadeService;
    @Mock
    private ImagemPerfilService imagemPerfilService;
    @InjectMocks
    private EntidadeService service;

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
    @DisplayName("Deve salvar uma entidade corretamente sem imagem de perfil")
    void salvar() throws IOException {
        EntidadeForm form = getForm();
        Entidade entidade = getEntidade();

        when(repository.save(any(Entidade.class))).thenReturn(entidade);
        when(tipoEntidadeService.existePorId(1L)).thenReturn(true);
        when(paisService.existePorId(1L)).thenReturn(true);

        assertThat(service.salvar(form)).isEqualTo(new EntidadeDto(entidade, null));
        verify(repository, times(1)).save(any(Entidade.class));
        verify(imagemPerfilService, times(0)).salvar(any());
    }

    @Test
    @DisplayName("Deve buscar uma entidade corretamente")
    void buscar() throws IOException {
        Entidade entidade = getEntidade();
        when(repository.findById(1L)).thenReturn(Optional.of(entidade));

        assertThat(service.buscar(1L)).isEqualTo(new EntidadeDto(entidade, null));
    }

    @Test
    @DisplayName("Deve excluir corretamente")
    void excluir() {
        Entidade entidade = new Entidade(1L);
        entidade.atualizarImagemPerfil("batata.jpg");

        when(repository.findById(1L)).thenReturn(Optional.of(entidade));

        service.excluir(1L);

        verify(repository, times(1)).saveAndFlush(entidade);
        verify(repository, times(1)).deleteById(entidade.getId());
        verify(imagemPerfilService, times(1)).apagar("batata.jpg");
    }

    @Test
    @DisplayName("Deve atualizar uma entidade corretamente")
    void atualizar() throws IOException {
        EntidadeUpdateForm form = getUpdateForm();
        Entidade entidade = getEntidade();

        when(repository.findById(1L)).thenReturn(Optional.of(entidade));

        assertThat(service.atualizar(1L, form)).isEqualTo(new EntidadeDto(entidade, null));
        verify(imagemPerfilService, times(1)).atualizar(entidade.getNomeImagem(), form.imagemPerfil());
        verify(repository, times(1)).save(entidade);
    }

    private Entidade getEntidade() {
        return new Entidade(getForm(), null);
    }

    private Entidade getEntidadeComImagem() {
        EntidadeForm formComImagem = getFormComImagem();
        return new Entidade(formComImagem, formComImagem.imagemPerfil().getName());
    }

    private EntidadeForm getForm() {
        return new EntidadeForm("Secretaria de Economia e Planejamento", "SEP", "27779999999",
                "12341234000112", "27779999999", "123@email.com", null, null, null,
                null, null, 1L, 1L);
    }

    private EntidadeForm getFormComImagem() {
        return new EntidadeForm("Secretaria de Economia e Planejamento", "SEP", "27779999999",
                "12341234000112", "27779999999", "123@email.com", null,
                getMockMultipartFile(), null,
                null, null, 1L, 1L);
    }

    private EntidadeUpdateForm getUpdateForm() {
        return new EntidadeUpdateForm("Secretaria de Economia e Planejamento", "SEP", "27779999999",
                "12341234000112", "27779999999", "123@email.com", null,
                getMockMultipartFile(), null,
                null, null, 1L, 1L);
    }

    private static MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile("batata.jpg", "batata".getBytes());
    }

}