package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import br.gov.es.siscap.dto.ProjetoOdsDto;

@Entity
@Table(name = "projeto_ods")
@NoArgsConstructor
@Getter
@Setter
public class ProjetoOds extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_ods_id_gen")
    @SequenceGenerator(
        name = "projeto_ods_id_gen",
        sequenceName = "projeto_ods_id_seq",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @Column(name = "id_ods", nullable = false)
    private Integer idOds;

    public ProjetoOds(Projeto projeto, ProjetoOdsDto dto) {
        this.projeto = projeto;
        this.idOds = dto.odsId();
    }
}
