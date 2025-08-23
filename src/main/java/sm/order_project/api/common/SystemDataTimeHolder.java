package sm.order_project.api.common;

import org.springframework.stereotype.Component;
import sm.order_project.api.domain.BaseEntity;

import java.time.LocalDateTime;

@Component
public class SystemDataTimeHolder implements DateTimeHolder {

    @Override
    public LocalDateTime getTimeNow() {
        return LocalDateTime.now();
    }

    @Override
    public LocalDateTime getCreateTime(BaseEntity entity) {
        return entity.getCreatedAt();
    }

    @Override
    public LocalDateTime getUpdateTime(BaseEntity entity) {
        return entity.getUpdatedAt();
    }

    @Override
    public LocalDateTime getDeleteTime(BaseEntity entity) {
        return entity.getDeletedAt();
    }

}