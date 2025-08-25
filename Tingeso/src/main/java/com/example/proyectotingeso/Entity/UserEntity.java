package com.example.proyectotingeso.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;

@Entity
@Data
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String name;

    private String email;

    private String password;

    private String phone;

    @Column(nullable = false)
    private Long state;

    @Column(unique = true, nullable = false)
    private String rut;

    @Column(nullable = false)
    private Long role;




}
