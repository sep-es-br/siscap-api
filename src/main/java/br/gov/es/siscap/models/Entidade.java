package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "entidade")
@Getter
@SQLDelete(sql = "update entidade set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Entidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String nomeFantasia;
    private LocalDate dataFundacao;
    @OneToOne
    @JoinColumn(name = "entidade_pai")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Entidade entidadePai;
    @ManyToOne
    @SQLJoinTableRestriction("apagado = FALSE")
    @JoinColumn(name = "status")
    private Status status;
    @OneToOne
    @JoinColumn(name = "id_endereco")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Endereco endereco;
    @ManyToOne
    @JoinColumn(name = "id_tipo_entidade")
    @SQLJoinTableRestriction("apagado = FALSE")
    private TipoEntidade tipoEntidade;
    @OneToMany(mappedBy = "entidade")
    private List<Documento> documentos;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;

    public Entidade() {
    }

    public Entidade(Long id) {
        this.id = id;
    }
}
