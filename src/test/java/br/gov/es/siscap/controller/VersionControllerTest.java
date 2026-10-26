package br.gov.es.siscap.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VersionControllerTest {
    @Test
    void returnsReleaseInOpenPmoFormat() throws Exception {
        MockMvcBuilders.standaloneSetup(new VersionController(" 1.47.r.26.1023.1520 ")).build()
            .perform(get("/versions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.version").value("1.47.r.26.1023.1520"));
    }

    @Test
    void usesDevelopmentForEmptyVersion() throws Exception {
        MockMvcBuilders.standaloneSetup(new VersionController(" ")).build()
            .perform(get("/versions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.version").value("Desenvolvimento"));
    }
}
