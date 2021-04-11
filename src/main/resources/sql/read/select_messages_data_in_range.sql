SELECT `date`, messages_created, messages_updated, messages_removed
FROM guild_data
WHERE guild_id = ? AND
      `date` >= ? AND
      `date` <= ?
ORDER BY `date`;