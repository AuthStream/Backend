package authstream.application.services.kv;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStoreService {
    private static final ConcurrentHashMap<String, TokenEntry> tokenStore = new ConcurrentHashMap<>();

    public TokenEntry create(String tokenKey, TokenEntry tokenEntry) {
        if (tokenKey == null || tokenEntry == null) {
            throw new IllegalArgumentException("Token key and entry must not be null");
        }
        TokenEntry existingEntry = tokenStore.putIfAbsent(tokenKey, tokenEntry);
        return existingEntry;
    }

    public TokenEntry read(String tokenKey) {
        if (tokenKey == null) {
            throw new IllegalArgumentException("Token key must not be null");
        }
        TokenEntry entry = tokenStore.get(tokenKey);
        if (entry != null && entry.isExpired()) {
            tokenStore.remove(tokenKey);
            return null;
        }
        return entry;
    }

    public boolean delete(String tokenKey) {
        if (tokenKey == null) {
            throw new IllegalArgumentException("Token key must not be null");
        }
        TokenEntry removed = tokenStore.remove(tokenKey);
        return removed != null;
    }

    public boolean existsAndValid(String tokenKey) {
        TokenEntry entry = tokenStore.get(tokenKey);
        if (entry == null || entry.isExpired()) {
            tokenStore.remove(tokenKey);
            return false;
        }
        return true;
    }

    public void cleanupExpired() {
        tokenStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public static void main(String[] args) {
        TokenStoreService service = new TokenStoreService();

        TokenEntry.Message message = new TokenEntry.Message("{encryptedBody123}");
        Instant createdAt = Instant.now();
        Instant expired = createdAt.plusSeconds(10); // Hết hạn sau 10 giây
        TokenEntry entry = new TokenEntry(message, createdAt, expired);


        TokenEntry existing = service.create("token1", entry);
        System.out.println("Create token1 - Existing entry: " + (existing == null ? "null (newly created)" : "not null"));
        TokenEntry duplicate = service.create("token1", entry);
        System.out.println("Create token1 again - Existing entry: " + (duplicate == null ? "null" : "not null (duplicate)"));

        TokenEntry readEntry = service.read("token1");
        if (readEntry != null) {
            System.out.println("Read token1 - Body: " + readEntry.getMessage().getBody());
            System.out.println("Read token1 - Remaining TTL: " + readEntry.getRemainingTTL() + " seconds");
        } else {
            System.out.println("Read token1: null");
        }

        boolean exists = service.existsAndValid("token1");
        System.out.println("Exists and valid token1: " + exists);

        boolean deleted = service.delete("token1");
        System.out.println("Delete token1: " + deleted);
        System.out.println("Read after delete: " + (service.read("token1") == null ? "null" : "not null"));

        TokenEntry shortLivedEntry = new TokenEntry(new TokenEntry.Message("tempBody"), Instant.now(), Instant.now().plusSeconds(2));
        service.create("token2", shortLivedEntry);
        System.out.println("Create token2 - Body: " + service.read("token2").getMessage().getBody());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Read expired token2 before cleanup: " + (service.read("token2") == null ? "null" : "not null"));
        service.cleanupExpired();
        System.out.println("After cleanup - Read token2: " + (service.read("token2") == null ? "null" : "not null"));

        System.out.println("Exists and valid for non-existent token: " + service.existsAndValid("token3"));
    }
}