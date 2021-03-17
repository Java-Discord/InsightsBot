package net.javadiscord.data.dao;

import net.javadiscord.data.model.events.MessageEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageEventRepository extends BaseEntityRepository<MessageEvent> {
}
