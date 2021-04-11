SELECT `date`, members_joined, members_left, members_banned, members_unbanned
FROM guild_data
WHERE guild_id = ? AND
    `date` >= ? AND
    `date` <= ?
ORDER BY `date`;