package authstream.application.services.hashing.config;

public class Argon2Config {
    private final String salt;
    private final int iterations;
    private final int memory;
    private final int parallelism;

    private Argon2Config(Builder builder) {
        this.salt = builder.salt != null ? builder.salt : "defaultSalt";
        this.iterations = builder.iterations > 0 ? builder.iterations : 1;
        this.memory = builder.memory > 0 ? builder.memory : 65536;
        this.parallelism = builder.parallelism > 0 ? builder.parallelism : 1;
    }

    public String getSalt() { return salt; }
    public int getIterations() { return iterations; }
    public int getMemory() { return memory; }
    public int getParallelism() { return parallelism; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String salt;
        private int iterations;
        private int memory;
        private int parallelism;

        public Builder salt(String salt) { this.salt = salt; return this; }
        public Builder iterations(int iterations) { this.iterations = iterations; return this; }
        public Builder memory(int memory) { this.memory = memory; return this; }
        public Builder parallelism(int parallelism) { this.parallelism = parallelism; return this; }
        public Argon2Config build() { return new Argon2Config(this); }
    }

    @Override
    public String toString() {
        return "Argon2Config{salt='" + salt + "', iterations=" + iterations +
                ", memory=" + memory + ", parallelism=" + parallelism + "}";
    }
}