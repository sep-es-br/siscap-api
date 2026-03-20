/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.gov.es.siscap.dto;


import br.gov.es.siscap.models.Projeto;
import br.gov.es.siscap.models.StatusProjeto;
import java.time.LocalDateTime;

/**
 *
 * @author Cliente
 */
public record StatusProjetoDto(
            long id,
            String status,
            LocalDateTime inicioEm,
            LocalDateTime fimEm,
            String feitoPor
            
        ) {

    public StatusProjetoDto(StatusProjeto model) {
        this(
            model.getId(), 
            model.getStatus(), 
            model.getInicioEm(),
            model.getFimEm(),
            model.getPessoa().getNome()
        );
    }
    
    
}
