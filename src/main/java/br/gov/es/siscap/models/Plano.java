package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "plano")
@Getter
@SQLDelete(sql = "update plano set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    private boolean apagado;

    public Plano() {
        apagado = Boolean.FALSE;
    }

}
