package com.example.proyectotingeso.Services;

import com.example.proyectotingeso.Entity.RoleEntity;
import com.example.proyectotingeso.Repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Optional;

public class RoleServicesTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServices roleServices;

    private RoleEntity role;

    @BeforeEach
    void setUp() {
        role = new RoleEntity(1L, "Admin");
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testCreateRole_whenRolesNotExist_thenCreateRoles() {
        // Simula que no existen los roles "Employer", "Admin", y "Client"
        when(roleRepository.findByName("Employer")).thenReturn(null);
        when(roleRepository.findByName("Admin")).thenReturn(null);
        when(roleRepository.findByName("Client")).thenReturn(null);

        String result = roleServices.createRole();

        // Verifica que el resultado sea "Roles creados correctamente"
        assertEquals("Roles creados correctamente", result);

        // Verifica que los roles fueron guardados en el repositorio
        verify(roleRepository, times(1)).save(any(RoleEntity.class));
    }

    @Test
    public void testCreateRole_whenRolesExist_thenDoNotCreate() {
        // Simula que los roles ya existen
        when(roleRepository.findByName("Employer")).thenReturn(new RoleEntity(1L, "Employer"));
        when(roleRepository.findByName("Admin")).thenReturn(new RoleEntity(2L, "Admin"));
        when(roleRepository.findByName("Client")).thenReturn(new RoleEntity(3L, "Client"));

        String result = roleServices.createRole();

        // Verifica que el resultado sea "Roles ya inicializados"
        assertEquals("Roles ya inicializados", result);

        // Verifica que no se haya intentado guardar ningún rol nuevo
        verify(roleRepository, times(0)).save(any(RoleEntity.class));
    }

    @Test
    public void testGetAllRoles() {
        // Simula la respuesta del repositorio
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role));

        // Llama al método getAllRoles
        var roles = roleServices.getAllRoles();

        // Verifica que la lista no esté vacía y contenga el rol simulado
        assertFalse(roles.isEmpty());
        assertEquals(1, roles.size());
        assertEquals("Admin", roles.get(0).getName());
    }

    @Test
    public void testGetRoleById_whenRoleExists_thenReturnRole() {
        // Simula la respuesta del repositorio
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Llama al método getRoleById
        Optional<RoleEntity> foundRole = roleServices.getRoleById(1L);

        // Verifica que el rol fue encontrado
        assertTrue(foundRole.isPresent());
        assertEquals("Admin", foundRole.get().getName());
    }

    @Test
    public void testGetRoleById_whenRoleDoesNotExist_thenReturnEmpty() {
        // Simula la respuesta del repositorio para un ID no existente
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        // Llama al método getRoleById
        Optional<RoleEntity> foundRole = roleServices.getRoleById(99L);

        // Verifica que no se encontró el rol
        assertFalse(foundRole.isPresent());
    }

    @Test
    public void testDeleteRole_whenRoleExists_thenDeleteRole() throws Exception {
        // Simula que el rol existe
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Llama al método deleteRole
        boolean result = roleServices.deleteRole(1L);

        // Verifica que el rol fue eliminado
        assertTrue(result);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteRole_whenRoleDoesNotExist_thenThrowException() {
        // Simula que el rol no existe
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: Verifica que se lance una excepción al intentar eliminar un rol que no existe
        assertThrows(Exception.class, () -> roleServices.deleteRole(99L));
    }

    @Test
    public void testUpdateRole_whenRoleExists_thenUpdateRole() {
        // Simula la respuesta del repositorio
        RoleEntity updatedRole = new RoleEntity(1L, "UpdatedAdmin");
        when(roleRepository.save(updatedRole)).thenReturn(updatedRole);

        // Llama al método updateRole
        RoleEntity result = roleServices.updateRole(updatedRole);

        // Verifica que el rol fue actualizado correctamente
        assertNotNull(result);
        assertEquals("UpdatedAdmin", result.getName());
        verify(roleRepository, times(1)).save(updatedRole);
    }
}
