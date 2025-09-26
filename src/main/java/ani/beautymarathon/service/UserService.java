package ani.beautymarathon.service;

import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.exception.UserDeletedException;
import ani.beautymarathon.repository.UserRepository;
import ani.beautymarathon.view.UpdateUserView;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User newUser) {
        User user = userRepository.save(newUser);
        log.info("User saved: {} ", user);
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public User update(long id, UpdateUserView userView) {
        final User user = getById(id);

        if (user.getDeletedState() == DeletedState.DELETED) {
            throw new UserDeletedException("User with id " + id + " is deleted. Cannot update");
        }

        user.setName(userView.name());
        user.setStartWeight(userView.startWeight());
        user.setTargetWeight(userView.targetWeight());

        final User updatedUser = userRepository.save(user);
        log.info("User with id {} has been updated {}", id, updatedUser);
        return updatedUser;
    }

    public User updateStatus(long id, DeletedState deletedState) {
        final User user = getById(id);
        user.setDeletedState(deletedState);

        final User updated = userRepository.save(user);
        log.info("Status of user with id {} has been updated {}", id, updated);
        return updated;
    }
}
