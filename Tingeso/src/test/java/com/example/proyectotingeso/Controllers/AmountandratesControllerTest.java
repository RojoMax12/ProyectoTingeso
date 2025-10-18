package com.example.proyectotingeso.Controllers;


import com.example.proyectotingeso.Entity.AmountsandratesEntity;
import com.example.proyectotingeso.Services.AmountsandratesServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AmountandratesController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔥 desactiva filtros de Spring Security
public class AmountandratesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AmountsandratesServices amountsandratesServices;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createAmountsAndRates_ShouldReturnDefaultValues() throws Exception {
        AmountsandratesEntity entity = new AmountsandratesEntity(
                1L,
                0.0,
                0.0,
                0.0
        );

        given(amountsandratesServices.createAmountsAndRates()).willReturn(entity);

        mockMvc.perform(post("/api/AmountandRates/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyrentalrate", is(0.0)))
                .andExpect(jsonPath("$.dailylatefeefine", is(0.0)))
                .andExpect(jsonPath("$.reparationcharge", is(0.0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateAmountsAndRates_ShouldReturnUpdatedEntity() throws Exception {
        AmountsandratesEntity updated = new AmountsandratesEntity(
                1L,
                25.0,
                12.0,
                5.0
        );

        given(amountsandratesServices.updateAmountAndRates(Mockito.any(AmountsandratesEntity.class)))
                .willReturn(updated);

        String requestJson = """
            {
                "id": 1,
                "dailyrentalrate": 25.0,
                "dailylatefeefine": 12.0,
                "reparationcharge": 5.0
            }
            """;

        mockMvc.perform(put("/api/AmountandRates/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyrentalrate", is(25.0)))
                .andExpect(jsonPath("$.dailylatefeefine", is(12.0)))
                .andExpect(jsonPath("$.reparationcharge", is(5.0)));
    }
}
