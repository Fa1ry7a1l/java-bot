package edu.java.entity.repository.jpa;

import edu.java.entity.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JPALinkRepository extends JpaRepository<Link, Long> {

    @Query("SELECT l from Link l where l.updatedAt < :offsetTime")
    List<Link> findMoreThenOffsetUpdated(@Param("offsetTime") OffsetDateTime offsetDateTime);

    @Query("SELECT l from Link l where l.url = :url")
    Optional<Link> findByUrl(@Param("url") URI url);
}
