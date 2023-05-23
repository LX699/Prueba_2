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
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;


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
        LocalDate currentDate = LocalDate.of(2023, 5, 22); // Establecer la fecha actual

        for (Usuario usuario : usuarios) {
            LocalDate ultimaConexion = usuario.getUltimaConexion();
            if (ultimaConexion.isBefore(currentDate.minusYears(4))) { // Cambiar de meses a años
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

    public List<Usuario> getUsuariosConSeguidoresInactivos() {
        List<Usuario> usuariosConSeguidoresInactivos = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2023, 5, 22); // Establecer la fecha actual

        for (Usuario usuario : usuarios) {
            List<Integer> siguiendo = usuario.getSiguiendo();
            int totalCuentas = siguiendo.size();
            int inactivas = 0;

            for (Integer cuentaId : siguiendo) {
                Usuario cuenta = buscarUsuarioPorId(cuentaId);
                if (cuenta != null) {
                    LocalDate ultimaConexion = cuenta.getUltimaConexion();
                    if (ultimaConexion.isBefore(currentDate.minusYears(4))) {
                        inactivas++;
                    }
                }
            }

            if (inactivas >= totalCuentas / 2) {
                usuariosConSeguidoresInactivos.add(usuario);
            }
        }

        return usuariosConSeguidoresInactivos;
    }

    private Usuario buscarUsuarioPorId(int id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        return null;
    }

    public List<Usuario> getUltimosUsuariosConectados(int cantidad) {
        List<Usuario> ultimosUsuariosConectados = new ArrayList<>(usuarios);
        ultimosUsuariosConectados.sort(Comparator.comparing(Usuario::getUltimaConexion).reversed());
        return ultimosUsuariosConectados.subList(0, Math.min(cantidad, ultimosUsuariosConectados.size()));

    }

    public List<Usuario> getUsuariosPopulares() {
        List<Usuario> usuariosPopulares = new ArrayList<>();
        int maxSeguidores = 0;
    
        for (Usuario usuario : usuarios) {
            int cuentaSeguidores = usuario.getSiguiendo().size();
    
            if (cuentaSeguidores > maxSeguidores) {
                usuariosPopulares.clear();
                usuariosPopulares.add(usuario);
                maxSeguidores = cuentaSeguidores;
            } else if (cuentaSeguidores == maxSeguidores) {
                usuariosPopulares.add(usuario);
            }
        }
    
        return usuariosPopulares;
    }


    public List<Usuario> obtenerUsuarioInactivoMayorSeguidores() throws IOException {
        List<Usuario> usuarios = leerUsuarios(); // Llamada al método leerUsuarios() para obtener la lista de usuarios
        List<Usuario> usuariosInactivos = getUsuariosInactivos(); // Llamada al método getUsuariosInactivos() para obtener la lista de usuarios inactivos
    
        // Variables para almacenar el máximo número de seguidores y los usuarios inactivos correspondientes
        int maxSeguidores = 0;
        List<Usuario> usuariosConMaxSeguidores = new ArrayList<>();
    
        for (Usuario usuario : usuariosInactivos) {
            int numSeguidores = usuario.getSiguiendo().size();
            if (numSeguidores > maxSeguidores) {
                maxSeguidores = numSeguidores;
                usuariosConMaxSeguidores.clear();
                usuariosConMaxSeguidores.add(usuario);
            } else if (numSeguidores == maxSeguidores) {
                usuariosConMaxSeguidores.add(usuario);
            }
        }
    
        return usuariosConMaxSeguidores;
    }
}
