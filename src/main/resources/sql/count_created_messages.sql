SELECT COUNT(me.id) AS message_count
FROM message_events me
LEFT JOIN guild_events ge ON me.id = ge.id
WHERE me.event_type = 'CREATE'
    AND ge.timestamp >= ? AND ge.timestamp < ?;