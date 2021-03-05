package net.javadiscord.data.dao;

import net.javadiscord.data.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Generic repository for any {@link BaseEntity}. Extend this repository for any
 * DAO of a model which extends from that superclass.
 * @param <T> The root entity type.
 */
public interface BaseEntityRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
}
