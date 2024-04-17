package br.gov.es.siscap.models;

import br.gov.es.siscap.form.PessoaForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "pessoa")
@Getter
@SQLDelete(sql = "update pessoa set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
@NoArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String nome;
    private String nomeSocial;
    private String nacionalidade;
    private String genero;
    private String cpf;
    @Setter
    private String email;
    private String telefoneComercial;
    private String telefonePessoal;
    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.REMOVE})
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;
    private String nomeImagem;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pessoa_area_atuacao",
            joinColumns = {@JoinColumn(name = "id_pessoa")},
            inverseJoinColumns = @JoinColumn(name = "id_area_atuacao"))
    private Set<AreaAtuacao> areasAtuacao;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Pessoa(PessoaForm form, String nomeImagem) {
        this.nome = form.nome();
        this.nomeSocial = form.nomeSocial();
        this.nacionalidade = form.nacionalidade();
        this.genero = form.genero();
        this.cpf = form.cpf();
        this.email = form.email();
        this.telefoneComercial = form.telefoneComercial();
        this.telefonePessoal = form.telefonePessoal();
        this.endereco = form.endereco() != null ? new Endereco(form.endereco()) : null;
        this.areasAtuacao = form.idAreasAtuacao() != null ?
                form.idAreasAtuacao().stream().map(AreaAtuacao::new).collect(Collectors.toSet()) : null;
        this.nomeImagem = nomeImagem;
        this.criadoEm = LocalDateTime.now();
    }

    public Pessoa(Long id) {
        this.id = id;
    }

    public void atualizar(PessoaForm form) {
        this.nome = form.nome();
        this.nomeSocial = form.nomeSocial();
        this.nacionalidade = form.nacionalidade();
        this.genero = form.genero();
        this.cpf = form.cpf();
        this.email = form.email();
        this.telefoneComercial = form.telefoneComercial();
        this.telefonePessoal = form.telefonePessoal();
        if (this.endereco != null)
            this.endereco.atualizarEndereco(form.endereco());
        else
            this.endereco = new Endereco(form.endereco());
        this.areasAtuacao = form.idAreasAtuacao().stream().map(AreaAtuacao::new).collect(Collectors.toSet());
        this.setAtualizadoEm(LocalDateTime.now());
    }

    public void atualizarImagemPerfil(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }

    public void apagar() {
        this.cpf = null;
        this.atualizadoEm = LocalDateTime.now();
    }
}
