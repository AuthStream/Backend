package authstream.application.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import authstream.application.dtos.UserDto;
import authstream.application.mappers.UserMapper;
import authstream.application.services.hashing.HashingService;
import authstream.application.services.hashing.HashingType;
import authstream.application.services.hashing.config.BcryptConfig;
import authstream.domain.entities.User;
import authstream.infrastructure.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final String DEFAULT_SALT = "$2a$12$Gbe4AzAQpfwu5bYRWhpiD.";
    private static final int DEFAULT_WORK_FACTOR = 12;
    private static final int THREAD_POOL_SIZE = 8; // Số thread tối đa
    BcryptConfig bcryptConfig = BcryptConfig.builder()
            .salt(DEFAULT_SALT)
            .workFactor(DEFAULT_WORK_FACTOR)
            .build();

    @Autowired
    private UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public UserDto createUser(UserDto dto) {
        if (userRepository.checkUsernameExists(dto.getUsername()) > 0) {
            throw new RuntimeException("Username already exists");
        }

        // Hash password trước khi lưu

        String hashedPassword = HashingService.hash(dto.getPassword(), HashingType.BCRYPT, bcryptConfig);

        // Lưu user với password đã hash
        userRepository.addUser(dto.getUsername(), hashedPassword);
        User createdUser = userRepository.getUserByUsername(dto.getUsername());
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

    public UserDto checkLogin(UserDto loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        User user = userRepository.getUserByUsername(loginRequest.getUsername());
        String hashedPassword = HashingService.hash(loginRequest.getPassword(), HashingType.BCRYPT, bcryptConfig);

        if (user == null || !user.getPassword().equals(hashedPassword)) {
            throw new RuntimeException("Invalid username or password");
        }

        logger.debug("User logged in: {}", user);
        return userMapper.toDto(user);
    }

    // Tạo bulk user từ CSV
    @Transactional
    public void createUsersFromCsv(MultipartFile file) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<UserDto> csvToBean = new CsvToBeanBuilder<UserDto>(reader)
                    .withType(UserDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(true)
                    .build();

            List<UserDto> users = csvToBean.parse();
            if (users.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty or invalid");
            }

            logger.info("Parsed " + users.size() + " users from CSV");
            users.forEach(
                    dto -> logger.info("UserDto: username=" + dto.getUsername() + ", password=" + dto.getPassword()));

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            List<User> userEntities = users.stream()
                    .map(dto -> {
                        if (dto.getUsername() == null) {
                            throw new IllegalArgumentException("Username cannot be null in DTO: " + dto);
                        }
                        User user = new User();
                        user.setId(UUID.randomUUID());
                        user.setUsername(dto.getUsername());
                        user.setPassword(dto.getPassword());
                        return user; // Chưa hash ở đây
                    })
                    .collect(Collectors.toList());

            userEntities.forEach(user -> executor.submit(() -> {
                String hashedPassword = HashingService.hash(user.getPassword(), HashingType.BCRYPT, bcryptConfig);
                user.setPassword(hashedPassword);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                logger.info("Hashed User: id=" + user.getId() + ", username=" + user.getUsername()
                        + ", password=" + user.getPassword());
            }));

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            logger.info("Saving " + userEntities.size() + " users to DB");
            userRepository.saveAll(userEntities);
            logger.info("Successfully saved all users");
        } catch (Exception e) {
            throw e;
        }
    }

    public Pair<User, Object> findByUsername(String username) {
        try {
            User user = userRepository.getUserByUsername(username);

            if (user == null) {
                return Pair.of(null, "User not found");

            }
            return Pair.of(user, null);

        } catch (Exception e) {
            return Pair.of(null, "Something wrong with sever: " + e.getMessage());
        }
    }
}