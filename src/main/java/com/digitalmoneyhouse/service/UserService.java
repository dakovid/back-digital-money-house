package com.digitalmoneyhouse.service;

import com.digitalmoneyhouse.model.Account;
import com.digitalmoneyhouse.model.User;
import com.digitalmoneyhouse.repository.AccountRepository;
import com.digitalmoneyhouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final Random random = new Random();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) throws IOException {
        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generar CVU y alias
        user.setCvu(generateCVU());
        user.setAlias(generateAlias());

        User savedUser = userRepository.save(user);

        // Crear una cuenta asociada al usuario
        Account account = new Account();
        account.setUser(savedUser);
        account.setBalance(0.0);
        accountRepository.save(account);

        return savedUser;
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(Long id, User userData) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(userData.getNombre() != null)
            existingUser.setNombre(userData.getNombre());
        if(userData.getApellido() != null)
            existingUser.setApellido(userData.getApellido());
        if(userData.getTelefono() != null)
            existingUser.setTelefono(userData.getTelefono());

        return userRepository.save(existingUser);
    }

    private String generateCVU() {
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();
    }

    private String generateAlias() throws IOException {
        // Obtener el InputStream del recurso alias.txt
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("alias.txt");
        if (inputStream == null) {
            throw new IOException("El archivo alias.txt no se encontró en el classpath.");
        }

        // Leer todas las líneas y filtrarlas para eliminar líneas vacías
        List<String> words = new BufferedReader(new InputStreamReader(inputStream))
                                  .lines()
                                  .map(String::trim)
                                  .filter(line -> !line.isEmpty())
                                  .collect(Collectors.toList());

        if (words.isEmpty()) {
            throw new IllegalStateException("El archivo alias.txt está vacío o no contiene palabras válidas.");
        }

        // Seleccionar aleatoriamente tres palabras
        String word1 = words.get(random.nextInt(words.size()));
        String word2 = words.get(random.nextInt(words.size()));
        String word3 = words.get(random.nextInt(words.size()));

        return word1 + "." + word2 + "." + word3;
    }
}

