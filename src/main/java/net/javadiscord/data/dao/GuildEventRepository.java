package net.javadiscord.data.dao;

import net.javadiscord.data.model.GuildEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildEventRepository extends BaseEntityRepository<GuildEvent> {
}
