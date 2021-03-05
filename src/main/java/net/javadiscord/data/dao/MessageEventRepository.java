package net.javadiscord.data.dao;

import net.javadiscord.data.model.events.MessageEvent;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface MessageEventRepository extends BaseEntityRepository<MessageEvent> {
	long countAllByGuildIdEqualsAndTimestampAfterAndEventTypeEquals(long guildId, Instant ts, MessageEvent.EventType eventType);
}
