package cbit.vcell.parser;

import java.util.*;

//TODO: if ever desired, upgrade this to have two variants of
// children, one using double, the other using `BigDecimal`;
public record ContinuousDomain(Term min, Term max) implements Comparable<ContinuousDomain> {
	//TODO: if allowed, using unicode for the empty set makes sense here
	private static final String EMPTY_SET_NOTATION = "()";

	public static ContinuousDomain getEmptyDomain() {
		return new ContinuousDomain(Double.POSITIVE_INFINITY, false, Double.NEGATIVE_INFINITY, false);
	}

	/**
	 * Main constructor of <code>ContinuousDomain</code>; performs input validation
	 * @param min lower bound of the domain
	 * @param max upper bound of the domain
	 */
	public ContinuousDomain(Term min, Term max) {
		if (min == null) throw new IllegalArgumentException("min cannot be null");
		if (max == null) throw new IllegalArgumentException("max cannot be null");
		if (Double.isNaN(min.value())) throw new IllegalArgumentException("min argument cannot have NaN value");
		if (Double.isNaN(max.value())) throw new IllegalArgumentException("max argument cannot have NaN value");
		// Ensure valid infinities are
		boolean lowerBoundIsAnInfinity = min.value() == Double.NEGATIVE_INFINITY || min.value() == Double.POSITIVE_INFINITY;
		boolean upperBoundIsAnInfinity = max.value() == Double.NEGATIVE_INFINITY || max.value() == Double.POSITIVE_INFINITY;
		Term lowerBound = lowerBoundIsAnInfinity ? new Term(min.value(), false) : min;
		Term upperBound = upperBoundIsAnInfinity ? new Term(max.value(), false) : max;
		// Standardize the "empty domain" as min > max, encoded as min -> `inf`, max -> `-inf`
		boolean shouldMakeEmptySet = min.value() > max.value() || (min.value() == max.value() && !(min.isInclusive() && max.isInclusive()));
		this.min = shouldMakeEmptySet ? new Term(Double.POSITIVE_INFINITY, true) : lowerBound;
		this.max = shouldMakeEmptySet ? new Term(Double.NEGATIVE_INFINITY, true) : upperBound;
	}

	/**
	 * Builds the domain of all reals `(-inf, inf)`
	 */
	public ContinuousDomain() {
		this(Double.NEGATIVE_INFINITY, true, Double.POSITIVE_INFINITY, true);
	}

	/**
	 * Builds a domain based off of the provided string. The string should be in math-notation form
	 *
	 * @param domain domain to create
	 */
	public ContinuousDomain(String domain) {
		this(ContinuousDomain.notationToLowerBound(domain), ContinuousDomain.notationToUpperBound(domain));
	}

	/**
	 * Builds a domain from min and max values, and whether they are inclusive or not
	 * Note that if min == max, but inclusivities are not both true, the empty/null domain will be created
	 *
	 * @param min          lower bound of the range
	 * @param minInclusive inclusiveness of the lower bound
	 * @param max          upper bound of the range
	 * @param maxInclusive inclusiveness of the upper bound
	 */
	public ContinuousDomain(double min, boolean minInclusive, double max, boolean maxInclusive) {
		this(new Term(min, minInclusive), new Term(max, maxInclusive));
	}

	public boolean isEmptyDomain() {
		return this.min.value > this.max.value;
	}

	public static ContinuousDomain intersectDomains(ContinuousDomain... domains) {
		return ContinuousDomain.intersectDomains(Arrays.asList(domains));
	}

	public static ContinuousDomain intersectDomains(List<ContinuousDomain> domains) {
		if (domains == null || domains.isEmpty()) return ContinuousDomain.getEmptyDomain();
		if (domains.size() == 1) return domains.get(0);
		if (domains.size() == 2) return ContinuousDomain.intersectionOfDomains(domains.get(0), domains.get(1));
		List<ContinuousDomain> reductions = new ArrayList<>();
		for (int i = 0; i < domains.size(); i += 2) {
			if (i + 1 == domains.size()) {
				reductions.add(domains.get(i));
				continue;
			}
			reductions.add(ContinuousDomain.intersectionOfDomains(domains.get(i), domains.get(i + 1)));
		}
		return ContinuousDomain.intersectDomains(reductions);
	}

