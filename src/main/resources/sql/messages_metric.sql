SET @start = ?;
SET @end = ?;
SET @guild_id = ?;

SET @created_count = (
    SELECT COUNT(message_id)
    FROM message_events
    LEFT JOIN guild_events ge ON message_events.id = ge.id
    WHERE event_type = 'CREATE'
      AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @deleted_count = (
    SELECT COUNT(message_id)
    FROM message_events
    LEFT JOIN guild_events ge ON ge.id = message_events.id
    WHERE event_type = 'DELETE' AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @updated_count = (
    SELECT COUNT(message_id)
    FROM message_events
    LEFT JOIN guild_events ge ON message_events.id = ge.id
    WHERE event_type = 'UPDATE' AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @retained_count = (
    SELECT COUNT(message_id)
    FROM message_events
    LEFT JOIN guild_events ge ON ge.id = message_events.id
    WHERE event_type = 'CREATE'
      AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
      AND message_id NOT IN (
          SELECT me2.message_id
          FROM message_events me2
          LEFT JOIN guild_events g2 ON me2.id = g2.id
          WHERE me2.event_type = 'DELETE'
          AND g2.guild_id = ge.guild_id AND g2.timestamp >= @start AND g2.timestamp <= @end
      )
);
SET @reactions_added_count = (
    SELECT COUNT(reaction_events.id)
    FROM reaction_events
    LEFT JOIN guild_events ge ON reaction_events.id = ge.id
    WHERE event_type = 'ADD' AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @reactions_removed_count = (
    SELECT COUNT(reaction_events.id)
    FROM reaction_events
    LEFT JOIN guild_events ge ON reaction_events.id = ge.id
    WHERE event_type = 'REMOVE' AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @active_users_count = (
    SELECT COUNT(DISTINCT (ge.user_id))
    FROM guild_events ge
    WHERE ge.user_id IS NOT NULL
      AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
      AND (ge.id IN (SELECT id FROM message_events) OR ge.id IN (SELECT id FROM reaction_events))
);

SELECT
    @created_count AS messages_created,
    @deleted_count AS messages_deleted,
    @updated_count AS messages_updated,
    @retained_count AS messages_retained,
    @reactions_added_count AS reactions_added,
    @reactions_removed_count AS reactions_removed,
    @active_users_count AS active_users
;