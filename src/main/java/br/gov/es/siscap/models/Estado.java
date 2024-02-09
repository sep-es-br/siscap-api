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
@Table(name = "estado")
@Getter
@SQLDelete(sql = "update estado set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_pais")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Pais pais;
    @Column(name = "id_ibge")
    private String codigoIBGE;
    @Column(name = "id_regiao_ibge")
    private String codigoRegiaoIBGE;
    private String nome;
    private String sigla;
    private String nomeRegiao;
    private String siglaRegiao;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
     private boolean apagado = Boolean.FALSE;

    public Estado() {
    }
}
