package net.javadiscord.data.aggregation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javadiscord.data.dao.MetricRepository;
import net.javadiscord.data.model.stats.time_interval_metrics.MessagesMetric;
import net.javadiscord.util.SqlHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

import static net.javadiscord.util.SqlHelper.wrapStr;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeIntervalAggregationService {
	private final MetricRepository metricRepository;
	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public MessagesMetric generateMessagesMetric(long guildId, Instant start, Instant end) {
		return this.jdbcTemplate.query(connection -> SqlHelper.loadMulti(
				connection,
				"sql/messages_metric.sql",
				wrapStr(Timestamp.from(start).toString()),
				wrapStr(Timestamp.from(end).toString()),
				String.valueOf(guildId)
		), r -> {
			if (!r.next()) return null;
			MessagesMetric mm = new MessagesMetric(guildId, start, end);
			mm.setMessagesCreated(r.getLong("messages_created"));
			mm.setMessagesDeleted(r.getLong("messages_deleted"));
			mm.setMessagesUpdated(r.getLong("messages_updated"));
			mm.setMessagesRetained(r.getLong("messages_retained"));
			mm.setReactionsAdded(r.getLong("reactions_added"));
			mm.setReactionsRemoved(r.getLong("reactions_removed"));
			mm.setActiveUsers(r.getLong("active_users"));
			return this.metricRepository.save(mm);
		});
	}
}
