package com.example.proyectotingeso.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "StateTools")
@NoArgsConstructor
@AllArgsConstructor
public class StateToolsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)

    private Long id;

    private String name;
}
