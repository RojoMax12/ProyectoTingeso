package com.example.proyectotingeso.Controllers;

import com.example.proyectotingeso.Entity.ClientEntity;
import com.example.proyectotingeso.Services.ClientServices;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de seguridad
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientServices clientServices;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createClient_ShouldReturnClient() throws Exception {
        ClientEntity client = new ClientEntity(1L, "Juan Perez", "juan@mail.com", "12345678-9", "987654321", 1L);

        given(clientServices.createClient(Mockito.any(ClientEntity.class))).willReturn(client);

        String json = """
            {
              "name": "Juan Perez",
              "email": "juan@mail.com",
              "rut": "12345678-9",
              "phone": "987654321",
              "state": 1
            }
            """;

        mockMvc.perform(post("/api/Client/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Juan Perez")))
                .andExpect(jsonPath("$.rut", is("12345678-9")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllClients_ShouldReturnList() throws Exception {
        ClientEntity c1 = new ClientEntity(1L, "Ana Gomez", "ana@mail.com", "11111111-1", "123456789", 1L);
        ClientEntity c2 = new ClientEntity(2L, "Pedro Lopez", "pedro@mail.com", "22222222-2", "987654321", 1L);

        List<ClientEntity> clients = Arrays.asList(c1, c2);
        given(clientServices.getAllClients()).willReturn(clients);

        mockMvc.perform(get("/api/Client/Allclient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Ana Gomez")))
                .andExpect(jsonPath("$[1].name", is("Pedro Lopez")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getClientByRut_ShouldReturnClient() throws Exception {
        ClientEntity client = new ClientEntity(1L, "Carlos Ruiz", "carlos@mail.com", "33333333-3", "123123123", 1L);
        given(clientServices.getClientByRut("33333333-3")).willReturn(client);

        mockMvc.perform(get("/api/Client/rut/{rut}", "33333333-3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Carlos Ruiz")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateClient_ShouldReturnUpdated() throws Exception {
        ClientEntity updated = new ClientEntity(1L, "Luis Sanchez", "luis@mail.com", "44444444-4", "999888777", 2L);
        given(clientServices.updateClient(Mockito.any(ClientEntity.class))).willReturn(updated);

        String json = """
            {
              "id": 1,
              "name": "Luis Sanchez",
              "email": "luis@mail.com",
              "rut": "44444444-4",
              "phone": "999888777",
              "state": 2
            }
            """;

        mockMvc.perform(put("/api/Client/UpdateClient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Luis Sanchez")))
                .andExpect(jsonPath("$.rut", is("44444444-4")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteClient_ShouldReturnNoContent() throws Exception {
        Mockito.when(clientServices.deleteClient(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/Client/Deleteclient/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getClientById_ShouldReturnClient() throws Exception {
        ClientEntity client = new ClientEntity(5L, "Maria Lopez", "maria@mail.com", "55555555-5", "555123123", 1L);
        given(clientServices.getClientById(5L)).willReturn(client);

        mockMvc.perform(get("/api/Client/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Maria Lopez")))
                .andExpect(jsonPath("$.rut", is("55555555-5")));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con el rol 'ADMIN'
    public void testGetAllClientLoanLate() throws Exception {
        // Crear datos de prueba: una lista de clientes con préstamos atrasados
        ClientEntity client1 = new ClientEntity(1L, "Carlos", "carlos@mail.com", "13.777.548-2", "+567923243", 2L);
        ClientEntity client2 = new ClientEntity(2L, "Felipe", "felipe@mail.com", "19.123.456-7", "+5679232432", 2L);

        List<ClientEntity> clientsWithLateLoans = Arrays.asList(client1, client2);

        // Simular la respuesta del servicio
        given(clientServices.getAllClientLoanLate()).willReturn(clientsWithLateLoans);

        // Realizar la solicitud GET al endpoint y verificar la respuesta
        mockMvc.perform(get("/api/client/AllClientLoanLate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verificar que la respuesta sea 200 OK
                .andExpect(jsonPath("$", hasSize(2)))  // Verificar que hay 2 clientes
                .andExpect(jsonPath("$[0].id", is(1)))  // Verificar que el primer cliente tiene ID 1
                .andExpect(jsonPath("$[1].id", is(2))); // Verificar que el segundo cliente tiene ID 2
    }

}
