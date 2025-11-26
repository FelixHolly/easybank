package at.holly.easybankbackend.repository;

import at.holly.easybankbackend.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

  Page<Card> findByUserId(long userId, Pageable pageable);

}
