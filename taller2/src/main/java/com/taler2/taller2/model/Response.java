package com.taler2.taller2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Response {
    protected LocalDateTime timeStampo;
    protected int statusCode;
    protected HttpStatus status;
    protected String message;
    protected Map<?,?> data;
}