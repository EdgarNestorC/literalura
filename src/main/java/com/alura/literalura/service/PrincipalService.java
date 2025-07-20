package com.alura.literalura.service;

import com.alura.literalura.dto.*;
import com.alura.literalura.model.*;
import com.alura.literalura.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrincipalService {
    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;


    private final String URL_BASE = "https://gutendex.com/books/?search=";

    public PrincipalService(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional(readOnly = true)
    public void mostrarMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \n
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAno();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    cerrarAplicacion();
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    @Transactional
    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el nombre del libro que deseas buscar:");
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + tituloLibro.replace(" ", "+"));

        try {
            RespuestaGutendex respuesta = objectMapper.readValue(json, RespuestaGutendex.class);

            if (respuesta.resultados() == null || respuesta.resultados().isEmpty()) {
                System.out.println("No se encontraron libros con ese título.");
                return;
            }

            DatosLibro datosLibro = respuesta.resultados().get(0);

            // Verificar si el libro ya existe
            Optional<Libro> libroExistente = libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo())
                    .stream()
                    .findFirst();

            if (libroExistente.isPresent()) {
                System.out.println("El libro ya está registrado: " +
                        convertirALibroDTO(libroExistente.get()));
                return;
            }

            // Guardar el libro y autores
            Libro libro = new Libro();
            libro.setTitulo(datosLibro.titulo());
            libro.setIdioma(datosLibro.idiomas() != null && !datosLibro.idiomas().isEmpty() ?
                    datosLibro.idiomas().get(0) : "en");
            libro.setNumeroDescargas(datosLibro.numeroDescargas() != null ?
                    datosLibro.numeroDescargas() : 0);

            Set<Autor> autores = datosLibro.autores() != null ?
                    datosLibro.autores().stream()
                            .map(a -> {
                                Autor autor = new Autor();
                                autor.setNombre(a.nombre() != null ? a.nombre() : "Desconocido");
                                autor.setAñoNacimiento(a.añoNacimiento());
                                autor.setAñoFallecimiento(a.añoFallecimiento());
                                return autor;
                            })
                            .collect(Collectors.toSet()) :
                    new HashSet<>();

            libro.setAutores(autores);
            libroRepository.save(libro);

            System.out.println("Libro guardado exitosamente: " + convertirALibroDTO(libro));

        } catch (JsonProcessingException e) {
            System.out.println("Error al procesar la respuesta de la API: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    private void listarLibrosRegistrados() {
        List<LibroDTO> libros = libroRepository.findAll().stream()
                .map(this::convertirALibroDTO)
                .toList();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        libros.forEach(System.out::println);
    }

    private LibroDTO convertirALibroDTO(Libro libro) {
        return new LibroDTO(
                libro.getTitulo(),
                libro.getAutores() != null ?
                        libro.getAutores().stream()
                                .map(Autor::getNombre)
                                .toList() :
                        List.of("Desconocido"),
                libro.getIdioma(),
                libro.getNumeroDescargas()
        );
    }

    @Transactional(readOnly = true)
    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        autores.forEach(a -> System.out.printf("""
                        Nombre: %s
                        Nacimiento: %d
                        Fallecimiento: %s
                        Libros: %s
                        
                        """,
                a.getNombre(),
                a.getAñoNacimiento(),
                a.getAñoFallecimiento() != null ? a.getAñoFallecimiento() : "N/A",
                a.getLibros() != null ?
                        a.getLibros().stream().map(Libro::getTitulo).collect(Collectors.toList()) :
                        "N/A"));
    }

    @Transactional(readOnly = true)
    private void listarAutoresVivosEnAno() {
        System.out.println("Ingrese el año para buscar autores vivos:");
        var año = teclado.nextInt();
        teclado.nextLine();

        List<Autor> autores = autorRepository.findAutoresVivosEnAño(año);
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + año);
            return;
        }
        autores.forEach(a -> System.out.printf("""
                        Nombre: %s
                        Nacimiento: %d
                        Fallecimiento: %s
                        
                        """,
                a.getNombre(),
                a.getAñoNacimiento(),
                a.getAñoFallecimiento() != null ? a.getAñoFallecimiento() : "N/A"));
    }

    @Transactional(readOnly = true)
    private void listarLibrosPorIdioma() {
        System.out.println("""
                Seleccione el idioma:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """);
        var idioma = teclado.nextLine();

        List<Libro> libros = libroRepository.findByIdioma(idioma);

        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma " + idioma);
            return;
        }

        libros.stream()
                .map(this::convertirALibroDTO)
                .forEach(libro -> System.out.printf("""
                                Título: %s
                                Autor(es): %s
                                Descargas: %d
                                
                                """,
                        libro.titulo(),
                        libro.autores(),
                        libro.numeroDescargas()));
    }

    private void cerrarAplicacion() {
        System.out.println("Cerrando la aplicación...");
        teclado.close(); // Cierra el Scanner
        System.exit(0); // Cierra la aplicación completamente
    }
}
