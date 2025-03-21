package authstream.application.services.hashing.config;

public class Sha256Config {
    private final String salt;

    private Sha256Config(Builder builder) {
        this.salt = builder.salt != null ? builder.salt : "defaultSalt";
    }

    public String getSalt() { return salt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String salt;

        public Builder salt(String salt) { this.salt = salt; return this; }
        public Sha256Config build() { return new Sha256Config(this); }
    }

    @Override
    public String toString() {
        return "Sha256Config{salt='" + salt + "'}";
    }
}