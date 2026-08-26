package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.model.User;
import com.guilherme.splitwise_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> resultado = userRepository.findById(id);

        if (resultado.isPresent()) {
            return resultado.get();
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }

    public User updateUser(Long id, User updateUser){
        Optional<User> resultado = userRepository.findById(id);

        if (resultado.isPresent()) {
            User user = resultado.get();
            user.setName(updateUser.getName());
            user.setEmail(updateUser.getEmail());
            user.setPassword(updateUser.getPassword());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id:" + id);
        }
    }

    public void deleteUser(Long id){
        Optional<User> resultado = userRepository.findById(id);

        if (resultado.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User with id" + id + "does not exist.");
        }
    }

}