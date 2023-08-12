package com.taler2.taller2.model;


public class ProcedimientoResponse {
    private int codigo;
    private String mensaje;

    public ProcedimientoResponse(int codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }
    public ProcedimientoResponse() {
    }
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}