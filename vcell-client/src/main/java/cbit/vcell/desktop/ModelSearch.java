package cbit.vcell.desktop;

import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * How well a model matches a search, for listing the best matches first. A match on the model's name ranks above one on
 * its owner's name, which ranks above one in its description (annotation); within the name, an exact match ranks
 * highest, then one at its start, then one at the start of a word in it, then one anywhere. In a description, only a
 * match at the start of a word counts: a search for "ran" (the GTPase) shouldn't find every "transport" and "membrane".
 * The advanced search's options (SearchOptions) say where to look, which models to list at all, and in what order.
 */
final class ModelSearch {
	/** The rank of a model the search doesn't match. */
	static final int NO_MATCH = Integer.MAX_VALUE;
	/** The rank of a model the search matches only in its description. */
	static final int IN_DESCRIPTION = 6;
	private static final int EXACT_NAME = 0, NAME_START = 1, NAME_WORD = 2, IN_NAME = 3, OWNER_WORD = 4, IN_OWNER = 5;

	private ModelSearch() {}

	/** The model's rank for the search text (lower is better; NO_MATCH if it doesn't match); 0 for every model, for none. */
	static int rank(VCDocumentInfo info, String searchText) {
		return ModelSearch.rank(info, searchText, SearchOptions.defaults().getFields());
	}

	/** As rank(info, searchText), looking for the text only in these fields. */
	static int rank(VCDocumentInfo info, String searchText, Set<SearchOptions.Field> fields) {
		if (ModelSearch.isBlank(searchText)) return 0;
		String text = searchText.strip().toLowerCase(Locale.ROOT);
		Version version = info.getVersion();
		String name = null == version.getName() ? "" : version.getName();
		String lowerName = name.toLowerCase(Locale.ROOT);
		if (fields.contains(SearchOptions.Field.NAME)) {
			if (lowerName.equals(text)) return EXACT_NAME;
			if (lowerName.startsWith(text)) return NAME_START;
			if (ModelSearch.startsAWord(name, text)) return NAME_WORD;
			if (lowerName.contains(text)) return IN_NAME;
		}
		if (fields.contains(SearchOptions.Field.OWNER)) {
			String owner = null == version.getOwner() ? "" : version.getOwner().getName();
			if (ModelSearch.startsAWord(owner, text)) return OWNER_WORD;
			if (owner.toLowerCase(Locale.ROOT).contains(text)) return IN_OWNER;
		}
		if (fields.contains(SearchOptions.Field.DESCRIPTION) && null != version.getAnnot() && ModelSearch.startsAWord(version.getAnnot(), text)) {
			return IN_DESCRIPTION;
		}
		return NO_MATCH;
	}

	/** The models the search matches, best first; models that match equally well keep their order. All, for no search. */
	static List<VCDocumentInfo> bestFirst(List<VCDocumentInfo> models, String searchText) {
		if (ModelSearch.isBlank(searchText)) return models;
		return models.stream()
				.filter(info -> NO_MATCH != ModelSearch.rank(info, searchText))
				.sorted(Comparator.comparingInt(info -> ModelSearch.rank(info, searchText))) // a stable sort
				.toList();
	}

	/**
	 * The models the search matches and the options let through, in the options' order: best match (models that match
	 * equally well keep their order; with no search text, all keep it), by name, or newest first.
	 */
	static List<VCDocumentInfo> search(List<VCDocumentInfo> models, String searchText, SearchOptions options, Date now) {
		Set<SearchOptions.Field> fields = options.getFields();
		List<VCDocumentInfo> found = models.stream()
				.filter(info -> options.accepts(info, now))
				.filter(info -> NO_MATCH != ModelSearch.rank(info, searchText, fields))
				.toList();
		return switch (options.getOrder()) {
			case BEST_MATCH -> ModelSearch.isBlank(searchText) ? found
					: found.stream().sorted(Comparator.comparingInt(info -> ModelSearch.rank(info, searchText, fields))).toList(); // stable
			case NAME -> found.stream().sorted(Comparator.comparing((VCDocumentInfo info) -> info.getVersion().getName(), String.CASE_INSENSITIVE_ORDER)).toList();
			case NEWEST -> found.stream().sorted(Comparator.comparing((VCDocumentInfo info) -> info.getVersion().getDate(),
					Comparator.nullsLast(Comparator.reverseOrder()))).toList();
		};
	}

	static boolean isBlank(String searchText) {
		return null == searchText || searchText.isBlank();
	}

	// Whether the (lower case) text appears in the string where a word starts: at its start, after a character that
	// isn't a letter or digit, or at a capital following a small letter ("Calcium" in "SpermCalcium")
	private static boolean startsAWord(String string, String text) {
		String lower = string.toLowerCase(Locale.ROOT);
		for (int index = lower.indexOf(text); index >= 0; index = lower.indexOf(text, index + 1)) {
			if (0 == index) return true;
			if (index >= string.length()) break; // lower case can be longer than the string (rare letters); stop there
			char before = string.charAt(index - 1), first = string.charAt(index);
			if (!Character.isLetterOrDigit(before)) return true;
			if (Character.isLowerCase(before) && Character.isUpperCase(first)) return true;
		}
		return false;
	}
}
