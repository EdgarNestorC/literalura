package com.alura.literalura.repository;

import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autores")
    List<Libro> findAllWithAutores();

    @Query("SELECT l FROM Libro l LEFT JOIN FETCH l.autores WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))")
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    @Query("SELECT l FROM Libro l LEFT JOIN FETCH l.autores WHERE l.idioma = :idioma")
    List<Libro> findByIdioma(String idioma);

}
