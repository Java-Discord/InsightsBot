DELETE FROM guild_data
WHERE guild_id = ? AND
      period_start >= ? AND
      period_end <= ?;
