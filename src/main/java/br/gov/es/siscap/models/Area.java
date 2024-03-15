package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "area")
@Getter
@SQLDelete(sql = "update area set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
@NoArgsConstructor
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @ManyToOne
    @JoinColumn(name = "id_eixo")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Eixo eixo;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    private boolean apagado;

    public Area(Long id) {
        this.id = id;
    }
}
