SELECT `date`, COUNT(user_id)
FROM guild_data
LEFT JOIN guild_data_user_message_counts gdumc ON guild_data.id = gdumc.data_id
WHERE guild_id = ? AND
    `date` >= ? AND
    `date` <= ?
GROUP BY guild_data.date
ORDER BY `date`;