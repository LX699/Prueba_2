package ufro.cl.Prueba_2.model;

import java.time.LocalDate;
import java.util.List;

public class Usuario {

    private int id;
    private String correo;
    private LocalDate ultimaConexion;
    private List<Integer> siguiendo;

    public Usuario() {
    }

    public Usuario(int id, String correo, LocalDate ultimaConexion, List<Integer> siguiendo) {
        this.id = id;
        this.correo = correo;
        this.ultimaConexion = ultimaConexion;
        this.siguiendo = siguiendo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(LocalDate ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }

    public List<Integer> getSiguiendo() {
        return siguiendo;
    }

    public void setSiguiendo(List<Integer> siguiendo) {
        this.siguiendo = siguiendo;
    }
}
