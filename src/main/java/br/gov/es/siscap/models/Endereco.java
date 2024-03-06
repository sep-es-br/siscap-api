package br.gov.es.siscap.models;

import br.gov.es.siscap.form.EnderecoForm;
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

    public Endereco(EnderecoForm endereco) {
        this.rua = endereco.rua();
        this.numero = endereco.numero();
        this.bairro = endereco.bairro();
        this.complemento = endereco.complemento();
        this.codigoPostal = endereco.codigoPostal();
        this.cidade = endereco.idCidade() != null ? new Cidade(endereco.idCidade()) : null;
        this.criadoEm = LocalDateTime.now();
    }

    public void atualizarEndereco(EnderecoForm form) {
        if (form.rua() != null)
            this.rua = form.rua();
        if (form.numero() != null)
            this.numero = form.numero();
        if (form.bairro() != null)
            this.bairro = form.bairro();
        if (form.complemento() != null)
            this.complemento = form.complemento();
        if (form.codigoPostal() != null)
            this.codigoPostal = form.codigoPostal();
        if (form.idCidade() != null)
            this.cidade = new Cidade(form.idCidade());
        this.setAtualizadoEm(LocalDateTime.now());
    }
}
