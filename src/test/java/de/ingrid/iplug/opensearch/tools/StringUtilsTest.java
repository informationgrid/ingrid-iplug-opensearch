package de.ingrid.iplug.opensearch.tools;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

	public final void testParameterExists() {
		assertTrue(StringUtils.parameterExists("abcdef{param1}xyz", "param1"));
		assertFalse(StringUtils.parameterExists("abcdef{param1}xyz", "param2"));
		assertTrue(StringUtils.parameterExists("abcdef{optional?}xyz", "optional"));
		assertFalse(StringUtils.parameterExists("abcdef{optional?}xyz", "optional2"));
	}

	public final void testReplaceParameter() {
		assertTrue(StringUtils.replaceParameter("a;f={param1};xyz", "param1", "insert").equals("a;f=insert;xyz"));
		assertFalse(StringUtils.replaceParameter("a;f={param1};xyz", "param2", "insert").equals("a;f=insert;xyz"));
		assertTrue(StringUtils.replaceParameter("a;f={param1?};xyz", "param1", "insert").equals("a;f=insert;xyz"));
		assertFalse(StringUtils.replaceParameter("a;f={param1?};xyz", "param2", "insert").equals("a;f=insert;xyz"));
	}
	
	public final void testRemoveUnusedParameter() {
		assertTrue(StringUtils.removeUnusedParameter("query?q=Wasser&bbox={geo:box?}&p={startPage?}").equals("query?q=Wasser"));
		assertTrue(StringUtils.removeUnusedParameter("query?q=Wasser&bbox={geo:box?}&p=2").equals("query?q=Wasser&p=2"));
		assertTrue(StringUtils.removeUnusedParameter("query?bbox={geo:box?}&q=Wasser&p=2").equals("query?q=Wasser&p=2"));
		assertTrue(StringUtils.removeUnusedParameter("query?p=1&q=Wasser").equals("query?p=1&q=Wasser"));
		assertTrue(StringUtils.removeUnusedParameter("query?p=1&bbox={geo:box?}&q=Wasser").equals("query?p=1&q=Wasser"));
		assertTrue(StringUtils.removeUnusedParameter("query?p=1&bbox={geo:box?}&h=10&q=Wasser").equals("query?p=1&h=10&q=Wasser"));
		assertTrue(StringUtils.removeUnusedParameter("http://localhost:8181/query?q=wasser+lufts+boden&bbox={geo:box?}&p=0&h=10&xml={ingridsearch:xml?}&format=rss").equals("http://localhost:8181/query?q=wasser+lufts+boden&p=0&h=10&format=rss"));
	}
}
