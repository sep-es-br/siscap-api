package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ods_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update ods_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class OdsExterno extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ods_externo_id_gen")
    @SequenceGenerator(
            name = "ods_externo_id_gen",
            sequenceName = "ods_externo_id_seq",
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "ods_id", nullable = false)
    private Integer odsId;

    @NotNull
    @Column(name = "ods_ordem", nullable = false)
    private Integer odsOrdem;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "ods_nome", nullable = false, length = 2000)
    private String odsNome;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "ods_descricao", nullable = false, length = 2000)
    private String odsDescricao;

}
