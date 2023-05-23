package ufro.cl.Prueba_2.service;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ufro.cl.Prueba_2.model.Usuario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UsuarioService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private ResourceLoader resourceLoader;

    private List<Usuario> usuarios; // Declarar la lista de usuarios como variable de instancia

    @PostConstruct
    public void inicializar() throws IOException {
        usuarios = leerUsuarios(); // Inicializar la lista de usuarios después de la construcción del bean
    }

    public List<Usuario> leerUsuarios() throws IOException {
        List<Usuario> usuarios = new ArrayList<>();

        Resource resource = resourceLoader.getResource("classpath:Dataset/dataset2.csv");
        File file = resource.getFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int contador = 0;
            while ((line = br.readLine()) != null) {
                // Omitir las primeras líneas que contienen los nombres de las columnas
                if (contador > 0) {
                    String[] data = line.split(";");
                    int id = Integer.parseInt(data[0]);
                    String correo = data[1];
                    LocalDate ultimaConexion = LocalDate.parse(data[2], DATE_FORMATTER);
                    List<Integer> siguiendo = new ArrayList<>();

                    if (!data[3].isEmpty()) {
                        String[] siguiendoData = data[3].split(",");
                        for (String siguiendoStr : siguiendoData) {
                            siguiendo.add(Integer.parseInt(siguiendoStr));
                        }
                    }

                    Usuario usuario = new Usuario(id, correo, ultimaConexion, siguiendo);
                    usuarios.add(usuario);
                }
                contador++;
            }
        }

        return usuarios;
    }

    public List<Usuario> getUsuariosInactivos() {
        List<Usuario> usuariosInactivos = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (Usuario usuario : usuarios) {
            LocalDate ultimaConexion = usuario.getUltimaConexion();
            if (ultimaConexion.isBefore(currentDate.minusMonths(6))) {
                usuariosInactivos.add(usuario);
            }
        }

        return usuariosInactivos;
    }

    public List<Usuario> getUsuariosConMasSeguidores() {
        List<Usuario> usuariosConMasSeguidores = new ArrayList<>();
        int maxSeguidores = 0;

        for (Usuario usuario : usuarios) {
            List<Integer> siguiendo = usuario.getSiguiendo();
            int cuentaSeguidores = siguiendo.size();

            if (cuentaSeguidores > maxSeguidores) {
                usuariosConMasSeguidores.clear();
                usuariosConMasSeguidores.add(usuario);
                maxSeguidores = cuentaSeguidores;
            } else if (cuentaSeguidores == maxSeguidores) {
                usuariosConMasSeguidores.add(usuario);
            }
        }

        return usuariosConMasSeguidores;
    }

    public List<Usuario> obtenerUsuariosConMitadSeguidoresInactivos() {
        return usuarios.stream()
                .filter(usuario -> {
                    int numSeguidores = usuario.getSiguiendo().size();
                    long numSeguidoresInactivos = usuario.getSiguiendo().stream()
                            .filter(seguido -> usuarios.stream()
                                    .anyMatch(u -> u.getId() == seguido && u.getUltimaConexion().isBefore(LocalDate.now().minusMonths(6)))) // Cambiar 6 por el número de meses considerado como inactivo
                            .count();
                    return numSeguidoresInactivos >= numSeguidores / 2.0;
                })
                .collect(Collectors.toList());
    }





}
