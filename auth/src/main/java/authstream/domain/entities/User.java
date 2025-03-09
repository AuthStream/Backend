package authstream.domain.entities;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    private String username;
    private String password;
}
