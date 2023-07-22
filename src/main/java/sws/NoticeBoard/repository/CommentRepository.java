package sws.NoticeBoard.repository;

import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sws.NoticeBoard.domain.Comment;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
  private final EntityManager em;

  public void save(Comment comment) {
    em.persist(comment);
  }

  public Comment findById(Long id) {
    return em.find(Comment.class, id);
  }

  public List<Comment> findByAll(Long boardId) {
    return em.createQuery(
            "select c from Comment c join fetch c.board b join fetch b.member where b.id=:boardId",
            Comment.class)
        .setParameter("boardId", boardId)
        .getResultList();
  }

  public Comment findIds(Long bId, Long cId) {
    return em.createQuery(
            "select c from Comment c join fetch c.board b join fetch b.member where b.id=:bId and"
                + " c.id=:cId",
            Comment.class)
        .setParameter("bId", bId)
        .setParameter("cId", cId)
        .getSingleResult();
  }

  public void delete(Comment comment) {
    em.remove(comment);
  }
}
