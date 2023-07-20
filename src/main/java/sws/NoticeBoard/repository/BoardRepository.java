package sws.NoticeBoard.repository;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sws.NoticeBoard.domain.Board;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
  private final EntityManager em;

  public void save(Board board) {
    em.persist(board);
  }

  public Board findById(Long id) {
    return em.find(Board.class, id);
  }

  public List<Board> findByAll() {
    return em.createQuery("select b from Board b", Board.class).getResultList();
  }

  public void delete(Board board) {
    em.remove(board);
  }

  public List<Board> findByBoardLoginId(String loginId) {
    return em.createQuery(
            "select b from Board b join fetch b.member m where m.loginId = :loginId", Board.class)
        .setParameter("loginId", loginId)
        .getResultList();
  }

  public List<Board> findByRealName(String realName) {
    return em.createQuery(
            "select b from Board b" + " join fetch b.member m" + " where m.realName = :realName",
            Board.class)
        .setParameter("realName", realName)
        .getResultList();
  }
}
