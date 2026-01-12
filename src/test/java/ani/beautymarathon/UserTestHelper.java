package ani.beautymarathon;

import ani.beautymarathon.entity.DeletedState;
import ani.beautymarathon.entity.User;
import ani.beautymarathon.view.user.GetUserView;

import java.math.BigDecimal;

public class UserTestHelper {
    public static User createTestUser() {
        long id = 1;
        User user = new User();
        user.setId(id);
        user.setEmail("test@test.com");
        user.setStartWeight(new BigDecimal("66.3"));
        user.setTargetWeight(new BigDecimal("50.0"));
        user.setDeletedState(DeletedState.NOT_DELETED);
        user.setName("Test name");
        return user;
    }

    public static GetUserView createTestUserView(User user) {
        return new GetUserView(
                user.getId(),
                user.getName(),
                user.getStartWeight(),
                user.getTargetWeight(),
                user.getCreationDate(),
                user.getDeletedState(),
                user.getEmail()
        );
    }
}
