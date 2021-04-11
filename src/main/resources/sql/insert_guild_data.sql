INSERT INTO guild_data
(
    period_start,
    period_end,
    guild_id,
    messages_created,
    messages_updated,
    messages_removed,
    reactions_added,
    reactions_removed,
    members_joined,
    members_left,
    members_banned,
    members_unbanned
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
