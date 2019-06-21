package dev.mvvasilev.repository;

import dev.mvvasilev.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Miroslav Vasilev
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getUserByEmail(String email);

    void deleteUserById(Long id);

    void deleteUserByEmail(String email);
}
