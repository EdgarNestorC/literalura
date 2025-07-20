package com.alura.literalura.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RespuestaGutendex(
        @JsonProperty("count") int count,
        @JsonProperty("next") String next,
        @JsonProperty("previous") String previous,
        @JsonProperty("results") List<DatosLibro> resultados  // Aquí está el problema
) {}
