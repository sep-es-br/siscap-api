package br.gov.es.siscap.service;

import br.gov.es.siscap.dto.ACUserInfoDto;
import br.gov.es.siscap.enums.Permissoes;
import br.gov.es.siscap.infra.Roles;
import br.gov.es.siscap.models.Pessoa;
import br.gov.es.siscap.models.Usuario;
import br.gov.es.siscap.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static br.gov.es.siscap.enums.Permissoes.ADMIN_AUTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService service;
    @Mock
    private ImagemPerfilService imagemPerfilService;
    @Mock
    private PessoaService pessoaService;
    @Mock
    private TokenService tokenService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private Roles roles;
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
    @DisplayName("Dever retornar usuário admin.")
    void autenticar() {
        var spy = spy(service);

        var permissoes = new HashMap<String, List<Permissoes>>();
        permissoes.put("ADMIN", List.of(ADMIN_AUTH));

        var usuario = getUsuario();
        var userInfoAdmin = getUserInfoAdmin();

        doReturn(userInfoAdmin).when(spy).getUserInfo("xD");
        when(usuarioRepository.findBySub("xD-182")).thenReturn(usuario);
        when(tokenService.gerarToken((Usuario) usuario)).thenReturn("1");
        when(imagemPerfilService.buscar(((Usuario) usuario).getPessoa().getNomeImagem())).thenReturn(null);
        when(roles.getRoles()).thenReturn(permissoes);

        var usuarioDto = spy.autenticar("xD");

        assertThat(usuarioDto.permissoes()).hasSize(1);
        assertThat(usuarioDto.permissoes()).contains(ADMIN_AUTH);

        verify(spy, times(1)).getUserInfo("xD");
        verify(usuarioRepository, times(1)).findBySub("xD-182");
        verify(imagemPerfilService, times(1)).buscar(((Usuario) usuario).getPessoa().getNomeImagem());
    }

    private ACUserInfoDto getUserInfoAdmin() {
        return new ACUserInfoDto("Batata", true, true, "XYZ", "xD-182", false,
                "batata@email.com", "batata@email.com", "182", new HashSet<>(List.of("ADMIN")));
    }

    private UserDetails getUsuario() {
        return new Usuario(null, new HashSet<>(List.of("ADMIN")),
                new Pessoa(1L), "", "");
    }

}