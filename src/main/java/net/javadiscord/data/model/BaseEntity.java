package net.javadiscord.data.model;

import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * A general-purpose abstract superclass for all entities, with a Long id.
 */
@MappedSuperclass
@Getter
public abstract class BaseEntity {
	/**
	 * The primary key identifier for this entity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Custom implementation of equals which works as follows:
	 * <ol>
	 *     <li>If the object is not of the same class, return false.</li>
	 *     <li>If either this entity or the given entity doesn't have an
	 *     id, return false.</li>
	 *     <li>If both entities' ids are non-null and equal, return true.
	 *     Otherwise return false.</li>
	 * </ol>
	 * @param o The object to check equality against.
	 * @return True if the object is equal to this one, or false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BaseEntity that = (BaseEntity) o;
		// Assume that if one of the entities doesn't have an id, they're not equal.
		if (this.getId() == null || that.getId() == null) return false;
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
