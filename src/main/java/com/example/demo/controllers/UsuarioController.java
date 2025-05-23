package com.example.demo.controllers;

import com.example.demo.models.UsuarioModel;
import com.example.demo.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioModel usuario) {
        UsuarioModel existente = usuarioService.obtenerTodos()
            .stream()
            .filter(u -> u.getNombre().equalsIgnoreCase(usuario.getNombre()))
            .findFirst()
            .orElse(null);
        if (existente != null) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }

        UsuarioModel usuarioCreado = usuarioService.crearUsuario(usuario);
        return ResponseEntity.ok(usuarioCreado);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> usuarioLogin) {
    String nombre = usuarioLogin.get("nombre");
    String contrasena = usuarioLogin.get("contrasena");

    Optional<UsuarioModel> usuarioValidado = usuarioService.validarUsuario(nombre, contrasena);
    if (usuarioValidado.isPresent()) {
        // Devuelve algún JSON, por ejemplo:
        return ResponseEntity.ok(Map.of("mensaje", "Login exitoso", "usuario", usuarioValidado.get().getNombre()));
    } else {
        return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
    }
}


    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioModel>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    // Opcionales adicionales (por si los necesitas en frontend)
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioModel datos) {
        return usuarioService.actualizarUsuario(id, datos)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }
}
