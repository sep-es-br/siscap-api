package br.gov.es.siscap.models;

import br.gov.es.siscap.form.PessoaForm;
import br.gov.es.siscap.form.PessoaUpdateForm;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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
    private String nome;
    private String nomeSocial;
    private String nacionalidade;
    private String genero;
    private String cpf;
    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.REMOVE})
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;
    private String caminhoImagem;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Pessoa(PessoaForm form, String caminhoImagem) {
        this.nome = form.nome();
        this.nomeSocial = form.nomeSocial();
        this.nacionalidade = form.nacionalidade();
        this.genero = form.genero();
        this.cpf = form.cpf();
        this.endereco = form.endereco() != null ? new Endereco(form.endereco()) : null;
        this.caminhoImagem = caminhoImagem;
        this.criadoEm = LocalDateTime.now();
    }

    public void atualizarPessoa(PessoaUpdateForm form) {
        if (form.nome() != null)
            this.nome = form.nome();
        if (form.nomeSocial() != null)
            this.nomeSocial = form.nomeSocial();
        if (form.nacionalidade() != null)
            this.nacionalidade = form.nacionalidade();
        if (form.genero() != null)
            this.genero = form.genero();
        if (form.cpf() != null)
            this.cpf = form.cpf();
        if (form.endereco() != null) {
            if (this.endereco != null)
                this.endereco.atualizarEndereco(form.endereco());
            else
                this.endereco = new Endereco(form.endereco());
        }
        this.setAtualizadoEm(LocalDateTime.now());
    }

    public void atualizarImagemPerfil(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }
}