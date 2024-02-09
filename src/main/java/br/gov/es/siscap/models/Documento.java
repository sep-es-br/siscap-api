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
@Table(name = "documento")
@Getter
@SQLDelete(sql = "update documento set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entidade")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Entidade entidade;
    @ManyToOne
    @JoinColumn(name = "id_tipo_documento")
    @SQLJoinTableRestriction("apagado = FALSE")
    private TipoDocumento tipoDocumento;
    private String numero;
    private String orgaoEmissor;
    @ManyToOne
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "status")
    private Status status;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

}
