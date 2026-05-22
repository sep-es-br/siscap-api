package br.gov.es.siscap.models;

import jakarta.persistence.*;
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
public class OdsIndicadorExterno {

    

}