	private static ContinuousDomain intersectionOfDomains(ContinuousDomain domain1, ContinuousDomain domain2) {
		if (domain1 == null || domain2 == null) throw new IllegalArgumentException("Domains cannot be null");
		if (domain1.isEmptyDomain()) return domain1;
		if (domain2.isEmptyDomain()) return domain2;

		Term minTerm = Term.minimumIntersectionPoint(domain1.min, domain2.min);
		Term maxTerm = Term.maximumIntersectionPoint(domain1.max, domain2.max);
		return new ContinuousDomain(minTerm, maxTerm); // Constructor handles empty sets
	}

	public static List<ContinuousDomain> unifyDomains(ContinuousDomain... domains) {
		return ContinuousDomain.unifyDomains(Arrays.asList(domains));
	}

	public static List<ContinuousDomain> unifyDomains(List<ContinuousDomain> domains) {
		if (domains == null || domains.isEmpty()) return List.of();
		if (domains.size() == 1) return Collections.unmodifiableList(domains);
		if (domains.size() == 2) return ContinuousDomain.unionOfDomains(domains.get(0), domains.get(1));
		Queue<ContinuousDomain> sortedDomains = new LinkedList<>(domains.stream().filter(Objects::nonNull).sorted().toList());
		List<ContinuousDomain> reductions = new ArrayList<>();
		ContinuousDomain currentDomain = sortedDomains.poll();
		while (!sortedDomains.isEmpty()) {
			ContinuousDomain nextDomain = sortedDomains.poll();
			List<ContinuousDomain> unionDomains = ContinuousDomain.unionOfDomains(currentDomain, nextDomain);
			if (unionDomains.size() == 1) {
				currentDomain = unionDomains.get(0);
				continue;
			}
			reductions.add(unionDomains.get(0));
			currentDomain = unionDomains.get(1);
		}
		reductions.add(currentDomain);

		if (reductions.size() == domains.size()) return Collections.unmodifiableList(reductions);
		return ContinuousDomain.unifyDomains(reductions);
	}


	private static List<ContinuousDomain> unionOfDomains(ContinuousDomain domain1, ContinuousDomain domain2) {
		if (domain1 == null || domain2 == null) throw new IllegalArgumentException("Domains cannot be null");
		if (domain1.equals(domain2) || domain1.isEmptyDomain()) return List.of(domain2);
		if (domain2.isEmptyDomain()) return List.of(domain1);
		// four cases:
		// (1) one domain supersets the other
		ContinuousDomain superDomain = ContinuousDomain.getSuperSetDomain(domain1, domain2);
		if (superDomain != null) return List.of(superDomain);
		// (2) the domains do overlap other just barely connect
		ContinuousDomain lesserDomain = ContinuousDomain.getLesserDomain(domain1, domain2);
		ContinuousDomain greaterDomain = ContinuousDomain.getGreaterDomain(domain1, domain2);
		if (lesserDomain.max.value() > greaterDomain.min.value() || Term.termsConnect(lesserDomain.max, greaterDomain.min))
			return List.of(new ContinuousDomain(lesserDomain.min, greaterDomain.max));
		// (3) the domains have no overlap nor connection
		return List.of(lesserDomain, greaterDomain);
	}

