package sm.order_project.api.common;

import sm.order_project.api.domain.BaseEntity;

import java.time.LocalDateTime;

public interface DateTimeHolder {

    LocalDateTime getTimeNow();

    LocalDateTime getCreateTime(BaseEntity entity);

    LocalDateTime getUpdateTime(BaseEntity entity);

    LocalDateTime getDeleteTime(BaseEntity entity);
}
