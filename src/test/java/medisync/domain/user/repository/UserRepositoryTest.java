package medisync.domain.user.repository;

import common.BaseRepositoryTest;
import medisync.domain.user.entity.User;
import medisync.domain.user.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void 이메일로_유저_조회() {
        // given
        User user = User.builder()
                .name("테스터1")
                .email("tester1@test.com")
                .birthDay(LocalDate.parse("1990-01-01"))
                .role(Role.PATIENT)
                .phone("010-1234-5678")
                .build();
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByEmail("tester1@test.com");

        // then
        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void 이메일_존재하면_true() {
        // given
        User user = User.builder()
                .name("테스터1")
                .email("tester1@test.com")
                .birthDay(LocalDate.parse("1990-01-01"))
                .role(Role.PATIENT)
                .phone("010-1234-5678")
                .build();
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail(user.getEmail());

        // then
        assertTrue(result);
    }

    @Test
    void 이메일_존재하지_않으면_false() {
        // when
        boolean result = userRepository.existsByEmail("notfound@test.com");

        // then
        assertFalse(result);
    }
}
