package dev.mvvasilev.service;

import dev.mvvasilev.entity.User;
import dev.mvvasilev.exception.UserNotFoundException;
import dev.mvvasilev.exception.ValidationException;
import dev.mvvasilev.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final long USER_ID = 123L;

    private static final String EMAIL = "someemail@domain.com";

    private static final String FIRST_NAME = "First";

    private static final String LAST_NAME = "Last";

    private static final String PASSWORD = "test_password123";

    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1997, Month.DECEMBER, 6);

    private static final String UPDATED_EMAIL = "updatedemail@domain.com";

    private static final String UPDATED_FIRST_NAME = "Updated First";

    private static final String UPDATED_LAST_NAME = "Updated Last";

    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.of(1998, Month.AUGUST, 13);

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Spy
    private ModelMapper modelMapper;

    private User createdUser;

    private User createdUserWithId;

    private User updatedUser;

    @Before
    public void init() {
        userService = new UserService(userRepository, passwordEncoder, tokenProvider);

        createdUser = new User();
        createdUser.setEmail(EMAIL);
        createdUser.setFirstName(FIRST_NAME);
        createdUser.setLastName(LAST_NAME);
        createdUser.setPasswordHash(passwordEncoder.encode(PASSWORD));
        createdUser.setDateOfBirth(DATE_OF_BIRTH);

        createdUserWithId = new User();
        modelMapper.map(createdUser, createdUserWithId);
        createdUserWithId.setId(USER_ID);

        updatedUser = new User();
        updatedUser.setId(USER_ID);
        updatedUser.setEmail(UPDATED_EMAIL);
        updatedUser.setFirstName(UPDATED_FIRST_NAME);
        updatedUser.setLastName(UPDATED_LAST_NAME);
        updatedUser.setPasswordHash(createdUser.getPasswordHash());
        updatedUser.setDateOfBirth(UPDATED_DATE_OF_BIRTH);
    }

    @Test
    public void testCreateUser() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(createdUserWithId);
        Mockito.when(userRepository.getUserByEmail(EMAIL)).thenReturn(Optional.empty());

        long id = userService.createUser(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, DATE_OF_BIRTH);

        Assert.assertEquals(USER_ID, id);
    }

    @Test(expected = ValidationException.class)
    public void testCreateUser_withAlreadyExistingEmail() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(createdUserWithId);
        Mockito.when(userRepository.getUserByEmail(EMAIL)).thenReturn(Optional.of(createdUserWithId));

        userService.createUser(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, DATE_OF_BIRTH);
    }

    @Test(expected = ValidationException.class)
    public void testCreateUser_withFutureDateOfBirth() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(createdUserWithId);
        Mockito.when(userRepository.getUserByEmail(EMAIL)).thenReturn(Optional.empty());

        LocalDate futureDate = LocalDate.now().plusDays(7);

        userService.createUser(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, futureDate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUser_withNullEmail() {
        userService.createUser(null, FIRST_NAME, LAST_NAME, PASSWORD, DATE_OF_BIRTH);
    }


    @Test
    public void testGetUser() {
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));

        User user = userService.getUser(USER_ID);

        Assert.assertEquals(createdUserWithId, user);
    }

    @Test
    public void testUpdateUser() {
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User user = userService.updateUserById(USER_ID, UPDATED_EMAIL, UPDATED_FIRST_NAME, UPDATED_LAST_NAME, UPDATED_DATE_OF_BIRTH);

        Assert.assertEquals(updatedUser, user);
    }

    @Test
    public void testUpdateUser_withNullEmail() {
        User updatedUserWithSameEmail = modelMapper.map(updatedUser, User.class);
        updatedUserWithSameEmail.setEmail(createdUser.getEmail());

        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(updatedUserWithSameEmail)).thenReturn(updatedUserWithSameEmail);

        User user = userService.updateUserById(USER_ID, null, UPDATED_FIRST_NAME, UPDATED_LAST_NAME, UPDATED_DATE_OF_BIRTH);

        Assert.assertEquals(updatedUserWithSameEmail, user);
    }

    @Test
    public void testUpdateUser_withNullFirstName() {
        User updatedUserWithSameFirstName = modelMapper.map(updatedUser, User.class);
        updatedUserWithSameFirstName.setFirstName(createdUser.getFirstName());

        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(updatedUserWithSameFirstName)).thenReturn(updatedUserWithSameFirstName);

        User user = userService.updateUserById(USER_ID, UPDATED_EMAIL, null, UPDATED_LAST_NAME, UPDATED_DATE_OF_BIRTH);

        Assert.assertEquals(updatedUserWithSameFirstName, user);
    }

    @Test
    public void testUpdateUser_withNullLastName() {
        User updatedUserWithSameLastName = modelMapper.map(updatedUser, User.class);
        updatedUserWithSameLastName.setLastName(createdUser.getLastName());

        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(updatedUserWithSameLastName)).thenReturn(updatedUserWithSameLastName);

        User user = userService.updateUserById(USER_ID, UPDATED_EMAIL, UPDATED_FIRST_NAME, null, UPDATED_DATE_OF_BIRTH);

        Assert.assertEquals(updatedUserWithSameLastName, user);
    }

    @Test
    public void testUpdateUser_withNullDateOfBirth() {
        User updatedUserWithSameDateOfBirth = modelMapper.map(updatedUser, User.class);
        updatedUserWithSameDateOfBirth.setDateOfBirth(createdUser.getDateOfBirth());

        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(updatedUserWithSameDateOfBirth)).thenReturn(updatedUserWithSameDateOfBirth);

        User user = userService.updateUserById(USER_ID, UPDATED_EMAIL, UPDATED_FIRST_NAME, UPDATED_LAST_NAME, null);

        Assert.assertEquals(updatedUserWithSameDateOfBirth, user);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateUser_withFutureDateOfBirth() {
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        LocalDate futureDate = LocalDate.now().plusDays(7);

        userService.updateUserById(USER_ID, UPDATED_EMAIL, UPDATED_FIRST_NAME, UPDATED_LAST_NAME, futureDate);
    }

    @Test(expected = ValidationException.class)
    public void testUpdateUser_withAlreadyExistingEmail() {
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createdUserWithId));
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.of(updatedUser));
        Mockito.when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.updateUserById(USER_ID, UPDATED_EMAIL, UPDATED_FIRST_NAME, UPDATED_LAST_NAME, UPDATED_DATE_OF_BIRTH);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdateUser_withNonExistentUserId() {
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        Mockito.when(userRepository.getUserByEmail(UPDATED_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.updateUserById(USER_ID, UPDATED_EMAIL, UPDATED_FIRST_NAME, UPDATED_LAST_NAME, UPDATED_DATE_OF_BIRTH);
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUserById(USER_ID);
    }

    @Test
    public void testGetUsers() {
        userService.getUsers(PageRequest.of(0, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsers_withNullPageable() {
        userService.getUsers(null);
    }
}
