package br.gov.es.siscap.models;

import br.gov.es.siscap.dto.RateioDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "projeto_acao_localidade_quantia")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE projeto_acao_localidade_quantia SET apagado = true WHERE id=?")
@SQLRestriction("apagado = false")
public class ProjetoAcaoLocalidadeQuantia extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_acao_localidade_quantia_id_gen")
    @SequenceGenerator(name = "projeto_acao_localidade_quantia_id_gen", sequenceName = "projeto_acao_localidade_quantia_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_projeto_acao", nullable = false)
    private ProjetoAcao projetoAcao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_localidade", nullable = false)
    private Localidade localidade;

    @NotNull
    @Column(name = "quantia", nullable = false, precision = 25, scale = 2)
    private BigDecimal quantia;

    @NotNull
    @Column(name = "percentual", nullable = false, precision = 7, scale = 4)
    private BigDecimal percentual;

    public ProjetoAcaoLocalidadeQuantia(
            ProjetoAcao projetoAcao,
            RateioDto rateioDto) {
        this.projetoAcao = projetoAcao;
        this.localidade = new Localidade(rateioDto.idLocalidade());
        this.quantia = rateioDto.quantia();
        this.percentual = rateioDto.percentual();
    }

    public void atualizar(RateioDto rateioDto) {
        this.quantia = rateioDto.quantia();
        this.percentual = rateioDto.percentual();
        super.atualizarHistorico();
    }

    public void apagar() {
        super.apagarHistorico();
    }
}