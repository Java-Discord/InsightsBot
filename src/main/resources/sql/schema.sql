DROP TABLE IF EXISTS guild_data_user_message_counts;
DROP TABLE IF EXISTS guild_data_reaction_counts;
DROP TABLE IF EXISTS guild_data;

CREATE TABLE guild_data (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `date` DATE NOT NULL DEFAULT CURRENT_DATE() COMMENT 'The date that the data applies to.',
    guild_id BIGINT UNSIGNED NOT NULL,

    # Data columns
    messages_created MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    messages_updated MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    messages_removed MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,

    reactions_added MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    reactions_removed MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,

    members_joined MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    members_left MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    members_banned MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    members_unbanned MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    member_count MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,

    CONSTRAINT guild_period_unique UNIQUE (`date`, guild_id)
) CHAR SET utf8, COLLATE utf8_bin;

CREATE TABLE guild_data_user_message_counts (
    data_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (data_id, user_id),
    message_count MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    CONSTRAINT user_message_counts_data_fk FOREIGN KEY (data_id) REFERENCES guild_data(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) CHAR SET utf8, COLLATE utf8_bin;

CREATE TABLE guild_data_reaction_counts (
    data_id BIGINT UNSIGNED NOT NULL,
    reaction VARCHAR(255) NOT NULL,
    PRIMARY KEY (data_id, reaction),
    reaction_count MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    CONSTRAINT reaction_counts_data_fk FOREIGN KEY (data_id) REFERENCES guild_data(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) CHAR SET utf8mb4, COLLATE utf8mb4_bin;
