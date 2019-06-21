package dev.mvvasilev.service;

import dev.mvvasilev.entity.User;
import dev.mvvasilev.exception.UserNotFoundException;
import dev.mvvasilev.exception.ValidationException;
import dev.mvvasilev.repository.UserRepository;
import dev.mvvasilev.security.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Miroslav Vasilev
 */
@Service
@Transactional
public class UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private AuthenticationService authenticationService;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
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

        Set<Permission> defaultPermissions = new HashSet<>();
        defaultPermissions.add(Permission.READ_OTHER_USER);
        defaultPermissions.add(Permission.READ_SELF);
        defaultPermissions.add(Permission.UPDATE_SELF);
        defaultPermissions.add(Permission.DELETE_SELF);
        user.setPermissions(defaultPermissions);

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
     * Retrieve a user from the database by their email, or throw an exception if no such user entity could be found.
     *
     * @param email The email to search for
     * @return The user entity
     * @throws UserNotFoundException If no user with the provided email could be found
     */
    public User getUser(String email) {
        Optional<User> userByEmail = userRepository.getUserByEmail(email);

        if (!userByEmail.isPresent()) {
            throw new UserNotFoundException("No user with an email of '" + email + "' could be found.");
        }

        return userByEmail.get();
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
    public User updateUserById(long id, String newEmail, String newFirstName, String newLastName, LocalDate newDateOfBirth) {
        User user = getUser(id);
        updateUser(user, newEmail, newFirstName, newLastName, newDateOfBirth);
        return user;
    }

    /**
     * Updates user information.
     * All parameters except the user's old email are optional.
     * If a parameter is null/empty, or equal to the same value as it currently exists on the User entity, it will not be updated.
     *
     * @param oldEmail       The old email of the user
     * @param newEmail       The new email of the user ( optional )
     * @param newFirstName   The new first name of the user ( optional )
     * @param newLastName    The new last name of the user ( optional )
     * @param newDateOfBirth The new date of birth of the user ( optional )
     */
    public User updateUserByEmail(String oldEmail, String newEmail, String newFirstName, String newLastName, LocalDate newDateOfBirth) {
        User user = getUser(oldEmail);
        updateUser(user, newEmail, newFirstName, newLastName, newDateOfBirth);
        return user;
    }

    protected void updateUser(User user, String newEmail, String newFirstName, String newLastName, LocalDate newDateOfBirth) {
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
    }

    /**
     * Deletes a user by their id.
     *
     * @param userId The id of the user entity which is to be deleted
     */
    public void deleteUserById(Long userId) {
        userRepository.deleteUserById(userId);
    }

    /**
     * Deletes a user by their email.
     *
     * @param email The email of the user entity which is to be deleted
     */
    public void deleteUserByEmail(String email) {
        userRepository.deleteUserByEmail(email);
    }

    public Page<User> getUsers(Pageable pageable) {
        Assert.notNull(pageable, "pageable cannot be null");

        return userRepository.findAll(pageable);
    }

    public String fetchUserJWT(String email, String rawPassword) {
        Optional<User> userByEmail = userRepository.getUserByEmail(email);

        if (!userByEmail.isPresent()) {
            throw new UserNotFoundException("Could not find user with the provided email address");
        }

        User user = userByEmail.get();

        if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return authenticationService.createToken(email, user.getPermissions());
        } else {
            throw new ValidationException("User could not be authenticated");
        }
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
