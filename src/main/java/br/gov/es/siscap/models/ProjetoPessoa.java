package br.gov.es.siscap.models;

import br.gov.es.siscap.form.ProjetoPessoaForm;
import br.gov.es.siscap.form.ProjetoPessoaFormUpdate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLJoinTableRestriction;

@Entity
@Table(name = "projeto_pessoa")
@Getter
@NoArgsConstructor
public class ProjetoPessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "id_projeto")
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @SQLJoinTableRestriction("apagado = FALSE")
    private Projeto projeto;
    @JoinColumn(name = "id_pessoa")
    @ManyToOne(fetch = FetchType.LAZY)
    @SQLJoinTableRestriction("apagado = FALSE")
    private Pessoa pessoa;
    @JoinColumn(name = "id_papel_projeto")
    @ManyToOne(fetch = FetchType.LAZY)
    private PapelProjeto papelProjeto;

    public ProjetoPessoa(ProjetoPessoaFormUpdate form) {
        this.id = form.id();
    }

    public ProjetoPessoa(ProjetoPessoaFormUpdate form, Long idProjeto) {
        this.id = form.id();
        this.projeto = new Projeto(idProjeto);
        this.pessoa = new Pessoa(form.idPessoa());
        this.papelProjeto = new PapelProjeto(form.idPapelProjeto());
    }

    public ProjetoPessoa(ProjetoPessoaForm form, Long idProjeto) {
        this.projeto = new Projeto(idProjeto);
        this.pessoa = new Pessoa(form.idPessoa());
        this.papelProjeto = new PapelProjeto(form.idPapelProjeto());
    }

    public void atualizar(ProjetoPessoaFormUpdate form) {
        this.projeto = new Projeto(form.idProjeto());
        this.pessoa = new Pessoa(form.idPessoa());
        this.papelProjeto = new PapelProjeto(form.idPapelProjeto());
    }
}
