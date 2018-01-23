package com.gbcreation.wall.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gbcreation.wall.model.Comment;
import com.gbcreation.wall.model.Item;
import com.gbcreation.wall.model.ItemType;
import com.gbcreation.wall.repository.CommentRepository;
import com.gbcreation.wall.repository.ItemFilterSpecifications;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest( ItemFilterSpecifications.class)
public class CommentServiceTest {
	
	@TestConfiguration
    static class CommentServiceTestContextConfiguration {
        @Bean
        public CommentService commentService() {
            return new CommentServiceImpl();
        }
    }
 
    //@Autowired
    //private ItemService itemService;
    
    @Autowired
    private CommentService commentService;
 
    @MockBean
    private CommentRepository commentRepository;
    
	private List<Item> items;
	
	private List<Comment> comments;

    
    @Before
    public void setUp() {
	    	items = generateItems();
	    	comments = generateComments();
    }

    

	@Test
    public void test_count_all() {
    		when(commentRepository.count()).thenReturn(12L);
	   
    		Long result = commentService.countAll();

	    	assertEquals(new Long(12),result);

	    	verify(commentRepository).count();
	    	verifyNoMoreInteractions(commentRepository);
    }
	
	@Test
	public void test_countBy_itemId() {
		when(commentRepository.countByItemId(Mockito.eq(items.get(4)))).thenReturn(3l);

		Long result = commentService.countByItemId(items.get(4));

		assertEquals(new Long(3),result);

		verify(commentRepository).countByItemId(Mockito.eq(items.get(4)));
		verifyNoMoreInteractions(commentRepository);
    	
	}

	@Test
	public void test_countBy_itemIdId() {
		when(commentRepository.countByItemIdId(4)).thenReturn(3l);

		Long result = commentService.countByItemIdId(4);

		assertEquals(new Long(3),result);

		verify(commentRepository).countByItemIdId(4);
		verifyNoMoreInteractions(commentRepository);
	}
    
	
	@Test
	public void test_findBy_Id() {
		
		Comment comment = new Comment("Jane Doe", "it's true, ont of your best", items.get(0));

		when(commentRepository.findOne(2L)).thenReturn(comment);
		Comment result = commentService.findById(2L);

		assertEquals(comment,result);

		verify(commentRepository).findOne(2L);
		verifyNoMoreInteractions(commentRepository);
	}

	@Test
	public void test_findBy_itemId() {
		Item item = new Item("picture1.jpg","/some/local/folder/","First Picture",ItemType.PICTURE);
		List<Comment> comments = Arrays.asList(new Comment("Jane Doe", "it's true, ont of your best", item));

		when(commentRepository.findByItemIdOrderByCreatedAtDesc(Mockito.eq(item))).thenReturn(comments);
		List<Comment> result = commentService.findByItemId(item);

		assertEquals(comments,result);

		verify(commentRepository).findByItemIdOrderByCreatedAtDesc(Mockito.eq(item));
		verifyNoMoreInteractions(commentRepository);
	}

	@Test
	public void test_findBy_ItemIdId() {
		Item item = new Item("picture1.jpg","/some/local/folder/","First Picture",ItemType.PICTURE);
		List<Comment> comments = Arrays.asList(new Comment("Jane Doe", "it's true, ont of your best", item));

		when(commentRepository.findByItemIdIdOrderByCreatedAtDesc(2L)).thenReturn(comments);
		List<Comment> result = commentService.findByItemIdId(2L);

		assertEquals(comments,result);

		verify(commentRepository).findByItemIdIdOrderByCreatedAtDesc(2L);
		verifyNoMoreInteractions(commentRepository);
	}

	@Test
	public void test_findBy_comment_like() {

		when(commentRepository.findTop100ByCommentContainingIgnoreCaseOrderByCreatedAtDesc("search content")).thenReturn(comments);
		
		List<Comment> result = commentService.findByCommentLike("search content");

		assertEquals(comments,result);

		verify(commentRepository).findTop100ByCommentContainingIgnoreCaseOrderByCreatedAtDesc("search content");
		verifyNoMoreInteractions(commentRepository);
	}
	
	@Test
	public void test_findBy_comment_like_noResults() {
		List<Comment> result = commentService.findByCommentLike("search content");

		assertEquals(Arrays.asList(),result);

		verify(commentRepository).findTop100ByCommentContainingIgnoreCaseOrderByCreatedAtDesc("search content");
		verifyNoMoreInteractions(commentRepository);
	}

