package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.PessoaDto;
import br.gov.es.siscap.form.EnderecoForm;
import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.form.PessoaUpdateForm;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.repository.PessoaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PessoaServiceTest {

    @InjectMocks
    private PessoaService service;
    @Mock
    private PessoaRepository repository;
    @Mock
    private ImagemPerfilService imagemPerfilService;
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
    @DisplayName("Deve salvar uma pessoa corretamente")
    void salvar() {
        PessoaForm pessoaForm = getForm();
        Pessoa pessoa = getEntidade();

        when(repository.save(any(Pessoa.class))).thenReturn(pessoa);

        assertThat(service.salvar(pessoaForm)).isEqualTo(new PessoaDto(pessoa));
        verify(imagemPerfilService, times(1)).salvar(any(MultipartFile.class));
    }

    @Test
    @DisplayName("Deve buscar uma pessoa corretamente")
    void buscar() {
        Pessoa pessoa = getEntidade();
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));

        assertThat(service.buscar(1L)).isEqualTo(new PessoaDto(pessoa));
    }

    @Test
    @DisplayName("Deve chamar a busca para imagem de perfil")
    void buscarImagemPerfil() {
        Pessoa pessoa = getEntidade();
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));
        service.buscarImagemPerfil(1L);
        verify(imagemPerfilService, times(1)).buscar(pessoa.getNomeImagem());
    }

    @Test
    @DisplayName("Deve atualizar uma pessoa corretamente")
    void atualizar() {
        PessoaUpdateForm form = getUpdateForm();
        when(repository.findById(1L)).thenReturn(Optional.of(getEntidade()));
        when(imagemPerfilService.salvar(form.imagemPerfil())).thenReturn(form.imagemPerfil().getName());

        assertThat(service.atualizar(1L, form)).isEqualTo(new PessoaDto(getEntidade()));
        verify(imagemPerfilService, times(1)).apagar(any(String.class));
        verify(imagemPerfilService, times(1)).salvar(any(MultipartFile.class));
    }

    @Test
    @DisplayName("Deve excluir uma pessoa corretamente")
    void excluir() {
        Pessoa pessoa = getEntidade();
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));

        service.excluir(1L);
        verify(repository, times(1)).deleteById(1L);
        verify(imagemPerfilService, times(1)).apagar(pessoa.getNomeImagem());
    }

    private EnderecoForm getEnderecoForm() {
        return new EnderecoForm("Rua Da Frente", 182, "Centro", "Casa",
                "00000000", 1L);
    }

    private PessoaForm getForm() {
        return new PessoaForm("Batata com Cheddar e Bacon", "Batatinha", "Brasileiro",
                "Masculino", "12312312312", "batata@mail.com",
                "", "", getEnderecoForm(),
                new MockMultipartFile("batata.jpg", "batata".getBytes()));
    }

    private PessoaUpdateForm getUpdateForm() {
        return new PessoaUpdateForm("Batata com Cheddar e Bacon", "Batatinha", "Brasileiro",
                "Masculino", "12312312312", "batata@mail.com",
                "", "", getEnderecoForm(),
                new MockMultipartFile("batata.jpg", "batata".getBytes()));
    }

    private Pessoa getEntidade() {
        return new Pessoa(getForm(), "batata.jpg");
    }

}