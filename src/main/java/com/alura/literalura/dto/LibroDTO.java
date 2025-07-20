package com.alura.literalura.dto;

import java.util.List;

public record LibroDTO(
        String titulo,
        List<String> autores,
        String idioma,
        Integer numeroDescargas
) {}
