package authstream.application.services.hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.mindrot.jbcrypt.BCrypt;

import com.lambdaworks.crypto.SCrypt;

import authstream.application.services.hashing.config.Argon2Config;
import authstream.application.services.hashing.config.BcryptConfig;
import authstream.application.services.hashing.config.Pbkdf2Config;
import authstream.application.services.hashing.config.ScryptConfig;
import authstream.application.services.hashing.config.Sha256Config;
import authstream.application.services.hashing.config.Sha512Config;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class HashingService {

    public static String hash(String text, HashingType encryptionType, Object config) {
        if (text == null || encryptionType == null || config == null) {
            throw new IllegalArgumentException("Text, HashingType, and config must not be null");
        }

        try {
            switch (encryptionType) {
                case BCRYPT:
                    BcryptConfig bcryptConfig = (BcryptConfig) config;
                    return BCrypt.hashpw(text, bcryptConfig.getSalt());

                case ARGON2:
                    Argon2Config argon2Config = (Argon2Config) config;
                    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
                    return argon2.hash(argon2Config.getIterations(), argon2Config.getMemory(),
                            argon2Config.getParallelism(), text.toCharArray());

                case PBKDF2:
                    Pbkdf2Config pbkdf2Config = (Pbkdf2Config) config;
                    char[] chars = text.toCharArray();
                    byte[] salt = pbkdf2Config.getSalt().getBytes(StandardCharsets.UTF_8);
                    PBEKeySpec spec = new PBEKeySpec(chars, salt, pbkdf2Config.getIterations(),
                            pbkdf2Config.getKeyLength());
                    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                    byte[] hash = skf.generateSecret(spec).getEncoded();
                    return Base64.getEncoder().encodeToString(hash);

                case SHA256:
                    Sha256Config sha256Config = (Sha256Config) config;
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    String saltedText = text + sha256Config.getSalt();
                    byte[] sha256Hash = sha256.digest(saltedText.getBytes(StandardCharsets.UTF_8));
                    return Base64.getEncoder().encodeToString(sha256Hash);

                case SHA512:
                    Sha512Config sha512Config = (Sha512Config) config;
                    MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
                    String saltedSha512Text = text + sha512Config.getSalt();
                    byte[] sha512Hash = sha512.digest(saltedSha512Text.getBytes(StandardCharsets.UTF_8));
                    return Base64.getEncoder().encodeToString(sha512Hash);

                case SCRYPT:
                    ScryptConfig scryptConfig = (ScryptConfig) config;
                    byte[] scryptHash = SCrypt.scrypt(text.getBytes(StandardCharsets.UTF_8),
                            scryptConfig.getSalt().getBytes(StandardCharsets.UTF_8),
                            scryptConfig.getN(), scryptConfig.getR(), scryptConfig.getP(),
                            scryptConfig.getKeyLength());
                    return Base64.getEncoder().encodeToString(scryptHash);

                default:
                    throw new IllegalArgumentException("Unsupported hashing type: " + encryptionType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during hashing: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        String text = "myPassword123";

        BcryptConfig bcryptConfig = BcryptConfig.builder().workFactor(10).build();
        System.out.println("BCRYPT: " + hash(text, HashingType.BCRYPT, bcryptConfig));

        Argon2Config argon2Config = Argon2Config.builder().iterations(5).memory(32768).parallelism(2).build();
        System.out.println("ARGON2: " + hash(text, HashingType.ARGON2, argon2Config));

        Pbkdf2Config pbkdf2Config = Pbkdf2Config.builder().iterations(20000).keyLength(512).build();
        System.out.println("PBKDF2: " + hash(text, HashingType.PBKDF2, pbkdf2Config));

        Sha256Config sha256Config = Sha256Config.builder().salt("shaSalt").build();
        System.out.println("SHA256: " + hash(text, HashingType.SHA256, sha256Config));

        Sha512Config sha512Config = Sha512Config.builder().salt("shaSalt").build();
        System.out.println("SHA512: " + hash(text, HashingType.SHA512, sha512Config));

        ScryptConfig scryptConfig = ScryptConfig.builder().n(32768).r(16).p(2).keyLength(64).build();
        System.out.println("SCRYPT: " + hash(text, HashingType.SCRYPT, scryptConfig));
    }
}