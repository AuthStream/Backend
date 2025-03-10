package authstream.domain.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserGroupId implements Serializable {
    private UUID userId;
    private UUID groupId;

    public UserGroupId() {
    }

    // Constructor đầy đủ
    public UserGroupId(UUID userId, UUID groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserGroupId that = (UserGroupId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }
}