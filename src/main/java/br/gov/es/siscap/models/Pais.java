package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "pais")
@Getter
@SQLDelete(sql = "update pais set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String continente;
    private String subcontinente;
    @Column(name = "iso_alpha_3")
    private String isoAlpha3;
    private String ddi;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
     private boolean apagado = Boolean.FALSE;

    public Pais() {
    }

    public Pais(Long id) {
        this.id = id;
    }

}
