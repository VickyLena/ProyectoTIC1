package com.wtfcinema.demo.controller;
import com.wtfcinema.demo.entities.Employee;
import com.wtfcinema.demo.entities.User;
import com.wtfcinema.demo.services.EmployeeServices;
import com.wtfcinema.demo.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private EmployeeServices employeeServices;

    @PostMapping("/submitLogin")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, RedirectAttributes redirectAttributes) {
        // Llamar a UserServices para verificar si el email corresponde a un usuario
        User usuario=userServices.getByEmail(email);
        if (usuario != null) {
            if (usuario.getPassword().equals(password)) {
                redirectAttributes.addFlashAttribute("message", "Login successful for User!");
                return "redirect:/movies";
            }
            redirectAttributes.addFlashAttribute("error", "Login Unsuccessful: User password does not match!");
            return "redirect:/login";
        }

        // Llamar a EmployeeServices si no es usuario
        Employee empleado=employeeServices.getByEmail(email);
        if (empleado != null) {
            if (empleado.getPassword().equals(password)) {
                redirectAttributes.addFlashAttribute("message", "Login successful for Emlpoyee!");
                return "redirect:/movies";
            }
            redirectAttributes.addFlashAttribute("error", "Login Unsuccessful: Employee password does not match!");
            return "redirect:/login";
        }

        // Si no está en ninguno, mostrar mensaje de error
        redirectAttributes.addFlashAttribute("error", "Login Unsuccessful: Email not attached to a User or Employee");
        return "redirect:/login";
    }
}


