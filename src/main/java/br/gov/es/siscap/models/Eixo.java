package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "eixo")
@Getter
@SQLDelete(sql = "update eixo set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
@NoArgsConstructor
public class Eixo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @ManyToOne
    @JoinColumn(name = "id_plano")
    private Plano plano;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    private boolean apagado;

    public Eixo(Long id) {
        this.id = id;
    }
}
