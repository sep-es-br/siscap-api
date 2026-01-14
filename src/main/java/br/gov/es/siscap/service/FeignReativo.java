package br.gov.es.siscap.service;

import java.util.function.Supplier;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class FeignReativo {
    // Converte qualquer chamada Feign em um Mono
    public static <T> Mono<T> fromFeign(Supplier<T> feignCall) {
        return Mono.fromCallable(feignCall::get)
            .subscribeOn(Schedulers.boundedElastic());
    }
}
