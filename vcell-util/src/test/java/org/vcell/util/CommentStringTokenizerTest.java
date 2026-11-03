package org.vcell.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class CommentStringTokenizerTest {

	private static List<String> tokens(String text) {
		CommentStringTokenizer tokenizer = new CommentStringTokenizer(text);
		List<String> tokens = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) tokens.add(tokenizer.nextToken());
		return tokens;
	}

	@Test
	public void lineCommentRunsToEndOfLineOnly() {
		// a "//" comment must not swallow the following lines, as .mesh files rely on
		assertEquals(List.of("a", "b", "c"), tokens("a b // note\nc"));
	}

	@Test
	public void lineCommentIsAttachedToFirstTokenOfLine() {
		CommentStringTokenizer tokenizer = new CommentStringTokenizer("a b //note");
		assertEquals("note", tokenizer.next().getAfterComment());
	}

	@Test
	public void blockCommentSpanningLinesIsSkippedAndAttachedToNextToken() {
		CommentStringTokenizer tokenizer = new CommentStringTokenizer("/* first\nsecond */ x y");
		Token x = tokenizer.next();
		assertEquals("x", x.getValue());
		assertNotNull(x.getBeforeComment());
		assertEquals("y", tokenizer.nextToken());
		assertFalse(tokenizer.hasMoreTokens());
	}

	@Test
	public void blockCommentOnOneLine() {
		assertEquals(List.of("x", "y"), tokens("/* c */ x y"));
	}

	@Test
	public void quotedTextKeepsCommentMarkers() {
		assertEquals(List.of("a//b"), tokens("\"a//b\""));
	}

	@Test
	public void unterminatedBlockCommentFailsClearly() {
		// the first line is tokenized eagerly, so the failure can surface as early as construction
		NoSuchElementException e = assertThrows(NoSuchElementException.class,
				() -> new CommentStringTokenizer("x /* never closed\nmore").hasMoreTokens());
		assertTrue(e.getMessage().contains("Unterminated comment"));
	}
}
