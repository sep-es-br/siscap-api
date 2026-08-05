package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.dto.ProjetoPlanejamentoPpaLoaDto;

@Entity
@Table(name = "projeto_planejamento_ppa_loa")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update projeto_planejamento_ppa_loa set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoPlanejamentoPpaLoa extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_planejamento_ppa_loa_id_gen")
    @SequenceGenerator(name = "projeto_planejamento_ppa_loa_id_gen", sequenceName = "projeto_planejamento_ppa_loa_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @Column(name = "cod_acao", nullable = true)
    private String codAcao;

    @Column(name = "cod_funcao", nullable = true)
    private String codFuncao;

    @Column(name = "cod_programa", nullable = true)
    private String codPrograma;

    @Column(name = "ano", nullable = true)
    private String ano;

    @Column(name = "cod_uo", nullable = true)
    private String codUo;

    public ProjetoPlanejamentoPpaLoa(Projeto projeto, ProjetoPlanejamentoPpaLoaDto dto) {
        this.projeto = projeto;
        this.codFuncao = dto. codFuncao();
        this.codPrograma = dto.codPrograma();
        this.ano = dto.ano();
        this.codUo = dto.codUo();
        this.codAcao = dto.codAcao();
    }

    public ProjetoPlanejamentoPpaLoa(ProjetoPlanejamentoPpaLoaDto planejamentoDto) {
        this.id = planejamentoDto.id();
        this.codFuncao = planejamentoDto.codFuncao();   
        this.codPrograma = planejamentoDto.codPrograma();
        this.ano = planejamentoDto.ano();
        this.codUo = planejamentoDto.codUo();
        this.codAcao = planejamentoDto.codAcao();
    }

}
