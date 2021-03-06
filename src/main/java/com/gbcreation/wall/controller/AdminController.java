package com.gbcreation.wall.controller;

import javax.annotation.Resource;
import javax.print.attribute.standard.Media;
import javax.xml.ws.Response;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gbcreation.wall.controller.exception.CommentNotFoundException;
import com.gbcreation.wall.controller.exception.ItemNotFoundException;
import com.gbcreation.wall.model.Comment;
import com.gbcreation.wall.model.Item;
import com.gbcreation.wall.service.CommentService;
import com.gbcreation.wall.service.ItemService;

import ch.qos.logback.core.status.Status;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@CrossOrigin(origins = {"${settings.cors_origin}"})
public class AdminController {

	@Resource
	private ItemService itemService;

	@Resource
	private CommentService commentService;
	
	@PostMapping(value="/item/add", consumes= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE } , produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Item> addItem(@RequestBody Item item) {
		log.info("AdminController: addItem {}", item.getFile());
		try {
			return new ResponseEntity<Item>(itemService.addItem(item),HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/item/update",method=RequestMethod.PUT, consumes= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE } , produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Item> updateItem(@RequestBody Item item) {
		try {
			if(item.getId() !=null ) {
				log.info("AdminController: updateItem id {}", item.getId());
			
				validateItem(item.getId());		
				return new ResponseEntity<Item>(itemService.updateItem(item), HttpStatus.OK);
			}
			else {
				throw new ItemNotFoundException("id not described in item");
			}
		}
		catch(ItemNotFoundException e) {
			return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/item/delete/{id}",method=RequestMethod.DELETE)
	public ResponseEntity<String> deleteItem(@PathVariable String id) {
		log.info("AdminController: delete item {}",id);
		try{
			Item itemFound = validateItem(new Long(id));	

			itemService.deleteItem(itemFound);
			return new ResponseEntity(HttpStatus.OK);
		}
		catch(ItemNotFoundException e) {
			return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(value="/item/{itemId}/comment/add",method=RequestMethod.POST, consumes= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE } , produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Comment> addComment(@PathVariable String itemId, @RequestBody Comment comment) {
		log.info("AdminController: addComment on item {} from author {}", itemId, comment.getAuthor());
		try {
			Item itemFound = validateItem(new Long(itemId));
			comment.setItemId(itemFound);
			
			return new ResponseEntity(commentService.addComment(comment), HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/comment/update",method=RequestMethod.PUT, consumes= { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE } , produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Comment> updateComment(@RequestBody Comment comment) {
		try{
			if(comment.getId() !=null ) {
				log.info("AdminController: updateComment id {}", comment.getId());
				
				validateComment(comment.getId());
				Item itemFound = commentService.findById(comment.getId()).getItemId();
				comment.setItemId(itemFound);
				
				return new ResponseEntity(commentService.updateComment(comment), HttpStatus.OK);
			}
			else {
				throw new CommentNotFoundException("id not described in comment");
			}
		}
		catch(CommentNotFoundException e) {
			return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/comment/delete/{id}",method=RequestMethod.DELETE)
	public ResponseEntity<String> deleteComment(@PathVariable String id) {
		log.info("AdminController: delete comment {}",id);
		try {
			Comment commentFound = validateComment(new Long(id));
			commentService.deleteComment(commentFound);
			return new ResponseEntity(HttpStatus.OK);
		}
		catch(CommentNotFoundException e) {
			return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
		}
	}
		
	
	
	/**
	 * Verify the {@literal itemId} exists.
	 *
	 * @param itemId
	 */
	private Item validateItem(long itemId) {
		Item itemFound = this.itemService.findById(itemId);
		if(itemFound == null) {
			throw new ItemNotFoundException(itemId);
		}
		return itemFound;
	}
	
	/**
	 * Verify the {@literal commentId} exists.
	 *
	 * @param commentId
	 */
	private Comment validateComment(long commentId) {
		Comment commentFound = this.commentService.findById(new Long(commentId));
		if(commentFound == null) {
			throw new CommentNotFoundException(commentId);
		}
		return commentFound;
	}

}
