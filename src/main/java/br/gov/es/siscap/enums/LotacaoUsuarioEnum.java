package br.gov.es.siscap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LotacaoUsuarioEnum {

	SUBEPP(1L),
	SUBEO(2L),
	OUTRO(3L);

	private final Long value;

	public static LotacaoUsuarioEnum fromGuid(String guid, String guidSUBEPP, String guidSUBEO) {
        if (guidSUBEPP.equals(guid)) return SUBEPP;
        if (guidSUBEO.equals(guid)) return SUBEO;
        return OUTRO;
    }

	public Long getValue() {
		return value;
	}	

}