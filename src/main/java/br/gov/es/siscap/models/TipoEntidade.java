package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "tipo_entidade")
@Getter
@SQLDelete(sql = "update tipo_entidade set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class TipoEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public TipoEntidade() {
    }

    public TipoEntidade(Long id) {
        this.id = id;
    }
}
