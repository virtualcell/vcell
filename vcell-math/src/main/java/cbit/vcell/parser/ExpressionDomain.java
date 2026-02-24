package cbit.vcell.parser;

import java.util.*;

//TODO: if ever desired, after completing the TODO in ContinuousDomain, make this class
// generic of `T extends ContinuousDomain`
public class ExpressionDomain {
	//TODO: if allowed, using unicode for the empty set AND union symbol makes sense here
	public static final String EMPTY_SET_NOTATION = "()";
	public static final String UNION_NOTATION = "U";

	private final NavigableMap<ContinuousDomain.Term, ContinuousDomain> domNavMap;

	/**
	 * Constructs an <code>ExpressionDomain</code> from performing a Union of provided {@link ContinuousDomain} domains
	 * @param continuousDomains all parts / pieces of the <code>ExpressionDomain</code> to be created
	 */
	public ExpressionDomain(ContinuousDomain... continuousDomains) {
		NavigableMap<ContinuousDomain.Term, ContinuousDomain> domain = new TreeMap<>();
		for (ContinuousDomain dor : continuousDomains) {
			ExpressionDomain.addDomainSafely(domain, dor);
		}
		// WE USE THIS MAP AS THE HASH-CODE VALUE! IT MUST BE UNMODIFIABLE AFTER CONSTRUCTION
		this.domNavMap = Collections.unmodifiableNavigableMap(domain);
	}

	/**
	 * Constructs an <code>ExpressionDomain</code> from performing a Union of provided {@link ContinuousDomain} domains
	 * @param continuousDomainNotations all parts / pieces of the <code>ExpressionDomain</code> to be created as strings
	 */
	public ExpressionDomain(String... continuousDomainNotations) {
		this(Arrays.stream(continuousDomainNotations).map(ContinuousDomain::new).toArray(ContinuousDomain[]::new));
	}


	public static ExpressionDomain unionOfDomains(ExpressionDomain exprDomain1, ExpressionDomain exprDomain2) {
		List<ContinuousDomain> allDomains = new ArrayList<>(exprDomain1.domNavMap.values());
		allDomains.addAll(exprDomain2.domNavMap.values());
		return new ExpressionDomain(allDomains.toArray(ContinuousDomain[]::new));
	}

	public static ExpressionDomain intersectionOfDomains(ExpressionDomain exprDomain1, ExpressionDomain exprDomain2) {
		//TODO: Rewrite so that it's more efficient, not needing 5x other set-like calls
		ExpressionDomain union = ExpressionDomain.unionOfDomains(exprDomain1, exprDomain2);
		ExpressionDomain oneDiffTwo = ExpressionDomain.differenceOfDomains(exprDomain1, exprDomain2);
		ExpressionDomain twoDiffOne = ExpressionDomain.differenceOfDomains(exprDomain2, exprDomain1);
		ExpressionDomain diffUnion = ExpressionDomain.unionOfDomains(oneDiffTwo, twoDiffOne);
		return ExpressionDomain.differenceOfDomains(union, diffUnion);
	}

	public static ExpressionDomain differenceOfDomains(ExpressionDomain startingDomain, ExpressionDomain toDifferenceDomain) {
		NavigableMap<ContinuousDomain.Term, ContinuousDomain> modifiableDomain = new TreeMap<>(startingDomain.domNavMap);
		for (ContinuousDomain nextDomain : toDifferenceDomain.domNavMap.values()) {
			ExpressionDomain.removeDomainSafely(modifiableDomain, nextDomain);
		}
		return new ExpressionDomain(modifiableDomain.values().toArray(ContinuousDomain[]::new));
	}

	private static void addDomainSafely(NavigableMap<ContinuousDomain.Term, ContinuousDomain> exprDomain, ContinuousDomain newDOR){
		if (newDOR == null || newDOR.isEmptyDomain()) return;
		ContinuousDomain termToAdd = newDOR;

		ContinuousDomain.Term leftNeighbor = exprDomain.floorKey(newDOR.min());
		if (leftNeighbor != null) {
			List<ContinuousDomain> unionizedDomains = ContinuousDomain.unifyDomains(termToAdd, exprDomain.get(leftNeighbor));
			if (unionizedDomains.size() == 1){
				termToAdd = unionizedDomains.get(0);
				exprDomain.remove(leftNeighbor); // has been integrated into the "new" domain
			} // else, no simplification was possible. no need to do anything.
		}

		ContinuousDomain.Term rightNeighbor = exprDomain.ceilingKey(newDOR.min());
		if (rightNeighbor != null) {
			List<ContinuousDomain> unionizedDomains = ContinuousDomain.unifyDomains(termToAdd, exprDomain.get(rightNeighbor));
			if (unionizedDomains.size() == 1){
				termToAdd = unionizedDomains.get(0);
				exprDomain.remove(rightNeighbor); // has been integrated into the "new" domain
			} // else, no simplification was possible. no need to do anything.
		}

		exprDomain.put(termToAdd.min(), termToAdd);
	}

