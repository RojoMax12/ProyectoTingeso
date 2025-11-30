package com.example.proyectotingeso.Services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.proyectotingeso.Entity.RoleEntity;
import com.example.proyectotingeso.Entity.StateUsersEntity;
import com.example.proyectotingeso.Entity.ToolEntity;
import com.example.proyectotingeso.Entity.UserEntity;
import com.example.proyectotingeso.Repository.RoleRepository;
import com.example.proyectotingeso.Repository.StateUsersRepository;
import com.example.proyectotingeso.Repository.ToolRepository;
import com.example.proyectotingeso.Repository.UserRepository;
import com.example.proyectotingeso.Services.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

public class UserServicesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StateUsersRepository stateUsersRepository;

    @Mock
    private ToolRepository toolRepository;

    @InjectMocks
    private UserServices userServices;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_whenUserExists_thenReturnTrue() {
        // Given: Un usuario que existe con una contraseña correcta
        UserEntity user = new UserEntity();
        user.setRut("12345678-9");
        user.setPassword("password");

        UserEntity existingUser = new UserEntity();
        existingUser.setRut("12345678-9");
        existingUser.setPassword("password");

        when(userRepository.findByRut("12345678-9")).thenReturn(existingUser);

        // When: Llamada al método login
        boolean result = userServices.login(user);

        // Then: Verifica que el resultado sea true
        assertTrue(result);
    }

    @Test
    public void testLogin_whenUserDoesNotExist_thenReturnFalse() {
        // Given: Un usuario que no existe
        when(userRepository.findByRut("12345678-9")).thenReturn(null);

        // When: Llamada al método login
        UserEntity user = new UserEntity();
        user.setRut("12345678-9");
        boolean result = userServices.login(user);

        // Then: Verifica que el resultado sea false
        assertFalse(result);
    }

    @Test
    public void testSaveUser_whenRoleIsNull_thenAssignDefaultRole() {
        // Given: Un usuario sin rol asignado
        UserEntity user = new UserEntity();
        user.setRut("12345678-9");
        user.setRole(null);  // Rol nulo
        user.setState(null); // Estado nulo

        RoleEntity employerRole = new RoleEntity(1L, "Employer");
        when(roleRepository.findByName("Employer")).thenReturn(employerRole);

        StateUsersEntity activeState = new StateUsersEntity(1L, "Active");
        when(stateUsersRepository.findByName("Active")).thenReturn(activeState);

        // CORRECCIÓN: Usar ArgumentMatchers.any() y castear la respuesta
        when(userRepository.save(ArgumentMatchers.any())).thenAnswer(i -> (UserEntity) i.getArguments()[0]);

        // When: Llamada al método saveUser
        UserEntity savedUser = userServices.saveUser(user);
        // ... (assertions) ...
    }


    @Test
    public void testSaveUser_whenRoleIsNotNull_thenSaveUserWithRole() {
        // Given: Un usuario con rol asignado
        UserEntity user = new UserEntity();
        user.setRut("12345678-9");
        user.setRole(2L);  // Rol personalizado
        user.setState(null); // El servicio asignará el estado por defecto si el rol ya está

        RoleEntity adminRole = new RoleEntity(2L, "Admin");

        when(roleRepository.findById(2L)).thenReturn(Optional.of(adminRole));

        StateUsersEntity activeState = new StateUsersEntity(1L, "Active");
        when(stateUsersRepository.findByName("Active")).thenReturn(activeState);

        // CORRECCIÓN: Usar ArgumentMatchers.any() y castear la respuesta
        when(userRepository.save(ArgumentMatchers.any())).thenAnswer(i -> (UserEntity) i.getArguments()[0]);

        // When: Llamada al método saveUser
        UserEntity savedUser = userServices.saveUser(user);

        // ... (assertions) ...
    }


    @Test
    public void testChangereplacement_costTool_whenNotAdmin_thenThrowException() {
        // Given: Un usuario con rol no "Admin"
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setRole(2L);  // Rol no "Admin"

        when(userRepository.findById(2L)).thenReturn(user);  // Simula que el usuario existe

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userServices.Changereplacement_costTool("Hammer", 2L, 200);
        });

        assertEquals("No eres administrador", exception.getMessage());
    }


    @Test
    public void testDeleteUser_whenUserNotFound_thenThrowException() throws Exception {
        when(userRepository.findById(1L)).thenReturn(null);  // Simula que no se encuentra el usuario

        Exception exception = assertThrows(Exception.class, () -> {
            userServices.deleteUser(1L);
        });

        assertEquals("No existe el usuario", exception.getMessage());
    }
}


