package com.taler2.taller2.controller;
import com.taler2.taller2.model.ProcedimientoResponse;
import com.taler2.taller2.model.Usuario;
import com.taler2.taller2.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.sql.CallableStatement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/list")
    public ResponseEntity<Response> getUsuarios() {
        List<Usuario> usuarios = jdbc.query(
                "SELECT * FROM usuario;",
                new BeanPropertyRowMapper<>(Usuario.class)
        );

        return  new ResponseEntity<Response>(
                Response.builder()
                        .timeStampo(LocalDateTime.now())
                        .message("Data retrieved succesful.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of("usuarios",usuarios))
                        .build()
                , HttpStatus.OK
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Response> getUsuarioById(@PathVariable Integer id) {
        try {
            Usuario usuario = jdbc.queryForObject(
                    "SELECT * FROM usuario WHERE id = ? ",
                    new Object[]{id},
                    new BeanPropertyRowMapper<>(Usuario.class)
            );

            return  new ResponseEntity<Response>(
                    Response.builder()
                            .timeStampo(LocalDateTime.now())
                            .message("Data retrieved succesful.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of("usuario",usuario))
                            .build()
                    , HttpStatus.OK
            );
        } catch (EmptyResultDataAccessException e){
            return  new ResponseEntity<Response>(
                    Response.builder()
                            .timeStampo(LocalDateTime.now())
                            .message("User id not found.")
                            .status(HttpStatus.NOT_FOUND)
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .build()
                    , HttpStatus.NOT_FOUND
            );
        }
    }

    @PostMapping("/insertarUsuario")
    public ResponseEntity<ProcedimientoResponse> insertarUsuario(
            @RequestBody Usuario usuario) {
        try {
            Map<String, Object> result = jdbc.call(connection -> {
                CallableStatement call = connection.prepareCall("{call InsertarUsuarioYRol(?, ?, ?)}");
                call.setString(1, usuario.getUsuario());
                call.setString(2, usuario.getPassword());
                call.setInt(3, usuario.getRol());
                return call;
            }, Collections.emptyList());

            List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (resultSet != null && !resultSet.isEmpty()) {
                Map<String, Object> firstRow = resultSet.get(0);
                int codigo = (int) firstRow.get("codigo");
                String mensaje = (String) firstRow.get("mensaje");

                ProcedimientoResponse response = new ProcedimientoResponse();
                response.setCodigo(codigo);
                response.setMensaje(mensaje);

                HttpStatus httpStatus = (codigo == 200) ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR;

                return new ResponseEntity<>(response, httpStatus);
            } else {
                return new ResponseEntity<>(
                        new ProcedimientoResponse(500, "Error al procesar la solicitud"),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        } catch (Exception e) {
            // Manejar errores y retornar una respuesta adecuada
            ProcedimientoResponse response = new ProcedimientoResponse();
            response.setCodigo(500);
            response.setMensaje("Error al procesar la solicitud: " + e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateUsuario")
    public ResponseEntity<Response> updateUsuario(@RequestBody Usuario usuario) {
        try {
            int updateResultUsuarios = jdbc.update(
                    "UPDATE usuario SET usuario = ?, password = ? WHERE id = ?",
                    usuario.getUsuario(), usuario.getPassword(), usuario.getId()
            );

            int updateResultUsuarioRol = jdbc.update(
                    "UPDATE usuario_rol SET rol_id = ? WHERE id_usuario = ?",
                    usuario.getRol(), usuario.getId()
            );

            if (updateResultUsuarios == 1 && updateResultUsuarioRol == 1) {
                return new ResponseEntity<>(
                        Response.builder()
                                .timeStampo(LocalDateTime.now())
                                .message("Data persisted successfully.")
                                .status(HttpStatus.ACCEPTED)
                                .statusCode(HttpStatus.ACCEPTED.value())
                                .build(),
                        HttpStatus.ACCEPTED
                );
            } else {
                return new ResponseEntity<>(
                        Response.builder()
                                .timeStampo(LocalDateTime.now())
                                .message("Data update failed.")
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // Internal Server Error
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .build(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        } catch (Exception e) {
            // Capturing the exception and adding the error message to the response
            return new ResponseEntity<>(
                    Response.builder()
                            .timeStampo(LocalDateTime.now())
                            .message("Error while processing the request: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteUsuario(@PathVariable Integer id) {
        int result = jdbc.update(
                "DELETE FROM usuario WHERE id = ?",
                id);
        if (result == 1) {
            return  new ResponseEntity<Response>(
                    Response.builder()
                            .timeStampo(LocalDateTime.now())
                            .message("Data persited succesful.")
                            .status(HttpStatus.ACCEPTED)
                            .statusCode(HttpStatus.ACCEPTED.value())
                            .build()
                    , HttpStatus.ACCEPTED
            );
        } else {
            return  new ResponseEntity<Response>(
                    Response.builder()
                            .timeStampo(LocalDateTime.now())
                            .message("Data incorrect format.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }
    }
}
