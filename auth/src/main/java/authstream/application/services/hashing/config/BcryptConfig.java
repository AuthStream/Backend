package authstream.application.services.hashing.config;

public class BcryptConfig {
    private final String salt;
    private final int workFactor;

    private BcryptConfig(Builder builder) {
        this.salt = builder.salt != null ? builder.salt : "defaultSalt";
        this.workFactor = builder.workFactor > 0 ? builder.workFactor : 12;
    }

    public String getSalt() { return salt; }
    public int getWorkFactor() { return workFactor; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String salt;
        private int workFactor;

        public Builder salt(String salt) { this.salt = salt; return this; }
        public Builder workFactor(int workFactor) { this.workFactor = workFactor; return this; }
        public BcryptConfig build() { return new BcryptConfig(this); }
    }

    @Override
    public String toString() {
        return "BcryptConfig{salt='" + salt + "', workFactor=" + workFactor + "}";
    }
}