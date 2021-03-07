SELECT DISTINCT ge.guild_id AS guild_id
FROM guild_events ge
WHERE ge.timestamp >= ? AND ge.timestamp < ?;
