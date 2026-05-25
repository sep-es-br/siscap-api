package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ods_indicador_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update ods_indicador_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class OdsIndicadorExterno extends ControleHistorico {

    public OdsIndicadorExterno(Integer idOdsIndicadorExterno) {
        this.id = idOdsIndicadorExterno;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ods_indicador_externo_id_gen")
    @SequenceGenerator(name = "ods_indicador_externo_id_gen", sequenceName = "ods_indicador_externo_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_indicador_externo", nullable = false)
    private IndicadorExterno indicadorExterno;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ods_externo", nullable = false)
    private OdsExterno odsExterno;

}
