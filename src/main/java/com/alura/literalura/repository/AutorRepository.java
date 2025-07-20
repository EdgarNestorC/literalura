package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT a FROM Autor a WHERE a.añoNacimiento <= :año AND (a.añoFallecimiento >= :año OR a.añoFallecimiento IS NULL)")
    List<Autor> findAutoresVivosEnAño(Integer año);
}
