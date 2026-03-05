package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;

import br.gov.es.siscap.dto.ProgramaOrganizacaoDto;

@Entity
@Table(name = "programa_organizacao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE programa_organizacao SET apagado = true WHERE id=?")
public class ProgramaOrganizacao extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_organizacao_id_gen")
    @SequenceGenerator(name = "programa_organizacao_id_gen", sequenceName = "programa_organizacao_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_programa")
    private Programa programa;

    @ManyToOne
    @JoinColumn(name = "id_organizacao")
    private Organizacao organizacao;

    @Column(name = "tipo_organizacao", nullable = false)
    private Integer tipoOrganizacao;

    public ProgramaOrganizacao(Programa programa, Organizacao organizacao, ProgramaOrganizacaoDto dto) {
        this.programa = programa;
        this.organizacao = organizacao;
        this.tipoOrganizacao = dto.tipoOrganizacao();
    }

    public ProgramaOrganizacao(Programa programa, Organizacao organizacao, Integer tipoOrganizacao) {
        this.programa = programa;
        this.organizacao = organizacao;
        this.tipoOrganizacao = tipoOrganizacao;
    }

}
