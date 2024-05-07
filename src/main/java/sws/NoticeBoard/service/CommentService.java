package sws.NoticeBoard.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sws.NoticeBoard.controller.form.CommentRequest;
import sws.NoticeBoard.controller.form.CommentResponse;
import sws.NoticeBoard.domain.Board;
import sws.NoticeBoard.domain.Comment;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.repository.BoardRepository;
import sws.NoticeBoard.repository.CommentRepository;
import sws.NoticeBoard.repository.MemberRepository;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CommentService {
	private final MemberRepository memberRepository;
	private final BoardRepository boardRepository;
	private final CommentRepository commentRepository;

	public void save(Long boardId, String loginId, String content) {
		Board findBoard = boardRepository.findById(boardId);
		Member findMember = memberRepository.findByLoginId(loginId);
		Comment comment = new Comment();
		comment.setBoard(findBoard);
		comment.setMember(findMember);
		comment.setContent(content);
		commentRepository.save(comment);
	}

	public CommentResponse findById(Long id) {
		Comment findComment = commentRepository.findById(id);
		CommentResponse commentResponse = entityToDto(findComment);
		return commentResponse;
	}

	public List<CommentResponse> findByAll(Long boardId) {
		List<Comment> findComment = commentRepository.findByAll(boardId);
		Function<Comment, CommentResponse> fn = (entity -> entityToDto(entity));
		return findComment.stream().map(fn).collect(Collectors.toList());
	}

	public CommentResponse entityToDto(Comment entity) {
		CommentResponse dto =
			CommentResponse.builder()
				.id(entity.getId())
				.boardId(entity.getBoard().getId())
				.content(entity.getContent())
				.writer(entity.getMember().getRealName())
				.create_at(entity.getCreate_at())
				.modify_at(entity.getModify_at())
				.deleteYn(entity.getDeleteYn())
				.build();
		return dto;
	}

	public CommentResponse findCommentById(Long cId, String loginId) {
		Comment findComment = commentRepository.findById(cId);
		Member findMember = memberRepository.findByLoginId(loginId);
		if (!findComment.getMember().equals(findMember))
			return null;
		return entityToDto(findComment);
	}

	public void updateComment(CommentRequest commentRequest) {
		Comment findComment = commentRepository.findById(commentRequest.getId());
		findComment.setContent(commentRequest.getContent());
	}

	public Long deleteComment(Long commentId, String loginId) {
		Comment findComment = commentRepository.findById(commentId);
		Member findMember = memberRepository.findByLoginId(loginId);
		if (!findComment.getMember().equals(findMember))
			return null;
		commentRepository.delete(findComment);
		return commentId;
	}
}
