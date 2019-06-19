package dev.mvvasilev.service;

import dev.mvvasilev.entity.User;
import dev.mvvasilev.exception.UserNotFoundException;
import dev.mvvasilev.exception.ValidationException;
import dev.mvvasilev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Miroslav Vasilev
 */
@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private StringKeyGenerator tokenGenerator;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, StringKeyGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * Create a new user and store into the database.
     *
     * @param email       The user's email
     * @param rawPassword The user's password
     * @param firstName   The user's first name
     * @param lastName    The user's last name
     * @param dateOfBirth The user's date of birth
     */
    public long createUser(String email, String rawPassword, String firstName, String lastName, LocalDate dateOfBirth) {
        User user = new User();

        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDateOfBirth(dateOfBirth);

        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        user = userRepository.save(user);

        return user.getId();
    }

    /**
     * Retrieve a user from the database by their id, or throw an exception if no such user entity could be found.
     *
     * @param id The user id to search for
     * @return The user entity
     * @throws UserNotFoundException If no user with the provided id could be found
     */
    public User getUser(long id) {
        Optional<User> userById = userRepository.findById(id);

        if (!userById.isPresent()) {
            throw new UserNotFoundException("No user with an id of '" + id + "' could be found.");
        }

        return userById.get();
    }

    /**
     * Updates user information.
     * All parameters except the user id are optional.
     * If a parameter is null/empty, or equal to the same value as it currently exists on the User entity, it will not be updated.
     *
     * @param id             The id of the user
     * @param newEmail       The new email of the user ( optional )
     * @param newFirstName   The new first name of the user ( optional )
     * @param newLastName    The new last name of the user ( optional )
     * @param newDateOfBirth The new date of birth of the user ( optional )
     */
    public User updateUser(long id, String newEmail, String newFirstName, String newLastName, LocalDate newDateOfBirth) {
        User user = getUser(id);

        // When setting the email of the user, must ensure it does not already exist in the database
        if (!ObjectUtils.isEmpty(newEmail) && !user.getEmail().equals(newEmail)) {
            user.setEmail(newEmail);
        }

        if (!ObjectUtils.isEmpty(newFirstName) && !user.getFirstName().equals(newFirstName)) {
            user.setFirstName(newFirstName);
        }

        if (!ObjectUtils.isEmpty(newLastName) && !user.getLastName().equals(newLastName)) {
            user.setLastName(newLastName);
        }

        if (newDateOfBirth != null && !user.getDateOfBirth().equals(newDateOfBirth)) {
            user.setDateOfBirth(newDateOfBirth);
        }

        userRepository.save(user);

        return user;
    }

    /**
     * Deletes a user by their id.
     *
     * @param userId The id of the user entity which is to be deleted
     */
    public void deleteUser(Long userId) {
        userRepository.deleteUserById(userId);
    }

    public Page<User> getUsers(Pageable pageable) {
        Assert.notNull(pageable, "pageable cannot be null");

        return userRepository.findAll(pageable);
    }

    public String generateUserToken(String email, String rawPassword) {
        Optional<User> userByEmail = userRepository.getUserByEmail(email);

        if (!userByEmail.isPresent()) {
            throw new UserNotFoundException("No user with that email could be found");
        }

        if (passwordEncoder.matches(rawPassword, userByEmail.get().getPasswordHash())) {
            throw new ValidationException("Incorrect password");
        }

        String token = tokenGenerator.generateKey();

        userByEmail.get().setToken(token);

        userRepository.save(userByEmail.get());

        return token;
    }

    public boolean validateUserToken(String authToken) {
        return userRepository.getUserByToken(authToken).isPresent();
    }
}
