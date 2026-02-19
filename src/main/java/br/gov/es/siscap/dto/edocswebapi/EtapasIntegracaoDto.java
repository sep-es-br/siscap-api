package br.gov.es.siscap.dto.edocswebapi;

import br.gov.es.siscap.enums.edocs.EtapasIntegracaoEdocsEnum;
import br.gov.es.siscap.service.ChaveEtapasIntegracao;
import lombok.ToString;

@ToString
public class EtapasIntegracaoDto {

    private Long id;
    private EtapasIntegracaoEdocsEnum etapa;
    private boolean iniciada;
    private boolean finalizada;
    private boolean erro;
    private String msgAlertaExibir;
    private String contextoNegocio;

    public EtapasIntegracaoDto(Long id, EtapasIntegracaoEdocsEnum etapa, boolean iniciada, boolean finalizada, boolean erro ) {
        this.id = id;
        this.etapa = etapa;
        this.iniciada = iniciada;
        this.finalizada = finalizada;
        this.erro = erro;
    }

    public EtapasIntegracaoDto(ChaveEtapasIntegracao chaveEtapaIntegracao, EtapasIntegracaoEdocsEnum etapa, boolean iniciada, boolean finalizada, boolean erro ) {
        this.id = chaveEtapaIntegracao.id();
        this.etapa = etapa;
        this.iniciada = iniciada;
        this.finalizada = finalizada;
        this.erro = erro;
        this.contextoNegocio = chaveEtapaIntegracao.tipo().name();
    }

    public EtapasIntegracaoEdocsEnum getEtapa() { return etapa; }
    public boolean isIniciada() { return iniciada; }
    public boolean isFinalizada() { return finalizada; }
    public boolean isErro() { return erro; }
    public Long getId() { return id; }
    public void setIniciou( boolean iniciou ) { this.iniciada = iniciou; }
    public void setFinalizou( boolean finalizou ) { this.finalizada = finalizou; }
    public void setErro(boolean erro) { this.erro = erro; }

    public void setMsgAlertaExibir(String msgAlertaExibir) { this.msgAlertaExibir = msgAlertaExibir; }
    public String getMsgAlertaExibir() { return this.msgAlertaExibir; }

    public void setContextoNegocio(String contextoNegocio) { this.contextoNegocio = contextoNegocio; }
    public String getContextoNegocio() { return this.contextoNegocio; }

}
