package net.antidot.api.search;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class QueryCoderTest {

	private CoderManager coderMgr;
	private Query query;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		query = mock(Query.class);
		when(query.hasFeed()).thenReturn(false);
		when(query.hasSearchString()).thenReturn(false);
		when(query.hasPage()).thenReturn(false);
		when(query.hasReplies()).thenReturn(false);
		when(query.hasLanguage()).thenReturn(false);
		when(query.hasSort()).thenReturn(false);
		when(query.hasFilter()).thenReturn(false);
		
		FeedCoderInterface feedCoder = mock(FeedCoderInterface.class);
		when(feedCoder.encode(anySetOf(String.class))).thenReturn("encodedFeed");
		
		FilterCoderInterface filterCoder = mock(FilterCoderInterface.class);
		when(filterCoder.encode(anyMap())).thenReturn("encodedFilter");
		
		coderMgr = new CoderManager(feedCoder, filterCoder);
	}
		
	@Test
	public void testLinkWithoutParameter() {
		try {
			QueryCoder coder = new QueryCoder("http://foo", coderMgr);
			assertEquals("http://foo", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}

	@Test
	public void testLinkWithFeed() {
		when(query.hasFeed()).thenReturn(true);
		when(query.getFeeds()).thenReturn(new TreeSet<String>());
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);

		try {
			assertEquals("http://foo?feed=encodedFeed", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}

	@Test
	public void testLinkWithPage() {
		when(query.hasPage()).thenReturn(true);
		when(query.getPage()).thenReturn(42L);
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);

		try {
			assertEquals("http://foo?page=42", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}

	@Test
	public void testLinkWithQuery() {
		when(query.hasSearchString()).thenReturn(true);
		when(query.getSearchString()).thenReturn("query");
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);

		try {
			assertEquals("http://foo?query=query", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}

	@Test
	public void testLinkWithReplies() {
		when(query.hasReplies()).thenReturn(true);
		when(query.getReplies()).thenReturn(666);
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);

		try {
			assertEquals("http://foo?replies=666", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}

	@Test
	public void testLinkWithLanguage() {
		when(query.hasLanguage()).thenReturn(true);
		when(query.getLanguage()).thenReturn("en-US");
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);

		try {
			assertEquals("http://foo?lang=en-US", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}

	@Test
	public void testLinkWithSort() {
		when(query.hasSort()).thenReturn(true);
		when(query.getSort()).thenReturn("sorted");
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);

		try {
			assertEquals("http://foo?sort=sorted", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}

	@Test
	public void testLinkWithFilter() {
		when(query.hasFilter()).thenReturn(true);
		when(query.getFilters()).thenReturn(new TreeMap<String, Set<String>>());
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);

		try {
			assertEquals("http://foo?filter=encodedFilter", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}
	}
	
	@Test
	public void testLinkWithPageAndReplies() {
		when(query.hasPage()).thenReturn(true);
		when(query.getPage()).thenReturn(42L);
		QueryCoder coder = null;
		coder = new QueryCoder("http://foo", coderMgr);
		try {
			assertEquals("http://foo?page=42", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}

		when(query.hasReplies()).thenReturn(true);
		when(query.getReplies()).thenReturn(666);

		try {
			assertEquals("http://foo?page=42&replies=666", coder.generateLink(query));
		} catch (URISyntaxException e) {
			fail("Should not have failed with: " + e.getMessage());
		}

	}

	@Test
	public void testCreateQueryFromQueryStringWithPage() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("page", new String[]{"42"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasPage());
		assertEquals(42L, query.getPage());
	}
	
	@Test
	public void testCreateQueryFromQueryStringWithReplies() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("replies", new String[]{"42"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasReplies());
		assertEquals(42, query.getReplies());
	}
	
	@Test
	public void testCreateQueryFromQueryStringWithQuery() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("query", new String[]{"42"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasSearchString());
		assertEquals("42", query.getSearchString());
	}
	
	@Test
	public void testCreateQueryFromQueryStringWithFeed() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("feed", new String[]{"foo"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasFeed());
		assertTrue(query.hasFeed("foo"));
		assertEquals(1, query.getFeeds().size());
	}
	
	@Test
	public void testCreateQueryFromQueryStringWithLanguage() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("lang", new String[]{"en"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasLanguage());
		assertEquals("en", query.getLanguage());
	}
	
	@Test
	public void testCreateQueryFromQueryStringWithSort() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("sort", new String[]{"afs:foo"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasSort());
		assertEquals("afs:foo", query.getSort());
	}
	
	@Test
	public void testCreateQueryFromQueryStringWithFilter() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("filter", new String[]{"foo_bar"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasFilter());
		String[] values = query.getFilterValues("foo");
		assertEquals(1, values.length);
		assertEquals("bar", values[0]);
	}
	
	@Test
	public void testCreateQueryFromQueryStringWithPageAndOtherParameter() {
		CoderManager coderMgr = new CoderManager();
		Map<String, String[]> parameters = new TreeMap<String, String[]>();
		parameters.put("replies", new String[]{"42"});
		parameters.put("page", new String[]{"666"});
		Query query = new QueryCoder("foo", coderMgr).decode(parameters);
		
		assertTrue(query.hasPage());
		assertEquals(666, query.getPage());
	}
}
