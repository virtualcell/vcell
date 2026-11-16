package org.vcell.documentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Full-text search over the help. Small enough (a few hundred pages) to scan in memory: a page
 * matches when every query word occurs in its title or text; title matches rank first.
 */
public final class HelpSearch {
	/** @param score higher is better */
	public record Hit(HelpPage page, int score) {
		@Override
		public String toString() {
			return this.page.title();
		}
	}

	private record IndexedPage(HelpPage page, int order, String lowerTitle, String lowerText) {}

	private static final int TITLE_WEIGHT = 50;
	private static final int MAX_COUNTED_OCCURRENCES = 20;

	private final List<IndexedPage> indexedPages = new ArrayList<>();

	public HelpSearch(HelpBook book) {
		int order = 0;
		for (HelpPage page : book.getPages()) {
			this.indexedPages.add(new IndexedPage(page, order++, page.title().toLowerCase(Locale.ROOT),
					HelpBook.plainText(page).toLowerCase(Locale.ROOT)));
		}
	}

	/** The lower-case words of a query. */
	public static List<String> terms(String query) {
		if (query == null || query.isBlank()) return List.of();
		return Arrays.stream(query.toLowerCase(Locale.ROOT).trim().split("\\s+")).distinct().toList();
	}

	/** Matching pages, best first; empty for a blank query. */
	public List<Hit> search(String query) {
		List<String> terms = HelpSearch.terms(query);
		if (terms.isEmpty()) return List.of();
		record Ranked(Hit hit, int order) {}
		List<Ranked> ranked = new ArrayList<>();
		for (IndexedPage indexed : this.indexedPages) {
			int score = HelpSearch.score(indexed, terms);
			if (score <= 0) continue;
			ranked.add(new Ranked(new Hit(indexed.page(), score), indexed.order()));
		}
		ranked.sort(Comparator.comparingInt((Ranked r) -> -r.hit().score()).thenComparingInt(Ranked::order));
		return ranked.stream().map(Ranked::hit).toList();
	}

	/** 0 unless every term occurs somewhere in the page. */
	private static int score(IndexedPage indexed, List<String> terms) {
		int score = 0;
		for (String term : terms) {
			int titleHits = HelpSearch.countOccurrences(indexed.lowerTitle(), term);
			int textHits = HelpSearch.countOccurrences(indexed.lowerText(), term);
			if (titleHits == 0 && textHits == 0) return 0;
			score += titleHits * TITLE_WEIGHT + Math.min(textHits, MAX_COUNTED_OCCURRENCES);
		}
		return score;
	}

	static int countOccurrences(String haystack, String needle) {
		int count = 0;
		for (int index = haystack.indexOf(needle); index >= 0; index = haystack.indexOf(needle, index + needle.length())) {
			count++;
		}
		return count;
	}
}
