/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.siscap.models.converter;

import br.gov.es.siscap.enums.StatusProgramaEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

/**
 *
 * @author gean.carneiro
 */
@Converter(autoApply = true)
public class StatusProgramaEnumConverter implements AttributeConverter<StatusProgramaEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StatusProgramaEnum attribute) {
        if(attribute == null) return null;
        return attribute.getValue();
    }

    @Override
    public StatusProgramaEnum convertToEntityAttribute(Integer dbData) {
        if(dbData == null) return null;
        
        return Arrays.stream(StatusProgramaEnum.values())
                .filter(status -> status.getValue() == dbData)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Valor inválido para StatusProgramaEnum: " + dbData));
        
    }
    
}
