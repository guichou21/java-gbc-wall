package com.gbcreation.wall.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gbcreation.wall.model.Comment;
import com.gbcreation.wall.model.Item;
import com.gbcreation.wall.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

	@Resource
	private CommentRepository commentRepository;
	
	@Override
	public long countAll() {
		return commentRepository.count();
	}

	@Override
	public long countByItemId(Item itemId) {
		return commentRepository.countByItemId(itemId);
	}

	@Override
	public long countByItemIdId(long itemIdId) {
		return commentRepository.countByItemIdId(itemIdId);
	}

	@Override
	public Page<Comment> retrieveComments(Pageable pageable) {
		return commentRepository.findAllByOrderByCreatedAtDesc(pageable);
	}
	
	@Override
	public Comment findById(Long id) {
		return commentRepository.findOne(id);
	}

	@Override
	public List<Comment> findByItemId(Item itemId) {
		return commentRepository.findByItemIdOrderByCreatedAtDesc(itemId);
	}

	@Override
	public Page<Comment> findByItemIdId(Long itemIdId, Pageable pageable) {
		return commentRepository.findByItemIdIdOrderByCreatedAtDesc(itemIdId, pageable);
	}

	@Override
	public Page<Comment> findByCommentLike(String comment, Pageable pageable) {
		return commentRepository.findByCommentContainingIgnoreCaseOrderByCreatedAtDesc(comment, pageable);
	}

	@Override
	public Page<Comment> findByAuthorLike(String author, Pageable pageable) {
		return commentRepository.findByAuthorContainingIgnoreCaseOrderByCreatedAtDesc(author, pageable);
	}

	@Override
	public Comment addComment(Comment c) {
		return commentRepository.save(c);
	}

	@Override
	public Comment updateComment(Comment c) {
		Comment cUpdated= commentRepository.save(c);
		cUpdated.setUpdatedAt(new Date());
		return cUpdated;
	}

	@Override
	public void deleteComment(Comment c) {
		commentRepository.delete(c);
	}

}
