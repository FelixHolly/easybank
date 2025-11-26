package at.holly.easybankbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test to verify the Spring application context loads correctly.
 * Uses H2 in-memory database for testing (configured in application-test.properties).
 */
@SpringBootTest
@ActiveProfiles("test")
class EasybankBackendApplicationTests {

    /**
     * Verifies that the Spring application context loads successfully.
     * This ensures all beans are properly configured and can be instantiated.
     */
    @Test
    void contextLoads() {
        // If this test passes, the application context loaded successfully
    }

}