	public static List<ContinuousDomain> differenceOfDomains(ContinuousDomain minuendDomain, ContinuousDomain subtrahendDomain) {
		if (minuendDomain == null || subtrahendDomain == null)
			throw new IllegalArgumentException("Domains cannot be null");
		if (minuendDomain.equals(subtrahendDomain) || minuendDomain.isEmptyDomain())
			return List.of(ContinuousDomain.getEmptyDomain());
		if (subtrahendDomain.isEmptyDomain()) return List.of(minuendDomain);
		ContinuousDomain lesserDomain = ContinuousDomain.getLesserDomain(minuendDomain, subtrahendDomain);
		ContinuousDomain greaterDomain = ContinuousDomain.getGreaterDomain(minuendDomain, subtrahendDomain);
		boolean minuendIsLesser = lesserDomain.equals(minuendDomain);
		boolean minuendIsGreater = greaterDomain.equals(minuendDomain);
		// five cases:
		// (0) subtrahend domain supersets the minuend (return empty set)
		if (!minuendIsLesser && !minuendIsGreater) return List.of(ContinuousDomain.getEmptyDomain());
		// (1) there isn't any overlap whatsoever
		boolean hasOverlap = minuendDomain.containsValue(subtrahendDomain.min.value()) || minuendDomain.containsValue(subtrahendDomain.max.value());
		if (!hasOverlap) return List.of(minuendDomain);
		// (2) There is partial overlap
		if (minuendIsLesser ^ minuendIsGreater)
			return List.of(getDiffFromOverlap(minuendDomain, subtrahendDomain, minuendIsLesser));
		// (3) minuend supersets the subtrahend
		ContinuousDomain lowerHalf = new ContinuousDomain(minuendDomain.min.value(), minuendDomain.min.isInclusive(),
				subtrahendDomain.min.value(), !subtrahendDomain.min.isInclusive());
		ContinuousDomain upperHalf = new ContinuousDomain(subtrahendDomain.max.value(), !subtrahendDomain.max.isInclusive(),
				minuendDomain.max.value(), minuendDomain.max.isInclusive());
		return List.of(lowerHalf, upperHalf);
	}

	private static ContinuousDomain getDiffFromOverlap(ContinuousDomain minuendDomain, ContinuousDomain subtrahendDomain, boolean minuendIsLesser) {
		ContinuousDomain diffDomain;
		if (minuendIsLesser) {   // (2a) minuend shares an overlapping subset with the subtrahend; minuend is lesser
			diffDomain = new ContinuousDomain(minuendDomain.min.value(), minuendDomain.min.isInclusive(),
					subtrahendDomain.min.value(), !subtrahendDomain.min.isInclusive());
		} else {                // (2b) minuend shares an overlapping subset with the subtrahend; minuend is greater
			diffDomain = new ContinuousDomain(subtrahendDomain.max.value(), !subtrahendDomain.max.isInclusive(),
					minuendDomain.max.value(), minuendDomain.max.isInclusive());
		}
		return diffDomain;
	}

	// Return the domain with the lesser min term
	private static ContinuousDomain getLesserDomain(ContinuousDomain domain1, ContinuousDomain domain2) {
		Term minTerm = Term.getLesserTerm(domain1.min, domain2.min);
		if (minTerm.equals(domain2.min)) return domain2;
		return domain1;
	}

	// Return the domain with the greater max term
	private static ContinuousDomain getGreaterDomain(ContinuousDomain domain1, ContinuousDomain domain2) {
		Term maxTerm = Term.getGreaterTerm(domain1.max, domain2.max);
		if (maxTerm.equals(domain2.max)) return domain2;
		return domain1;
	}

	private static boolean shouldCreateEmptyDomain(Term min, Term max) {
		return min.value() > max.value() || (min.value() == max.value() && !(min.isInclusive() && max.isInclusive()));
	}

	/**
	 * Return which of the two domains is the super set of the other, or null if there exists no superset
	 *
	 * @param domain1 first domain to test
	 * @param domain2 second domain to test
	 * @return the superset-domain, or null if it doesn't exist
	 */
	public static ContinuousDomain getSuperSetDomain(ContinuousDomain domain1, ContinuousDomain domain2) {
		ContinuousDomain lesserDomain = ContinuousDomain.getLesserDomain(domain1, domain2);
		ContinuousDomain greaterDomain = ContinuousDomain.getGreaterDomain(domain1, domain2);
		if (lesserDomain == greaterDomain) return greaterDomain;
		return null;
	}


	public boolean containsValue(double value) {
		if (Double.isNaN(value) || this.isEmptyDomain()) return false;
		if (this.min.value() == value) return this.min.isInclusive() || Double.isInfinite(value);
		if (this.max.value() == value) return this.max.isInclusive() || Double.isInfinite(value);
		return this.min.value() < value && this.max.value() > value;
	}

	public boolean containsTerm(Term term) {
		if (this.min.equals(term) || this.max.equals(term)) return true;
		return this.min.value() < term.value() && this.max.value() > term.value();
	}

