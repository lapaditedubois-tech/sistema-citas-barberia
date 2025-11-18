package com.Neita.sistemacitasbarberia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/servicios")
    public String servicios() {
        return "servicios";
    }

    @GetMapping("/profesionales")
    public String profesionales() {
        return "profesionales";
    }

    @GetMapping("/galeria")
    public String galeria() {
        return "galeria";
    }

    @GetMapping("/productos")
    public String productos() {
        return "productos";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/profesional/dashboard")
    public String profesionalDashboard() {
        return "profesional-dashboard";
    }

    @GetMapping("/mis-citas")
    public String misCitas() {
        return "mis-citas";
    }
}
