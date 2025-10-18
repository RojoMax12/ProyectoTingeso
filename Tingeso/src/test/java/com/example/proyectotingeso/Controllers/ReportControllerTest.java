package com.example.proyectotingeso.Controllers;

import com.example.proyectotingeso.Entity.ReportEntity;
import com.example.proyectotingeso.Services.ReportServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportServices reportServices;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createReport_ShouldReturnList() throws Exception {
        LocalDate today = LocalDate.now();
        ReportEntity r1 = new ReportEntity(1L, "ReportLoanTools", 1L, null, null, today);
        ReportEntity r2 = new ReportEntity(2L, "ReportLoanTools", 2L, null, null, today);
        List<ReportEntity> reports = Arrays.asList(r1, r2);

        given(reportServices.ReportLoanTools()).willReturn(reports);

        mockMvc.perform(post("/api/report/ReportLoan")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idLoanTool", is(1)))
                .andExpect(jsonPath("$[1].idLoanTool", is(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getReportByDate_ShouldReturnFilteredReports() throws Exception {
        LocalDate start = LocalDate.of(2025, 9, 1);
        LocalDate end = LocalDate.of(2025, 9, 30);
        ReportEntity r1 = new ReportEntity(1L, "ReportLoanTools", 1L, null, null, LocalDate.of(2025, 9, 5));
        ReportEntity r2 = new ReportEntity(2L, "ReportLoanTools", 2L, null, null, LocalDate.of(2025, 9, 10));
        List<ReportEntity> reports = Arrays.asList(r1, r2);

        given(reportServices.ReportfilterDate(start, end)).willReturn(reports);

        mockMvc.perform(get("/api/report/Reports/{initdate}/{findate}", start, end))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idLoanTool", is(1)))
                .andExpect(jsonPath("$[1].idLoanTool", is(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllReports_ShouldReturnAllReports() throws Exception {
        LocalDate today = LocalDate.now();
        ReportEntity r1 = new ReportEntity(1L, "ReportLoanTools", 1L, null, null, today);
        ReportEntity r2 = new ReportEntity(2L, "ReportLoanTools", 2L, null, null, today);
        List<ReportEntity> reports = Arrays.asList(r1, r2);

        given(reportServices.ReportLoanTools()).willReturn(reports);

        mockMvc.perform(get("/api/report/AllReports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idLoanTool", is(1)))
                .andExpect(jsonPath("$[1].idLoanTool", is(2)));
    }
}
