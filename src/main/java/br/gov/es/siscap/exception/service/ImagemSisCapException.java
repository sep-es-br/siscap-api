package br.gov.es.siscap.exception.service;

import java.util.Collections;
import java.util.List;

public class ImagemSisCapException extends ServiceSisCapException {
    public ImagemSisCapException(List<String> erros) {
        super(erros);
    }

    public ImagemSisCapException(String erro) {
        super(Collections.singletonList(erro));
    }
}