	private static void removeDomainSafely(NavigableMap<ContinuousDomain.Term, ContinuousDomain> modifiableDomain, ContinuousDomain termToRemove){
		if (termToRemove == null || termToRemove.isEmptyDomain()) return;

		// Chop what min touches
		ContinuousDomain.Term potentialLowerBound = modifiableDomain.floorKey(termToRemove.min());
		if (potentialLowerBound != null && modifiableDomain.get(potentialLowerBound).containsTerm(termToRemove.min())) {
			ContinuousDomain formerBound = modifiableDomain.remove(potentialLowerBound);
			List<ContinuousDomain> remainingDomains = ContinuousDomain.differenceOfDomains(formerBound, termToRemove);
			for (ContinuousDomain remainingDomain : remainingDomains)
				if (!remainingDomain.isEmptyDomain())
					modifiableDomain.put(remainingDomain.min(), remainingDomain);
			potentialLowerBound = modifiableDomain.floorKey(termToRemove.min()); // resample the lower bound after cut
		}

		// Chop anything between min and max, then chop what max touches
		List<ContinuousDomain> toAddBackIn = new ArrayList<>(); // We separate the "add back in" to prevent infinite loops"
		ContinuousDomain.Term potentialUpperBound = modifiableDomain.floorKey(termToRemove.max());
		if (potentialUpperBound != null && !potentialUpperBound.equals(potentialLowerBound)) {
			// Anything between lower bound and upper bound must be chopped!
			while (!potentialUpperBound.equals(modifiableDomain.ceilingKey(termToRemove.min()))){
				// We perform a double check for literal edge cases: inclusion vs non-inclusion in comparison is difficult when one must be "greater" than the other...
				ContinuousDomain toPotentiallyRemove = modifiableDomain.remove(modifiableDomain.ceilingKey(termToRemove.min()));
				List<ContinuousDomain> doubleCheck = ContinuousDomain.differenceOfDomains(toPotentiallyRemove, termToRemove);
				if (doubleCheck.isEmpty() || (doubleCheck.size() == 1 && doubleCheck.get(0).isEmptyDomain())) continue;
				// If we didn't get the empty-set back, then we almost tossed away domain we needed!
				toAddBackIn.addAll(doubleCheck);
			}
			for (ContinuousDomain domain : toAddBackIn) modifiableDomain.put(domain.min(), domain); // Restore cut pieces that would have caused a loop.

			ContinuousDomain formerBound = modifiableDomain.remove(potentialUpperBound);
			List<ContinuousDomain> remainingDomains = ContinuousDomain.differenceOfDomains(formerBound, termToRemove);
			for (ContinuousDomain remainingDomain : remainingDomains)
				if (!remainingDomain.isEmptyDomain())
					modifiableDomain.put(remainingDomain.min(), remainingDomain);
		}
	}

	public ContinuousDomain[] getComponentContinuousDomains() {
		String thisObjectsNotation = this.toNotation();
		String[] components = thisObjectsNotation.contains(ExpressionDomain.UNION_NOTATION) ?
				thisObjectsNotation.split(ExpressionDomain.UNION_NOTATION): new String[]{thisObjectsNotation};
		return Arrays.stream(components).map(ContinuousDomain::new).toArray(ContinuousDomain[]::new);
	}

	public boolean contains(double value){
		ContinuousDomain.Term wrapper = new ContinuousDomain.Term(value, true);
		ContinuousDomain.Term term = this.domNavMap.floorKey(wrapper);
		if (term == null) return false;
		ContinuousDomain domainToCheck = this.domNavMap.get(term);
		return domainToCheck.containsValue(value);
	}

	public double getMinimumPossibleValue(){
		if (this.domNavMap.isEmpty()) return Double.NaN;
		ContinuousDomain minDomain = this.domNavMap.ceilingEntry(new ContinuousDomain.Term(Double.NEGATIVE_INFINITY, false)).getValue();
		ContinuousDomain.Term minBound = minDomain.min();
		double potentialValue = Double.isInfinite(minBound.value()) ? -Double.MAX_VALUE : minBound.value();
		return minBound.isInclusive() ? potentialValue : potentialValue + Double.MIN_VALUE;
	}

	public double getMaximumPossibleValue(){
		if (this.domNavMap.isEmpty()) return Double.NaN;
		ContinuousDomain maxDomain = this.domNavMap.floorEntry(new ContinuousDomain.Term(Double.POSITIVE_INFINITY, false)).getValue();
		ContinuousDomain.Term maxBound = maxDomain.max();
		double potentialValue = Double.isInfinite(maxBound.value()) ? Double.MAX_VALUE : maxBound.value();
		return maxBound.isInclusive() ? potentialValue : potentialValue - Double.MIN_VALUE;
	}

	public String toNotation(){
		return this.domNavMap.isEmpty() ? EMPTY_SET_NOTATION : String.join(UNION_NOTATION, this.domNavMap.values().stream().map(ContinuousDomain::getNotation).toList());
	}

	@Override
	public String toString(){
		return String.format("%s -> %s", ExpressionDomain.class.getSimpleName(), this.toNotation());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ExpressionDomain that)) return false;
		return Objects.equals(this.domNavMap, that.domNavMap);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.domNavMap);
	}
}
