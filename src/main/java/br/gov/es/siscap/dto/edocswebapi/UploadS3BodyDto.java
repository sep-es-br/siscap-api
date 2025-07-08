package br.gov.es.siscap.dto.edocswebapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UploadS3BodyDto(

    @JsonProperty("key")
    String key,

    @JsonProperty("bucket")
    String bucket,

    @JsonProperty("x-amz-algorithm")
    String xAmzAlgorithm,

    @JsonProperty("x-amz-credential")
    String xAmzCredential,

    @JsonProperty("x-amz-date")
    String xAmzDate,

    @JsonProperty("policy")
    String policy,

    @JsonProperty("x-amz-signature")
    String xAmzSignature

) {}
