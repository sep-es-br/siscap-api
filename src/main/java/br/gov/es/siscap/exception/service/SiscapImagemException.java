package br.gov.es.siscap.exception.service;

import java.util.Collections;

public class SiscapImagemException extends SiscapServiceException {
    public SiscapImagemException(String erro) {
        super(Collections.singletonList(erro));
    }
}
