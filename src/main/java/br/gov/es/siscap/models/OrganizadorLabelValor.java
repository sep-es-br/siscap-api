package br.gov.es.siscap.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizador_label_valor")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update organizador_label_valor set apagado = true where id_organizador=? and id_label_valor=?")
@SQLRestriction("apagado = FALSE and id_tipo_status = 1")
public class OrganizadorLabelValor {

    @EmbeddedId
    private OrganizadorLabelValorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idOrganizador")
    @JoinColumn(name = "id_organizador")
    private IndicadorOrganizadorExterno organizador;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idLabelValor")
    @JoinColumn(name = "id_label_valor")
    private IndicadorLabelValor labelValor;

}
