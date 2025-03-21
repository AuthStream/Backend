package authstream.application.services.hashing.config;

public class Pbkdf2Config {
    private final String salt;
    private final int iterations;
    private final int keyLength;

    private Pbkdf2Config(Builder builder) {
        this.salt = builder.salt != null ? builder.salt : "defaultSalt";
        this.iterations = builder.iterations > 0 ? builder.iterations : 1;
        this.keyLength = builder.keyLength > 0 ? builder.keyLength : 256;
    }

    public String getSalt() { return salt; }
    public int getIterations() { return iterations; }
    public int getKeyLength() { return keyLength; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String salt;
        private int iterations;
        private int keyLength;

        public Builder salt(String salt) { this.salt = salt; return this; }
        public Builder iterations(int iterations) { this.iterations = iterations; return this; }
        public Builder keyLength(int keyLength) { this.keyLength = keyLength; return this; }
        public Pbkdf2Config build() { return new Pbkdf2Config(this); }
    }

    @Override
    public String toString() {
        return "Pbkdf2Config{salt='" + salt + "', iterations=" + iterations +
                ", keyLength=" + keyLength + "}";
    }
}