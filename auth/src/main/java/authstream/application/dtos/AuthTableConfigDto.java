package authstream.application.dtos;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthTableConfigDto {

    private UUID id;
    private String userTable;
    private String passwordAttribute;
    private String usernameAttribute;
    private String hashingType; // "BCRYPT", "ARGON2", v.v.
    private String salt;
    private Object hashConfig; // Động: BcryptConfig, Argon2Config, v.v.

    public AuthTableConfigDto(UUID id, String userTable, String usernameAttribute, String passwordAttribute,
            String hashingType, String salt, Object hashConfig) {
        this.id = id;
        this.userTable = userTable;
        this.usernameAttribute=usernameAttribute;
        this.passwordAttribute = passwordAttribute;
        this.salt = salt;
        this.hashingType = hashingType;
        this.hashConfig = hashConfig;
    }
}