package authstream.application.services;

import authstream.application.dtos.UserDto;
import authstream.domain.entities.User;
import authstream.application.mappers.UserMapper;
import authstream.infrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserDto dto) {
        if (userRepository.checkUsernameExists(dto.getUsername()) > 0) {
            throw new RuntimeException("Username already exists");
        }
        userRepository.addUser(dto.getUsername(), dto.getPassword());
        User createdUser = userRepository.getUserByUsername(dto.getUsername()); // Lấy user vừa tạo
        return userMapper.toDto(createdUser);
    }

    public UserDto getUserById(UUID id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return userMapper.toDto(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(UUID id, UserDto dto) {
        User existingUser = userRepository.getUserById(id);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        if (!existingUser.getUsername().equals(dto.getUsername()) &&
                userRepository.checkUsernameExists(dto.getUsername()) > 0) {
            throw new RuntimeException("Username already taken");
        }
        userRepository.updateUser(id, dto.getUsername(), dto.getPassword());
        return userMapper.toDto(userRepository.getUserById(id));
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (userRepository.getUserById(id) == null) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteUser(id);
    }
}