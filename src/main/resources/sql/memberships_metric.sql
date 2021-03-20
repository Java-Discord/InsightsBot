SET @start = ?;
SET @end = ?;
SET @guild_id = ?;

SET @join_count = (
	SELECT COUNT(membership_events.id)
	FROM membership_events
	LEFT JOIN guild_events ge ON membership_events.id = ge.id
	WHERE event_type = 'JOIN'
	AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @leave_count = (
	SELECT COUNT(membership_events.id)
	FROM membership_events
	LEFT JOIN guild_events ge ON membership_events.id = ge.id
	WHERE event_type = 'LEAVE'
	AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @retained_count = (
	SELECT COUNT(me.id)
	FROM membership_events me
	LEFT JOIN guild_events ge ON me.id = ge.id
	WHERE event_type = 'JOIN'
	AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
	AND ge.user_id NOT IN (
		SELECT g2.user_id
		FROM membership_events me2
		INNER JOIN guild_events g2 ON me2.id = g2.id
		WHERE me2.event_type = 'LEAVE'
		AND g2.timestamp >= @start AND g2.timestamp <= @end AND g2.user_id = ge.user_id AND g2.guild_id = ge.guild_id
	)
);
SET @ban_count = (
	SELECT COUNT(membership_events.id)
	FROM membership_events
	LEFT JOIN guild_events ge ON membership_events.id = ge.id
	WHERE event_type = 'BAN'
	AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);
SET @unban_count = (
	SELECT COUNT(membership_events.id)
	FROM membership_events
	LEFT JOIN guild_events ge ON membership_events.id = ge.id
	WHERE event_type = 'UNBAN'
	AND ge.timestamp >= @start AND ge.timestamp <= @end AND ge.guild_id = @guild_id
);

SELECT
	@join_count AS members_joined,
	@leave_count AS members_left,
	@retained_count AS members_retained,
	@ban_count AS members_banned,
	@unban_count AS members_unbanned
;
