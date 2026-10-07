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
    private boolean tokenExpirado = false;
    private Boolean pdfConcluido;
    private Boolean assinaturaConcluida;
    private Boolean capturaConcluida;
    private Boolean encerramentoIniciado;
    private Boolean encerramentoConcluido;
    private Boolean erroEncerramento;
    private final java.util.List<String> avisos = new java.util.concurrent.CopyOnWriteArrayList<>();

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
    public Boolean getPdfConcluido() { return pdfConcluido; }
    public Boolean getAssinaturaConcluida() { return assinaturaConcluida; }
    public Boolean getCapturaConcluida() { return capturaConcluida; }
    public Boolean getEncerramentoIniciado() { return encerramentoIniciado; }
    public Boolean getEncerramentoConcluido() { return encerramentoConcluido; }
    public Boolean getErroEncerramento() { return erroEncerramento; }
    public void setProgressoEncerramento(boolean iniciado, boolean concluido, boolean erro) {
        this.encerramentoIniciado = iniciado;
        this.encerramentoConcluido = concluido;
        this.erroEncerramento = erro;
    }
    public java.util.List<String> getAvisos() { return avisos; }
    public void setProgressoParecer(boolean pdf, boolean assinatura, boolean captura) {
        this.pdfConcluido = pdf;
        this.assinaturaConcluida = assinatura;
        this.capturaConcluida = captura;
    }
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

    public void setTokenExpirado(boolean tokenExpirado) { this.tokenExpirado = tokenExpirado; }
    public boolean isTokenExpirado() { return this.tokenExpirado; }

}
