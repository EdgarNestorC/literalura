package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DatosAutor(
        @JsonProperty("name") String nombre,
        @JsonProperty("birth_year") Integer añoNacimiento,
        @JsonProperty("death_year") Integer añoFallecimiento
) {}
