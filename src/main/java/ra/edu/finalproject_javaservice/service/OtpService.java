package ra.edu.finalproject_javaservice.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private static final int MAX_ATTEMPTS = 3;
    private static final long EXPIRES_MINUTES = 5;
    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generate(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        store.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(EXPIRES_MINUTES), 0, false));
        return otp;
    }

    public void verify(String email, String otp) {
        OtpEntry entry = store.get(email);
        if (entry == null || entry.used || entry.expiresAt.isBefore(LocalDateTime.now())) {
            store.remove(email);
            throw new IllegalArgumentException("OTP is invalid or expired");
        }
        if (!entry.otp.equals(otp)) {
            int attempts = entry.attempts + 1;
            if (attempts >= MAX_ATTEMPTS) {
                store.remove(email);
                throw new IllegalArgumentException("OTP is invalid or expired");
            }
            store.put(email, new OtpEntry(entry.otp, entry.expiresAt, attempts, false));
            throw new IllegalArgumentException("OTP is invalid");
        }
        store.remove(email);
    }

    private record OtpEntry(String otp, LocalDateTime expiresAt, int attempts, boolean used) {}
}
