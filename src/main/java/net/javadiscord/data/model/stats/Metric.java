package net.javadiscord.data.model.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.javadiscord.data.model.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Represents any computed value that describes a set of data.
 */
@Entity
@Table(name = "metrics")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Metric extends BaseEntity {
	/**
	 * The timestamp when this metric was created.
	 */
	@CreationTimestamp
	private Instant createdAt;
}
