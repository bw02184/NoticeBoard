package sws.NoticeBoard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.controller.form.BoardForm;
import sws.NoticeBoard.controller.form.PageRequestDTO;
import sws.NoticeBoard.controller.form.PageResultDTO;
import sws.NoticeBoard.domain.Board;
import sws.NoticeBoard.domain.Grade;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.BoardJpaRepository;
import sws.NoticeBoard.repository.BoardRepository;
import sws.NoticeBoard.repository.CommentRepository;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
	private final BoardRepository boardRepository;
	private final BoardJpaRepository boardJpaRepository;
	private final MemberRepository memberRepository;
	private final CommentRepository commentRepository;

	public void save(BoardForm form, String loginId) {
		Member findmember = memberRepository.findByLoginId(loginId);
		Board board = new Board();
		board.setTitle(form.getTitle());
		board.setContent(form.getContent());
		board.setNoticeYn(form.isNoticeYn());
		board.setMember(findmember);
		board.setViewCount(0);
		boardRepository.save(board);
	}

	public List<Board> findByAll(int first, int last) {
		return boardRepository.findByAllList(first, last);
	}

	public Board findById(Long id) {
		return boardRepository.findById(id);
	}

	public void viewCount(Long id) {
		Board findBoard = boardRepository.findById(id);
		findBoard.setViewCount(findBoard.getViewCount() + 1);
	}

	public void update(BoardForm form, String loginId) {
		Board findBoard = boardRepository.findById(form.getId());
		Member findMember = memberRepository.findByLoginId(loginId);
		if (findMember == null || !findMember.equals(findBoard.getMember()))
			return;
		findBoard.setTitle(form.getTitle());
		findBoard.setContent(form.getContent());
	}

	public void delete(Long id, String loginId) {
		Board findBoard = boardRepository.findById(id);
		Member findMember = memberRepository.findByLoginId(loginId);
		if (findMember == null || (findMember.getGrade() == Grade.NORMAL && !findMember.equals(findBoard.getMember())))
			return;
		int deleteCount = commentRepository.deleteByBoardId(id);
		log.info("BoardId={}일때 Comments 삭제횟수={}", id, deleteCount);
		boardRepository.delete(findBoard);
	}

	public PageResultDTO<BoardForm, Board> getList(PageRequestDTO requestDTO) {
		Pageable pageable = requestDTO.getPageable(Sort.by("noticeYn").descending().and(Sort.by("id").descending()));
		log.info("pageable={}", pageable);
		Page<Board> result = boardJpaRepository.findAll(pageable);
		return new PageResultDTO<>(result);
	}
}
