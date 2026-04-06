/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.siscap.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author gean.carneiro
 */
@Entity
@Table(name = "projeto_programa")
@NoArgsConstructor
@Getter
public class ProjetoPrograma {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;
    
    @ManyToOne
    @JoinColumn(name= "id_programa", nullable = false)
    private Programa programa;
    
    @DateTimeFormat
    @Column(name = "apagado_em", nullable = true)
    @Setter
    private LocalDateTime apagadoEm;
    
    @DateTimeFormat
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
    
    public ProjetoPrograma(Projeto projeto, Programa programa) {
        this.projeto = projeto;
        this.programa = programa;
        this.criadoEm = LocalDateTime.now();
    }
    
}
