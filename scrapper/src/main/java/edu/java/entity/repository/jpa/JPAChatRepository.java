package edu.java.entity.repository.jpa;

import edu.java.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JPAChatRepository extends JpaRepository<Chat, Long> {

}
