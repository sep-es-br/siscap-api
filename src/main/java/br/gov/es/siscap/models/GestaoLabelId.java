package br.gov.es.siscap.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestaoLabelId implements Serializable {

    @Column(name = "id_gestao")
    private Integer idGestao;

    @Column(name = "id_label")
    private Integer idLabel;
    
}
