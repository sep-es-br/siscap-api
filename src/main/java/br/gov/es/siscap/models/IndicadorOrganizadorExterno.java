package br.gov.es.siscap.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "indicador_organizador_externo")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update indicador_organizador_externo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class IndicadorOrganizadorExterno extends ControleHistorico {
 
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
 
    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "model_name")
    private String modelName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizador_pai")
    private IndicadorOrganizadorExterno pai;

}
