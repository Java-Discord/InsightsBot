SELECT COUNT(me.id) AS join_count
FROM membership_events me
LEFT JOIN guild_events ge ON ge.id = me.id
WHERE ge.guild_id = ?
   AND me.event_type = 'JOIN'
   AND ge.timestamp >= ? AND ge.timestamp < ?;