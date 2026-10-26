package br.gov.es.siscap.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/versions")
public class VersionController {
    private final String version;

    public VersionController(@Value("${app.version:Desenvolvimento}") String version) {
        this.version = version == null || version.isBlank() || version.contains("@release#@") ? "Desenvolvimento" : version.trim();
    }

    @GetMapping(produces = "application/json")
    public VersionResponse currentVersion() {
        return new VersionResponse(true, new VersionData(version));
    }

    public record VersionData(String version) {}
    public record VersionResponse(boolean success, VersionData data) {}
}