	public String getNotation() {
		if (this.min.isInclusive() && this.max.isInclusive() && this.min.value == this.max.value())
			return "[" + this.getBestStringRepresentation(this.min.value()) + "]";
		char leftChar = !this.min.isInclusive() || this.min.value() == Double.NEGATIVE_INFINITY ? '(' : '[';
		char rightChar = !this.max.isInclusive() || this.max.value() == Double.POSITIVE_INFINITY ? ')' : ']';
		return String.format("%c%s, %s%c", leftChar, this.getBestStringRepresentation(this.min.value()), this.getBestStringRepresentation(this.max.value()), rightChar);
	}

	private String getBestStringRepresentation(double value) {
		if (value % 1L == 0L) return Long.toString((long) value);
		return Double.toString(value);
	}

	public String toString() {
		if (this.isEmptyDomain()) return ContinuousDomain.class.getSimpleName() + " -> " + EMPTY_SET_NOTATION;
		return String.format("%s -> %s", ContinuousDomain.class.getSimpleName(), this.getNotation());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ContinuousDomain that)) return false;
		return Objects.equals(this.min, that.min) && Objects.equals(this.max, that.max);
	}

	@Override
	public int compareTo(ContinuousDomain domain) {
		if (this.equals(domain)) return 0;
		int minCompare = Double.compare(this.min.value(), domain.min.value());
		if (minCompare != 0) return minCompare;
		int maxCompare = Double.compare(this.max.value(), domain.max.value());
		if (maxCompare != 0) return maxCompare;
		int minTermCompare = this.min.compareTo(domain.min);
		if (minTermCompare != 0) return minTermCompare;
		return this.max.compareTo(domain.max);
	}

	private static Term notationToLowerBound(String domain){
		String trimmedDomain = ContinuousDomain.getTrimmedDomain(domain);
		return ContinuousDomain.partOfNotationToBound(trimmedDomain, BOUND_TYPE.LOWER);
	}

	private static Term notationToUpperBound(String domain){
		String trimmedDomain = ContinuousDomain.getTrimmedDomain(domain);
		return ContinuousDomain.partOfNotationToBound(trimmedDomain, BOUND_TYPE.UPPER);
	}

	private static String getTrimmedDomain(String domain){
		if (domain == null) throw new IllegalArgumentException("domain cannot be null");
		if (domain.isEmpty()) throw new IllegalArgumentException("domain cannot be empty");

		String trimmedDomain = domain.trim();
		if (!trimmedDomain.startsWith("[") && !trimmedDomain.startsWith("("))
			throw new IllegalArgumentException(String.format("Domain `%s` has an invalid lower bound symbol.", trimmedDomain));
		if (!trimmedDomain.endsWith("]") && !trimmedDomain.endsWith(")"))
			throw new IllegalArgumentException(String.format("Domain `%s` has an invalid upper bound symbol.", trimmedDomain));
		return trimmedDomain;
	}

	private static Term partOfNotationToBound(String trimmedDomain, BOUND_TYPE boundType) {
		double boundValue;
		String[] parts = ContinuousDomain.getPartsOfTrimmedStringDomain(trimmedDomain);
		int index = parts.length < 2 ? 0 : boundType.ordinal();
		try {
			String bondValueString = parts[index];
			if (bondValueString.isEmpty() && trimmedDomain.startsWith("(") && trimmedDomain.endsWith(")")) return new Term(boundType.getEmptySetBound(), false);
			boundValue = Double.parseDouble(bondValueString);
			if (Double.isNaN(boundValue))
				throw new IllegalArgumentException("`NaN` is not allowed within a domain");
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("Domain `%s` does not contain a parsable %s bound.", trimmedDomain, boundType.getName()), e);
		}
		boolean boundIsAnInfinity = (boundValue == Double.NEGATIVE_INFINITY || boundValue == Double.POSITIVE_INFINITY);
		return boundIsAnInfinity ? new Term(boundValue, false) : new Term(boundValue, boundType.shouldBeInclusive(trimmedDomain));
	}

	private static String[] getPartsOfTrimmedStringDomain(String trimmedDomain){
		String[] parts = trimmedDomain.substring(1, trimmedDomain.length() - 1).split(",");
		if (parts.length == 0 || parts.length > 2)
			throw new IllegalArgumentException(String.format("Domain `%s` does not contain an appropriate amount of separation commas.", trimmedDomain));
		return Arrays.stream(parts).map(String::trim).toArray(String[]::new);
	}

	public record Term(double value, boolean isInclusive) implements Comparable<Term> {

		/**
		 * @return whichever term has a greater maximum value
		 */
		public static Term getGreaterTerm(Term t1, Term t2) {
			if (Objects.equals(t1, t2)) return t2;
			if (t1.value > t2.value) return t1;
			if (t1.value < t2.value) return t2;
			if (t1.isInclusive && !t2.isInclusive) return t1;
			return t2;
		}

		/**
		 * @return whichever term has a less minimum value
		 */
		public static Term getLesserTerm(Term t1, Term t2) {
			if (Objects.equals(t1, t2)) return t2;
			if (t1.value < t2.value) return t1;
			if (t1.value > t2.value) return t2;
			if (t1.isInclusive && !t2.isInclusive) return t1;
			return t2;
		}

		public static Term maximumIntersectionPoint(Term t1, Term t2) {
			// We want the min, since it's the maximum *shared* value
			boolean equality = t1.value == t2.value;
			double val = equality ? t1.value : Math.min(t1.value, t2.value);
			boolean includes;
			if (equality) {
				includes = t1.isInclusive && t2.isInclusive; // e.g. "[3] vs. (3) => (3)" -> maximum shared is non-inclusive
			} else {
				includes = t1.value == val ? t1.isInclusive : t2.isInclusive; // the larger value's inclusion value is what matters
			}
			return new Term(val, includes);
		}

		public static Term minimumIntersectionPoint(Term t1, Term t2) {
			// We want the max, since it's the minimum *shared* value
			boolean equality = t1.value == t2.value;
			double val = equality ? t1.value : Math.max(t1.value, t2.value);
			boolean includes;
			if (equality) {
				includes = t1.isInclusive && t2.isInclusive; // e.g. "[3] vs. (3) => (3)" -> minimum shared is non-inclusive
			} else {
				includes = t1.value == val ? t1.isInclusive : t2.isInclusive; // the larger value's inclusion value is what matters
			}
			return new Term(val, includes);
		}

		public static boolean termsConnect(Term t1, Term t2) {
			Term lesser = Term.getLesserTerm(t1, t2);
			Term greater = Term.getGreaterTerm(t1, t2);
			return lesser.isInclusive && greater.isInclusive && lesser.value == greater.value;
		}

		@Override
		public String toString() {
			String inclusiveFormat = "%s -> [%f]";
			String nonInclusiveFormat = "%s -> (%f)";
			String format = this.isInclusive ? inclusiveFormat : nonInclusiveFormat;
			return String.format(format, Term.class.getSimpleName(), this.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.value, this.isInclusive);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Term other)) return false;
			return this.value == other.value && this.isInclusive == other.isInclusive;
		}

		@Override
		public int compareTo(Term other) {
			int comp = Double.compare(this.value(), other.value());
			if (comp != 0) return comp;
			boolean matching = this.isInclusive() == other.isInclusive();
			if (matching) return 0;
			if (this.isInclusive()) return 1;
			return -1;
		}
	}

	private enum BOUND_TYPE {
		LOWER,
		UPPER;

		public boolean shouldBeInclusive(String trimmedDomain){
			if (trimmedDomain == null || trimmedDomain.isEmpty()) throw new IllegalArgumentException("trimmedDomain is null or empty");
			return switch(this){
				case LOWER -> '[' == trimmedDomain.charAt(0);
				case UPPER -> ']' == trimmedDomain.charAt(trimmedDomain.length() - 1);
			};
		}

		public Double getEmptySetBound(){
			return switch (this) {
				case LOWER -> Double.POSITIVE_INFINITY;
				case UPPER -> Double.NEGATIVE_INFINITY;
			};
		}

		public String getName(){
			return this.name().toLowerCase();
		}
	}

}
