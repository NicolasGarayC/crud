package com.taler2.taller2.model;
import lombok.Data;

@Data
public class Usuario {
    private Long id;
    private String usuario;
    private String password;
    private int rol;
}
