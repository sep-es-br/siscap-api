package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "cidade")
@Getter
@SQLDelete(sql = "update cidade set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_estado")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Estado estado;
    @Column(name = "id_ibge")
    private String codigoIBGE;
    private String nome;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_microregiao")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Microregiao microregiao;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Cidade() {
    }
}
