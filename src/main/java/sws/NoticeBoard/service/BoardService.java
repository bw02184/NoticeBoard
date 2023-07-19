package sws.NoticeBoard.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.controller.form.BoardForm;
import sws.NoticeBoard.domain.Board;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.BoardRepository;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
  private final BoardRepository boardRepository;
  private final MemberRepository memberRepository;

  public void save(BoardForm form, String loginId) {
    Member findmember = memberRepository.findByLoginId(loginId);
    Board board = new Board();
    board.setTitle(form.getTitle());
    board.setContent(form.getContent());
    board.setNoticeYn(false);
    board.setMember(findmember);
    board.setViewCount(0);
    boardRepository.save(board);
  }

  public List<Board> findByAll() {
    return boardRepository.findByAll();
  }

  public Board findById(Long id) {
    return boardRepository.findById(id);
  }

  public Board viewCount(Long id) {
    Board findBoard = boardRepository.findById(id);
    findBoard.setViewCount(findBoard.getViewCount() + 1);
    return findBoard;
  }
}
