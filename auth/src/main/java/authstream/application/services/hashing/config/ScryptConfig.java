package authstream.application.services.hashing.config;

public class ScryptConfig {
    private final String salt;
    private final int n;
    private final int r;
    private final int p;
    private final int keyLength;

    private ScryptConfig(Builder builder) {
        this.salt = builder.salt != null ? builder.salt : "defaultSalt";
        this.n = builder.n > 0 ? builder.n : 16384;
        this.r = builder.r > 0 ? builder.r : 8;
        this.p = builder.p > 0 ? builder.p : 1;
        this.keyLength = builder.keyLength > 0 ? builder.keyLength : 32;
    }

    public String getSalt() { return salt; }
    public int getN() { return n; }
    public int getR() { return r; }
    public int getP() { return p; }
    public int getKeyLength() { return keyLength; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String salt;
        private int n;
        private int r;
        private int p;
        private int keyLength;

        public Builder salt(String salt) { this.salt = salt; return this; }
        public Builder n(int n) { this.n = n; return this; }
        public Builder r(int r) { this.r = r; return this; }
        public Builder p(int p) { this.p = p; return this; }
        public Builder keyLength(int keyLength) { this.keyLength = keyLength; return this; }
        public ScryptConfig build() { return new ScryptConfig(this); }
    }

    @Override
    public String toString() {
        return "ScryptConfig{salt='" + salt + "', n=" + n + ", r=" + r +
                ", p=" + p + ", keyLength=" + keyLength + "}";
    }
}