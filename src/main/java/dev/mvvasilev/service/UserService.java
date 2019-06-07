package dev.mvvasilev.service;

import dev.mvvasilev.entity.User;
import dev.mvvasilev.exception.UserNotFoundException;
import dev.mvvasilev.exception.ValidationException;
import dev.mvvasilev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        if (validateEmailDoesNotExist(email)) {
            user.setEmail(email);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);

        if (validateDateOfBirth(dateOfBirth)) {
            user.setDateOfBirth(dateOfBirth);
        }

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
        if (!ObjectUtils.isEmpty(newEmail) && !user.getEmail().equals(newEmail) && validateEmailDoesNotExist(newEmail)) {
            user.setEmail(newEmail);
        }

        if (!ObjectUtils.isEmpty(newFirstName) && !user.getFirstName().equals(newFirstName)) {
            user.setFirstName(newFirstName);
        }

        if (!ObjectUtils.isEmpty(newLastName) && !user.getLastName().equals(newLastName)) {
            user.setLastName(newLastName);
        }

        if (newDateOfBirth != null && !user.getDateOfBirth().equals(newDateOfBirth) && validateDateOfBirth(newDateOfBirth)) {
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

    private boolean validateEmailDoesNotExist(String email) {
        Assert.notNull(email, "email cannot be null");

        Optional<User> userByEmail = userRepository.getUserByEmail(email);

        // If a user with this email already exists, throw an exception.
        if (userByEmail.isPresent()) {
            throw new ValidationException("A user with this email address already exists.");
        }

        return true;
    }

    private boolean validateDateOfBirth(LocalDate date) {
        Assert.notNull(date, "date cannot be null");

        LocalDate currentDate = LocalDate.now();

        if (currentDate.isBefore(date)) {
            throw new ValidationException("The date of birth cannot be in the future.");
        }

        return true;
    }
}
