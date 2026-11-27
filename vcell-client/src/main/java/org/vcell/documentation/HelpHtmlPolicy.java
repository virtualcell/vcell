package org.vcell.documentation;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The last check before a help page is shown: the HTML may contain only the tags and attributes
 * Markdown produces, links only to allowed places, and images only from the sources the renderer
 * itself wrote.
 *
 * <p>This is an allowlist over the renderer's own output, which is regular (flexmark escapes
 * text and attribute values), so a simple tag scan is exact. It catches anything the renderer's
 * rules miss rather than repairing it: a page that fails is not shown.
 */
final class HelpHtmlPolicy {
	private static final Pattern TAG = Pattern.compile("<(/?)([a-zA-Z][a-zA-Z0-9]*)((?:\\s+[a-zA-Z-]+=\"[^\"<>]*\")*)\\s*/?>");
	private static final Pattern ATTRIBUTE = Pattern.compile("\\s+([a-zA-Z-]+)=\"([^\"<>]*)\"");
	private static final Pattern NUMBER = Pattern.compile("[0-9]{1,5}");
	private static final Pattern ALIGNMENT = Pattern.compile("left|center|right");
	/** Only the entities flexmark writes; "&#106;avascript:" would otherwise slip past the checks. */
	private static final Pattern OTHER_ENTITY = Pattern.compile("&(?!amp;|lt;|gt;|quot;)");
	private static final Pattern CODE_CLASS = Pattern.compile("language-[A-Za-z0-9_+-]{1,40}");

	private static final Set<String> NO_ATTRIBUTES = Set.of();
	private static final Map<String, Set<String>> ALLOWED = Map.ofEntries(
			Map.entry("p", NO_ATTRIBUTES), Map.entry("br", NO_ATTRIBUTES), Map.entry("hr", NO_ATTRIBUTES),
			Map.entry("h1", Set.of("id")), Map.entry("h2", Set.of("id")), Map.entry("h3", Set.of("id")),
			Map.entry("h4", Set.of("id")), Map.entry("h5", Set.of("id")), Map.entry("h6", Set.of("id")),
			Map.entry("a", Set.of("href", "title", "name")),
			Map.entry("img", Set.of("src", "alt", "width", "height")),
			Map.entry("strong", NO_ATTRIBUTES), Map.entry("em", NO_ATTRIBUTES),
			Map.entry("code", Set.of("class")), Map.entry("pre", NO_ATTRIBUTES),
			Map.entry("blockquote", NO_ATTRIBUTES),
			Map.entry("ul", NO_ATTRIBUTES), Map.entry("ol", Set.of("start")), Map.entry("li", NO_ATTRIBUTES),
			Map.entry("table", NO_ATTRIBUTES), Map.entry("thead", NO_ATTRIBUTES), Map.entry("tbody", NO_ATTRIBUTES),
			Map.entry("tr", NO_ATTRIBUTES), Map.entry("th", Set.of("align", "colspan")), Map.entry("td", Set.of("align", "colspan")));

	private HelpHtmlPolicy() {
	}

	/**
	 * @param allowedImageSource true for an image src the renderer wrote itself
	 * @return what is not allowed, or null when the HTML is fine
	 */
	static String findViolation(String html, Predicate<String> allowedImageSource) {
		int index = 0;
		while (true) {
			int open = html.indexOf('<', index);
			if (open < 0) return null;
			Matcher tag = TAG.matcher(html);
			tag.region(open, html.length());
			if (!tag.lookingAt()) return "markup that is not a plain tag at character " + open;
			String name = tag.group(2).toLowerCase();
			Set<String> allowedAttributes = ALLOWED.get(name);
			if (allowedAttributes == null) return "a <" + name + "> tag";
			if (tag.group(1).isEmpty()) {
				Matcher attribute = ATTRIBUTE.matcher(tag.group(3));
				while (attribute.find()) {
					String attributeName = attribute.group(1).toLowerCase();
					String value = attribute.group(2);
					if (!allowedAttributes.contains(attributeName)) return "a " + attributeName + " attribute on <" + name + ">";
					String problem = HelpHtmlPolicy.valueProblem(name, attributeName, value, allowedImageSource);
					if (problem != null) return problem;
				}
			} else if (!tag.group(3).isEmpty()) {
				return "attributes on a closing tag";
			}
			index = tag.end();
		}
	}

	private static String valueProblem(String tag, String attribute, String value, Predicate<String> allowedImageSource) {
		if ((attribute.equals("href") || attribute.equals("src")) && OTHER_ENTITY.matcher(value).find()) {
			return "an encoded address " + value;
		}
		switch (attribute) {
			case "href":
				if (HelpBook.isExternal(HelpHtmlPolicy.unescape(value))) {
					return HelpBook.isAllowedExternalLink(HelpHtmlPolicy.unescape(value)) ? null : "a link to " + value;
				}
				if (value.startsWith("//") || value.contains("\\")) return "a link to " + value;
				return null;
			case "src":
				return allowedImageSource.test(HelpHtmlPolicy.unescape(value)) ? null : "an image from " + value;
			case "width", "height", "colspan", "start":
				return NUMBER.matcher(value).matches() ? null : "a " + attribute + " of " + value;
			case "align":
				return ALIGNMENT.matcher(value).matches() ? null : "an alignment of " + value;
			case "class":
				return CODE_CLASS.matcher(value).matches() ? null : "a class of " + value + " on <" + tag + ">";
			default:
				return null; // id, name, title, alt: text only, already escaped
		}
	}

	private static String unescape(String value) {
		return value.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
	}
}
