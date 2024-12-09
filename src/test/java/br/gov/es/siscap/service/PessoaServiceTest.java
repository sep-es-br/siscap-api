//package br.gov.es.siscap.service;
//
//import br.gov.es.siscap.dto.PessoaDto;
//import br.gov.es.siscap.form.EnderecoForm;
//import br.gov.es.siscap.form.PessoaForm;
//import br.gov.es.siscap.form.PessoaFormUpdate;
//import br.gov.es.siscap.models.Pessoa;
//import br.gov.es.siscap.repository.PessoaRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class PessoaServiceTest {
//
//    @InjectMocks
//    private PessoaService service;
//    @Mock
//    private PessoaRepository repository;
//    @Mock
//    private ImagemPerfilService imagemPerfilService;
//    @Mock
//    private UsuarioService usuarioService;
////    @Mock
////    private OrganizacaoService organizacaoService;
//    private AutoCloseable closeable;
//
//    @BeforeEach
//    void setUp() {
//        closeable = MockitoAnnotations.openMocks(this);
////        service.setPessoaService(organizacaoService);
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        closeable.close();
//    }
//
//    @Test
//    @DisplayName("Deve salvar uma pessoa corretamente")
//    void salvar() throws IOException {
//        PessoaForm pessoaForm = getForm();
//        Pessoa pessoa = getPessoa();
//
//        when(repository.save(any(Pessoa.class))).thenReturn(pessoa);
////        when(organizacaoService.existePorId(pessoaForm.idOrganizacao())).thenReturn(true);
//
//        assertThat(service.salvar(pessoaForm)).isEqualTo(new PessoaDto(pessoa, null));
//        verify(imagemPerfilService, times(1)).salvar(any(MultipartFile.class));
//    }
//
//    @Test
//    @DisplayName("Deve buscar uma pessoa corretamente")
//    void buscar() throws IOException {
//        Pessoa pessoa = getPessoa();
//        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));
//
//        assertThat(service.buscar(1L)).isEqualTo(new PessoaDto(pessoa, null));
//    }
//
//    @Test
//    @DisplayName("Deve atualizar uma pessoa corretamente")
//    void atualizar() throws IOException {
//        PessoaFormUpdate form = getFormParaUpdate();
//        Pessoa pessoa = getPessoa();
//        pessoa.atualizarImagemPerfil("batata.jpg");
//
//        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));
//        when(imagemPerfilService.atualizar(pessoa.getNomeImagem(), form.imagemPerfil())).thenReturn(form.imagemPerfil().getName());
//
//        assertThat(service.atualizar(1L, form, null)).isEqualTo(new PessoaDto(pessoa, null));
//        verify(imagemPerfilService, times(1)).atualizar(pessoa.getNomeImagem(), form.imagemPerfil());
//    }
//
//    @Test
//    @DisplayName("Deve excluir uma pessoa corretamente")
//    void excluir() {
//        Pessoa entidade = getPessoa();
//        when(repository.findById(1L)).thenReturn(Optional.of(entidade));
//
//        service.excluir(1L);
//        verify(repository, times(1)).deleteById(1L);
//        verify(imagemPerfilService, times(1)).apagar("batata.jpg");
//    }
//
//    private EnderecoForm getEnderecoForm() {
//        return new EnderecoForm("Rua Da Frente", "182", "Centro", "Casa",
//                "00000000", 1L);
//    }
//
//    private PessoaForm getForm() {
//        return new PessoaForm("Batata com Cheddar e Bacon", "Batatinha", "Brasileiro",
//                "Masculino", "12312312312", "batata@mail.com",
//                "", "", getEnderecoForm(),Set.of(1L, 2L, 3L), "182", Set.of(123L, 333L),
//                new MockMultipartFile("batata.jpg", "batata".getBytes()));
//    }
//
//    private PessoaFormUpdate getFormParaUpdate() {
//        return new PessoaFormUpdate("Batata com Cheddar e Bacon", "Batatinha", "Brasileiro",
//                "Masculino", "12312312312", "batata@mail.com",
//                "", "", getEnderecoForm(),Set.of(1L, 2L, 3L),  Set.of(123L, 333L),
//                new MockMultipartFile("batata.jpg", "batata".getBytes()));
//    }
//
//    private Pessoa getPessoa() {
//        return new Pessoa(getForm(), "batata.jpg");
//    }
//
//}