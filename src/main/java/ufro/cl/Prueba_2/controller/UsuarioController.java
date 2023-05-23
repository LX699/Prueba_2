package ufro.cl.Prueba_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ufro.cl.Prueba_2.model.Usuario;
import ufro.cl.Prueba_2.service.UsuarioService;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping(value = "usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/lista")
    public List<Usuario> obtenerUsuarios() throws IOException {
        return usuarioService.leerUsuarios();
    }

    @GetMapping("/ultimos-conectados")
    public List<Usuario> getUltimosUsuariosConectados(@RequestParam(defaultValue = "10") int cantidad) {
        return usuarioService.getUltimosUsuariosConectados(cantidad);
    }

    @GetMapping("/populares")
    public List<Usuario> getUsuariosPopulares() {
        return usuarioService.getUsuariosPopulares();
    }
    @GetMapping("/inactivosMayoresSeguidores")
    public ResponseEntity<List<Usuario>> obtenerUsuarioInactivoMayorSeguidores() throws IOException {
        List<Usuario> usuarios = usuarioService.obtenerUsuarioInactivoMayorSeguidores();
        return ResponseEntity.ok(usuarios);
    }

}
