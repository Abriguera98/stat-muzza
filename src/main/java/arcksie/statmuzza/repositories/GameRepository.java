package arcksie.statmuzza.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import arcksie.statmuzza.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    @Query("SELECT g FROM Game g WHERE g.dateTime > CURRENT_TIMESTAMP ORDER BY g.dateTime ASC")
    Page<Game> findNextGame(Pageable pageable);
}
