package com.example.demo.services;

import com.example.demo.models.UsuarioModel;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioModel crearUsuario(UsuarioModel usuario) {
        // Encriptar contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(encodedPassword);
        return usuarioRepository.save(usuario);
    }

    public List<UsuarioModel> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioModel> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<UsuarioModel> actualizarUsuario(Long id, UsuarioModel datosActualizados) {
        return usuarioRepository.findById(id)
            .map(usuario -> {
                usuario.setNombre(datosActualizados.getNombre());
                if (datosActualizados.getContrasena() != null && !datosActualizados.getContrasena().isEmpty()) {
                    usuario.setContrasena(passwordEncoder.encode(datosActualizados.getContrasena()));
                }
                return usuarioRepository.save(usuario);
            });
    }

    // Método para validar login
    public Optional<UsuarioModel> validarUsuario(String nombre, String contrasena) {
        UsuarioModel usuario = usuarioRepository.findByNombre(nombre);
        if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }
}
