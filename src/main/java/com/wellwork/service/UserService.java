package com.wellwork.service;

import com.wellwork.dto.UserRequestDTO;
import com.wellwork.dto.UserResponseDTO;
import com.wellwork.model.entities.User;
import com.wellwork.repository.UserRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate; // opcional

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RabbitTemplate rabbitTemplate // pode vir null se não houver Rabbit
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username já existe");
        }

        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(u);

        // =====================================
        // ENVIO OPCIONAL PARA O RABBITMQ
        // =====================================
        if (rabbitTemplate != null) {
            try {
                String msg = "Bem-vindo(a), " + u.getUsername() + "! Sua conta foi criada com sucesso.";
                rabbitTemplate.convertAndSend(
                        "user.exchange",
                        "user.welcome",
                        msg
                );
            } catch (AmqpException e) {
                System.out.println("⚠️ RabbitMQ indisponível. Mensagem NÃO enviada. (continua normal)");
            }
        } else {
            System.out.println("⚠️ RabbitMQ não configurado. Mensageria ignorada.");
        }

        return toResponse(u);
    }

    public UserResponseDTO getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User não encontrado: " + id));
        return toResponse(u);
    }

    public Page<UserResponseDTO> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User não encontrado: " + userId));
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User findEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User não encontrado: " + username));
    }

    private UserResponseDTO toResponse(User u) {
        UserResponseDTO r = new UserResponseDTO();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        return r;
    }

    public UserResponseDTO findByUsernameResponse(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User não encontrado: " + username));

        return toResponse(u);
    }
}
