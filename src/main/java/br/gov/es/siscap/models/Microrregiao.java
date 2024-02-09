package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "microrregiao")
@Getter
@SQLDelete(sql = "update microrregiao set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Microrregiao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @OneToMany(mappedBy = "microrregiao")
    private List<Cidade> cidades;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Microrregiao() {
    }

    public Microrregiao(Long id) {
        this.id = id;
    }
}
