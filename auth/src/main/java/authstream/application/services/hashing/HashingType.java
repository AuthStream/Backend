package authstream.application.services.hashing;

public enum HashingType {
    BCRYPT,
    ARGON2,
    PBKDF2,
    SHA256,
    SHA512,
    SCRYPT
}