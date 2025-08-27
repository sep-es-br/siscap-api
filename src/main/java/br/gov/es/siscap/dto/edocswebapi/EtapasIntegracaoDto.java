package br.gov.es.siscap.dto.edocswebapi;

import br.gov.es.siscap.enums.edocs.EtapasIntegracaoEdocsEnum;

public class EtapasIntegracaoDto {

    private Long idProjeto;
    private EtapasIntegracaoEdocsEnum etapa;
    private boolean iniciada;
    private boolean finalizada;

    public EtapasIntegracaoDto(Long idProjeto, EtapasIntegracaoEdocsEnum etapa, boolean iniciada, boolean finalizada) {
        this.idProjeto = idProjeto;
        this.etapa = etapa;
        this.iniciada = iniciada;
        this.finalizada = finalizada;
    }

    public EtapasIntegracaoEdocsEnum getEtapa() { return etapa; }
    public boolean isIniciada() { return iniciada; }
    public boolean isFinalizada() { return finalizada; }
    public void setFinalizada(boolean finalizada) { this.finalizada = finalizada; };
    public Long getIdProjeto() { return idProjeto; }
    public void setIniciou( boolean iniciou ) { this.iniciada = iniciou; }
    public void setFinalizou( boolean finalizou ) { this.finalizada = finalizou; }

}
