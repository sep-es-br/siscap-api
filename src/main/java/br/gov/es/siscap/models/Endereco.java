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
@Table(name = "endereco")
@Getter
@SQLDelete(sql = "update endereco set apagado = true where id=?")
@SQLRestriction("apagado = FALSE")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rua;
    private Integer numero;
    private String bairro;
    private String complemento;
    private String codigoPostal;
    @ManyToOne
    @JoinColumn(name = "id_cidade")
    @SQLJoinTableRestriction("apagado = FALSE")
    private Cidade cidade;
    @DateTimeFormat
    private LocalDateTime criadoEm;
    @Setter
    @DateTimeFormat
    private LocalDateTime atualizadoEm;
    @Setter
    private boolean apagado = Boolean.FALSE;


    public Endereco() {
    }
}
