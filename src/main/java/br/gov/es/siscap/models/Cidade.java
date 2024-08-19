package br.gov.es.siscap.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "cidade")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update cidade set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Estado estado;

    @Column(name = "id_ibge")
    private String codigoIBGE;

    @Column(name = "nome")
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_microrregiao")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Microrregiao microrregiao;

    @OneToMany(mappedBy = "cidade")
    private Set<ProjetoCidade> projetoCidadeSet;

    @DateTimeFormat
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @DateTimeFormat
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "apagado")
    private boolean apagado = Boolean.FALSE;

    public Cidade(Long id) {
        this.id = id;
    }
}
