package ani.beautymarathon.service;

import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.exception.EmailAlreadyExistsException;
import ani.beautymarathon.exception.UserDeletedException;
import ani.beautymarathon.repository.UserRepository;
import ani.beautymarathon.view.user.UpdateUserView;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ani.beautymarathon.MeasurementsTestHelper.createTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock();
    private final UserService userService = new UserService(userRepository);


    @Test
    void getById_WhenUserIsNotFound_ThenThrowEntityNotFoundException() {
        long id = 1;

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getById(id)
        );
        String expectedMessage = "User with id " + id + " not found";

        assertEquals(expectedMessage, entityNotFoundException.getMessage());
    }

    @Test
    void getById_WhenUserIsFound_ThenReturnsUser() {
        User user = createTestUser();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        User result = userService.getById(user.getId());

        assertEquals(user, result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getStartWeight(), result.getStartWeight());
        assertEquals(user.getTargetWeight(), result.getTargetWeight());
        assertEquals(user.getDeletedState(), result.getDeletedState());
        assertEquals(user.getCreationDate(), result.getCreationDate());
    }


    @Test
    void findAll_WhenUsersAreFound_ThenReturnsAllUsers() {
        User testUser = createTestUser();
        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("testUser2");
        testUser2.setEmail("testUser2@gmail.com");
        testUser2.setCreationDate(testUser.getCreationDate());
        testUser2.setStartWeight(testUser.getStartWeight());
        testUser2.setTargetWeight(testUser.getTargetWeight());
        testUser2.setDeletedState(testUser.getDeletedState());
        List<User> expectedUsers = Arrays.asList(testUser, testUser2);

        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<User> result = userService.findAll();

        assertEquals(expectedUsers, result);
    }

    @Test
    void create_WhenEmailAlreadyExist_ThenThrowEmailAlreadyExistsException() {
        User user = createTestUser();

        when(userRepository.saveAndFlush(user))
                .thenThrow(new DataIntegrityViolationException(
                        "The email is already exists"
                ));
        EmailAlreadyExistsException ex = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.create(user)
        );
        String expectedMessage = "The email is already exists";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void create_WhenCreatingIsSuccessful_ThenReturnsCreatedUser() {
        User user = createTestUser();

        when(userRepository.saveAndFlush(user)).thenReturn(user);
        User result = userService.create(user);

        assertEquals(user, result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getStartWeight(), result.getStartWeight());
        assertEquals(user.getTargetWeight(), result.getTargetWeight());
        assertEquals(user.getDeletedState(), result.getDeletedState());
        assertEquals(user.getCreationDate(), result.getCreationDate());
    }

    @Test
    void update_WhenUserIsNotFound_ThenThrowEntityNotFoundException() {
        User user = createTestUser();
        UpdateUserView updateUserView = new UpdateUserView(
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getEmail()
        );

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> userService.update(user.getId(), updateUserView)
        );
        String expectedMessage = "User with id " + user.getId() + " not found";

        assertEquals(expectedMessage, entityNotFoundException.getMessage());
    }

    @Test
    void update_WhenUserIsDeleted_ThenThrowUserDeletedException() {
        User user = createTestUser();
        UpdateUserView updateUserView = new UpdateUserView(
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getEmail()
        );

        when(userRepository.findById(any())).thenThrow(new UserDeletedException(
                "User with id" + user.getId() + " is deleted. Cannot update"
        ));
        UserDeletedException ex = assertThrows(
                UserDeletedException.class, () -> userService.update(user.getId(), updateUserView)
        );
        String expectedMessage = "User with id" + user.getId() + " is deleted. Cannot update";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void update_WhenEmailAlreadyExist_ThenThrowEmailAlreadyExistsException() {
        User user = createTestUser();
        UpdateUserView updateUserView = new UpdateUserView(
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getEmail()
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any()))
                .thenThrow(new DataIntegrityViolationException(
                        "The email is already exists"
                ));
        EmailAlreadyExistsException ex = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.update(user.getId(), updateUserView)
        );
        String expectedMessage = "The email is already exists";

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void update_WhenUpdatingIsSuccessful_ThenReturnsUpdatedUser() {
        User user = createTestUser();
        UpdateUserView updateUserView = new UpdateUserView(
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getEmail()
        );

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        User result = userService.update(user.getId(), updateUserView);

        assertEquals(user, result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getStartWeight(), result.getStartWeight());
        assertEquals(user.getTargetWeight(), result.getTargetWeight());
        assertEquals(user.getDeletedState(), result.getDeletedState());
        assertEquals(user.getCreationDate(), result.getCreationDate());
    }

    @Test
    void updateStatus_WhenUserIsNotFound_ThenThrowEntityNotFoundException() {
        long id = 1;
        DeletedState deletedState = DeletedState.NOT_DELETED;

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateStatus(id, deletedState)
        );
        String expectedMessage = "User with id " + id + " not found";

        assertEquals(expectedMessage, entityNotFoundException.getMessage());
    }

    @Test
    void updateStatus_WhenTheUserIsAlreadyHasRequiredStatus_ThenReturnsUser() {
        User user = createTestUser();
        DeletedState deletedState = DeletedState.NOT_DELETED;
        user.setDeletedState(deletedState);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.updateStatus(user.getId(), deletedState);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateStatus_WhenStatusIsUpdatedSuccessfully_ThenReturnsUpdatedUser() {
        User user = createTestUser();
        DeletedState deletedState = DeletedState.DELETED;

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        User updatedUser = userService.updateStatus(user.getId(), deletedState);
        when(userRepository.save(any())).thenReturn(updatedUser);

        verify(userRepository, times(1)).save(any());
    }
}