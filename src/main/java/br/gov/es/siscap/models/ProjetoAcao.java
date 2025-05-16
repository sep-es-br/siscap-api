package br.gov.es.siscap.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import br.gov.es.siscap.dto.ProjetoAcaoDto;
import br.gov.es.siscap.enums.TipoStatusEnum;

@Entity
@Table(name = "projeto_acao")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE projeto_acao SET apagado = true WHERE id=?")
@SQLRestriction("apagado = FALSE")
public class ProjetoAcao extends ControleHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "projeto_acao_id_gen")
    @SequenceGenerator(name = "projeto_acao_id_gen", sequenceName = "projeto_acao_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @Column(name = "acao_principal", nullable = false, length = 2000)
    private String descricaoAcaoPrincipal;

    @Column(name = "valor_estimado", nullable = false, precision = 14, scale = 2)
    private java.math.BigDecimal valorEstimado;

    @Column(name = "descricao_acoes_secundarias", length = 2000)
    private String descricaoAcoesSecundarias;

    public ProjetoAcao(Projeto projeto) {
        this.setProjeto(projeto);
    }

    public ProjetoAcao(Projeto projeto, ProjetoAcaoDto acao) {
		this.setProjeto(projeto);
		this.setDescricaoAcaoPrincipal(acao.descricaoAcaoPrincipal());
		this.setDescricaoAcoesSecundarias(acao.descricaoAcoesSecundarias());
        this.setValorEstimado(acao.valorEstimadoAcaoPrincipal());
	}

    public boolean compararIdAcaoComAcaoDto(ProjetoAcaoDto acaoDto) {
        return Objects.equals( this.id , acaoDto.idAcao() );
    }

    /*
    public void atualizarMembroEquipe(EquipeDto equipeDto) {
		this.setTipoPapel(new TipoPapel(equipeDto.idPapel()));
		if (!Objects.equals(equipeDto.idStatus(), TipoStatusEnum.ATIVO.getValue())) {
			this.setTipoStatus(new TipoStatus(equipeDto.idStatus()));
			this.setJustificativa(equipeDto.justificativa());
			this.setDataFim(LocalDateTime.now());
			super.atualizarHistorico();
		}
	}
    */

    public void atualizarAcaoProjeto(ProjetoAcaoDto acaoDto) {
		if (!Objects.equals(acaoDto.idStatus(), TipoStatusEnum.ATIVO.getValue())) {
            this.setDescricaoAcoesSecundarias(acaoDto.descricaoAcoesSecundarias())
            this.descricaoAcaoPrincipal(acaoDto.descricaoAcaoPrincipal())
            this.setValorEstimado(acaoDto.valorEstimadoAcaoPrincipal());
			this.setDataFim(LocalDateTime.now());
			super.atualizarHistorico();
		}
    }

}

