package com.alura.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)  // Ignora campos no mapeados
public record DatosLibro(
        @JsonProperty("id") Integer id,
        @JsonProperty("title") String titulo,
        @JsonProperty("download_count") Integer numeroDescargas,
        @JsonProperty("languages") List<String> idiomas,
        @JsonProperty("authors") List<DatosAutor> autores

) {}

