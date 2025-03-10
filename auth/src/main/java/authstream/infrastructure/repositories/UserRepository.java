package authstream.infrastructure.repositories;

import authstream.domain.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    String getAllUsersQuery = "SELECT * FROM users";
    String getUserByIdQuery = "SELECT * FROM users WHERE user_id = :id";
    String getUserByUsernameQuery = "SELECT * FROM users WHERE username = :username";
    String addUserQuery = "INSERT INTO users (username, password) VALUES (:username, :password)";
    String updateUserQuery = "UPDATE users SET username = :newUsername, password = :newPassword WHERE user_id = :id";
    String deleteUserQuery = "DELETE FROM users WHERE user_id = :id";
    String checkUsernameExistsQuery = "SELECT COUNT(*) FROM users WHERE username = :username";

    @Query(value = getAllUsersQuery, nativeQuery = true)
    List<User> getAllUsers();

    @Query(value = getUserByIdQuery, nativeQuery = true)
    User getUserById(@Param("id") UUID id);

    @Query(value = getUserByUsernameQuery, nativeQuery = true)
    User getUserByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = addUserQuery, nativeQuery = true)
    int addUser(@Param("username") String username, @Param("password") String password);

    @Modifying
    @Transactional
    @Query(value = updateUserQuery, nativeQuery = true)
    int updateUser(@Param("id") UUID id, @Param("newUsername") String newUsername,
            @Param("newPassword") String newPassword);

    @Modifying
    @Transactional
    @Query(value = deleteUserQuery, nativeQuery = true)
    int deleteUser(@Param("id") UUID id);

    @Query(value = checkUsernameExistsQuery, nativeQuery = true)
    int checkUsernameExists(@Param("username") String username);
}