	@Test
	public void test_findBy_Author_like() {
		when(commentRepository.findTop100ByAuthorContainingIgnoreCaseOrderByCreatedAtDesc("search content")).thenReturn(comments);
		
		List<Comment> result = commentService.findByAuthorLike("search content");

		assertEquals(comments,result);

		verify(commentRepository).findTop100ByAuthorContainingIgnoreCaseOrderByCreatedAtDesc("search content");
		verifyNoMoreInteractions(commentRepository);
	}

	@Test
	public void test_findBy_Author_like_noResults() {
		List<Comment> result = commentService.findByAuthorLike("search content");

		assertEquals(Arrays.asList(),result);

		verify(commentRepository).findTop100ByAuthorContainingIgnoreCaseOrderByCreatedAtDesc("search content");
		verifyNoMoreInteractions(commentRepository);
	}
	
	@Test
	public void test_add_comment() {
		Comment c = new Comment("John Doe", "nice picture1", items.get(0));
	    commentService.addComment(c);

	    	verify(commentRepository).save(c);
	    	verifyNoMoreInteractions(commentRepository);
	}

	@Test
	public void test_update_comment() {
		Comment c = new Comment("John Doe", "nice picture1", items.get(0));
		Comment comUpdated = new Comment("John Doe", "nice picture1, one of your i guess", items.get(0));
		Date modifiedDate = new Date();
		comUpdated.setUpdatedAt(modifiedDate);
		
		when(commentRepository.save(c)).thenReturn(comUpdated);
		
		Comment result= commentService.updateComment(c);

		assertEquals(result,comUpdated);
		assertEquals(c.getUpdatedAt(),null);
		assertEquals(result.getUpdatedAt(),modifiedDate);
    		verify(commentRepository).save(c);
    		verifyNoMoreInteractions(commentRepository);
	}

	@Test
	public void test_delete_comment() {
		Comment c = new Comment("John Doe", "nice picture1", items.get(0));
	    commentService.deleteComment(c);

	    	verify(commentRepository).delete(c);
	    	verifyNoMoreInteractions(commentRepository);
	}
    
    private List<Item> generateItems() {
	    	items = new ArrayList<Item>();
	    	items.add(new Item("picture1.jpg","/some/local/folder/","First Picture",ItemType.PICTURE));
	    	items.add(new Item("picture2.jpg","/some/local/folder/","Second Picture",ItemType.PICTURE));
	    	items.add(new Item("codevideo1","http://youtube.com/some/path/", "Demo video 1",ItemType.VIDEO));
	    	items.add(new Item("picture3.jpg","/some/local/folder/",  "Third Picture",ItemType.PICTURE));
	    	items.add(new Item("codevideo2","http://youtube.com/some/path/", "Demo video 2",ItemType.VIDEO_YOUTUBE));
	    	items.add(new Item("codevideo3","http://youtube.com/some/path/", "Demo video 3",ItemType.VIDEO_VIMEO));
	    	items.add(new Item("picture4.jpg","/some/local/folder/", "Fourth Picture",ItemType.PICTURE));
	    	items.add(new Item("picture5.jpg","/some/local/folder/","Fifth Picture",ItemType.PICTURE));
	    	items.add(new Item("picture6.jpg","/some/local/folder/", "Sixth Picture",ItemType.PICTURE));
	    	items.add(new Item("codevideo4","http://youtube.com/some/path/", "Demo video 4",ItemType.VIDEO_YOUTUBE));
	    	items.add(new Item("picture7.jpg","/some/local/folder/", "Seventh Picture",ItemType.PICTURE));
	    	items.add(new Item("codevideo5","http://youtube.com/some/path/", "Demo video 5",ItemType.VIDEO_YOUTUBE));
	    	return items;
    }
    
    private List<Comment> generateComments() {
		comments = new ArrayList<Comment>();
		comments.add(new Comment("John Doe", "nice picture1", items.get(0)));
		comments.add(new Comment("Jane Doe", "it's true, ont of your best", items.get(0)));
		comments.add(new Comment("Theodore Handle", "wow", items.get(0)));
		
		comments.add(new Comment("Guy Mann", "nice video2", items.get(4)));
		comments.add(new Comment("Eleanor Fant Mann", "a bit long maybe", items.get(4)));
		comments.add(new Comment("Guy Mann", "not agreed", items.get(4)));
		
		comments.add(new Comment("John Doe", "nice picture4", items.get(6)));
		comments.add(new Comment("Jane Doe", "where is it", items.get(6)));
		comments.add(new Comment("Theodore Handle", "australia maybee", items.get(6)));
		comments.add(new Comment("Guy Mann", "NZ i guess", items.get(6)));
		comments.add(new Comment("Eleanor Fant Mann", "no idea, so beautiful. wow", items.get(6)));
		comments.add(new Comment("Guy Mann", "ah ok, it's south africa", items.get(6)));
		
		return comments;
	}
}
