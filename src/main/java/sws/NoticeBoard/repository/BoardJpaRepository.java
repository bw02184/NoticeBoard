package sws.NoticeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sws.NoticeBoard.domain.Board;

public interface BoardJpaRepository extends JpaRepository<Board, Long> {}
