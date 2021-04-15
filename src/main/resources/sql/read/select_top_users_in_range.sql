SELECT umc.user_id, SUM(umc.message_count) AS total_message_count
FROM guild_data_user_message_counts umc
LEFT JOIN guild_data gd ON umc.data_id = gd.id
WHERE gd.guild_id = ? AND
      gd.date >= ? AND
      gd.date <= ?
GROUP BY umc.user_id
ORDER BY total_message_count
LIMIT 8;