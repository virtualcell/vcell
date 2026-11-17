package cbit.vcell.desktop;

import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.MathModelChildSummary;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * The advanced search's options: where the search text is looked for, and which models are listed at all (their kind,
 * owner, when they were saved, modeling approach and permissions), and in what order. Each starts at "anything"; a model
 * is listed if it passes every option narrowed from that.
 */
public final class SearchOptions {
	/** Where the search text is looked for. */
	public enum Field {
		NAME("Name"), OWNER("Owner"), DESCRIPTION("Description");

		public final String label;

		Field(String label) {
			this.label = label;
		}
	}

	/** How a model's applications (or a MathModel itself) are simulated. */
	public enum Approach {
		DETERMINISTIC("Deterministic", MathType.Deterministic),
		STOCHASTIC("Stochastic", MathType.Stochastic),
		RULE_BASED("Rule-based", MathType.RuleBased),
		SPRINGSALAD("SpringSaLaD", MathType.SpringSaLaD);

		public final String label;
		private final MathType mathType;

		Approach(String label, MathType mathType) {
			this.label = label;
			this.mathType = mathType;
		}
	}

	/** Whether a model is spatial: has a geometry of one dimension or more (a BioModel: any of its applications). */
	public enum Space {
		SPATIAL("Spatial"), NON_SPATIAL("Non-spatial");

		public final String label;

		Space(String label) {
			this.label = label;
		}
	}

	/** Who may see a model, and whether it was published with a paper (Published) or curated. */
	public enum Access {
		PRIVATE("Private"), SHARED("Shared"), PUBLIC("Public"), PUBLISHED("Published"), CURATED("Curated");

		public final String label;

		Access(String label) {
			this.label = label;
		}
	}

	/** How recently a model was saved: within a number of days, or between two dates (see getSavedFrom, getSavedTo). */
	public enum Saved {
		ANY_TIME("Any time", 0), PAST_WEEK("Past week", 7), PAST_MONTH("Past month", 30), PAST_YEAR("Past year", 365),
		BETWEEN("Between", 0);

		public final String label;
		private final int days;

		Saved(String label, int days) {
			this.label = label;
			this.days = days;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

	/** The order matching models are listed in. */
	public enum Order {
		BEST_MATCH("Best match"), NAME("Name"), NEWEST("Newest");

		public final String label;

		Order(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

	private final EnumSet<Field> fields;
	private final Set<VCDocumentType> types;
	private final String owner;
	private final Saved saved;
	private final LocalDate savedFrom, savedTo; // Between's dates, both included; either null for no limit that way
	private final EnumSet<Approach> approaches;
	private final EnumSet<Space> spaces;
	private final EnumSet<Access> access;
	private final Order order;

	private static final Set<VCDocumentType> ALL_TYPES = Set.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC);
	private static final SearchOptions DEFAULTS = new SearchOptions(EnumSet.allOf(Field.class), ALL_TYPES, "", Saved.ANY_TIME,
			EnumSet.allOf(Approach.class), EnumSet.allOf(Access.class), Order.BEST_MATCH);

	public SearchOptions(Set<Field> fields, Set<VCDocumentType> types, String owner, Saved saved, Set<Approach> approaches,
			Set<Access> access, Order order) {
		this(fields, types, owner, saved, null, null, approaches, access, order);
	}

	/** As above; for Saved Between, the first and last days a model may have been saved on (null: no limit that way). */
	public SearchOptions(Set<Field> fields, Set<VCDocumentType> types, String owner, Saved saved, LocalDate savedFrom, LocalDate savedTo,
			Set<Approach> approaches, Set<Access> access, Order order) {
		this(fields, types, owner, saved, savedFrom, savedTo, approaches, EnumSet.allOf(Space.class), access, order);
	}

	/** As above, with whether models are to be spatial, non-spatial or either. */
	public SearchOptions(Set<Field> fields, Set<VCDocumentType> types, String owner, Saved saved, LocalDate savedFrom, LocalDate savedTo,
			Set<Approach> approaches, Set<Space> spaces, Set<Access> access, Order order) {
		this.spaces = spaces.isEmpty() ? EnumSet.noneOf(Space.class) : EnumSet.copyOf(spaces);
		boolean between = Saved.BETWEEN == saved;
		boolean backwards = null != savedFrom && null != savedTo && savedFrom.isAfter(savedTo); // the later typed first: the same range
		this.savedFrom = !between ? null : backwards ? savedTo : savedFrom; // only Between has dates
		this.savedTo = !between ? null : backwards ? savedFrom : savedTo;
		this.fields = fields.isEmpty() ? EnumSet.noneOf(Field.class) : EnumSet.copyOf(fields);
		this.types = Set.copyOf(types);
		this.owner = null == owner ? "" : owner.strip();
		this.saved = Objects.requireNonNull(saved);
		this.approaches = approaches.isEmpty() ? EnumSet.noneOf(Approach.class) : EnumSet.copyOf(approaches);
		this.access = access.isEmpty() ? EnumSet.noneOf(Access.class) : EnumSet.copyOf(access);
		this.order = Objects.requireNonNull(order);
	}

	/** Every model, the search text looked for everywhere, best matches first. */
	public static SearchOptions defaults() {
		return DEFAULTS;
	}

	public Set<Field> getFields() { return EnumSet.copyOf(this.fields); }
	public Set<VCDocumentType> getTypes() { return this.types; }
	public String getOwner() { return this.owner; }
	public Saved getSaved() { return this.saved; }
	public LocalDate getSavedFrom() { return this.savedFrom; }
	public LocalDate getSavedTo() { return this.savedTo; }
	public Set<Approach> getApproaches() { return EnumSet.copyOf(this.approaches); }
	public Set<Space> getSpaces() { return this.spaces.isEmpty() ? EnumSet.noneOf(Space.class) : EnumSet.copyOf(this.spaces); }
	public Set<Access> getAccess() { return EnumSet.copyOf(this.access); }
	public Order getOrder() { return this.order; }

	@Override
	public boolean equals(Object other) {
		return other instanceof SearchOptions that && this.fields.equals(that.fields) && this.types.equals(that.types)
				&& this.owner.equals(that.owner) && this.saved == that.saved && Objects.equals(this.savedFrom, that.savedFrom)
				&& Objects.equals(this.savedTo, that.savedTo) && this.approaches.equals(that.approaches) && this.spaces.equals(that.spaces)
				&& this.access.equals(that.access) && this.order == that.order;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fields, this.types, this.owner, this.saved, this.savedFrom, this.savedTo, this.approaches, this.spaces, this.access, this.order);
	}

	/** How many of the options are narrowed (or reordered) from the defaults. */
	public int changedCount() {
		int changed = 0;
		if (!this.fields.equals(DEFAULTS.fields)) changed++;
		if (!this.types.equals(DEFAULTS.types)) changed++;
		if (!this.owner.isEmpty()) changed++;
		if (this.saved != DEFAULTS.saved) changed++;
		if (!this.approaches.equals(DEFAULTS.approaches)) changed++;
		if (!this.spaces.equals(DEFAULTS.spaces)) changed++;
		if (!this.access.equals(DEFAULTS.access)) changed++;
		if (this.order != DEFAULTS.order) changed++;
		return changed;
	}

	/**
	 * What differs from the defaults, one line an option: "Model type: −MathModels" for a box unchecked (+ for one
	 * checked), and the new value for the rest. Empty for the defaults.
	 */
	public List<String> describeChanges() {
		List<String> changes = new ArrayList<>();
		SearchOptions.addChange(changes, "Search in", DEFAULTS.fields, this.fields, field -> field.label);
		SearchOptions.addChange(changes, "Model type", DEFAULTS.types, this.types, SearchOptions::typeLabel);
		if (!this.owner.isEmpty()) changes.add("Owner: contains “" + this.owner + "”");
		if (this.saved != DEFAULTS.saved) changes.add("Saved: " + this.describeSaved());
		SearchOptions.addChange(changes, "Approach", DEFAULTS.approaches, this.approaches, approach -> approach.label);
		SearchOptions.addChange(changes, "Geometry", DEFAULTS.spaces, this.spaces, space -> space.label);
		SearchOptions.addChange(changes, "Permissions", DEFAULTS.access, this.access, permission -> permission.label);
		if (this.order != DEFAULTS.order) changes.add("Order: " + this.order.label);
		return changes;
	}

	// "Category: +Added, −Removed", in the options' own order, if anything was added or removed
	private static <E> void addChange(List<String> changes, String category, Set<E> defaults, Set<E> chosen, Function<E, String> label) {
		List<String> differences = new ArrayList<>();
		for (E value : defaults) if (!chosen.contains(value)) differences.add("−" + label.apply(value));
		for (E value : chosen) if (!defaults.contains(value)) differences.add("+" + label.apply(value));
		if (differences.isEmpty()) return;
		changes.add(category + ": " + String.join(", ", differences));
	}

	// "Past week"; for Between, "between Mar 1, 2024 and Nov 17, 2026", "since ..." or "until ...", or "any time"
	private String describeSaved() {
		if (Saved.BETWEEN != this.saved) return this.saved.label;
		DateTimeFormatter format = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
		if (null != this.savedFrom && null != this.savedTo) return "between " + format.format(this.savedFrom) + " and " + format.format(this.savedTo);
		if (null != this.savedFrom) return "since " + format.format(this.savedFrom);
		if (null != this.savedTo) return "until " + format.format(this.savedTo);
		return "any time";
	}

	private static String typeLabel(VCDocumentType type) {
		return VCDocumentType.BIOMODEL_DOC == type ? "BioModels" : VCDocumentType.MATHMODEL_DOC == type ? "MathModels" : type.name();
	}

	/** Whether the model passes every option narrowed from the defaults (whatever the search text). */
	boolean accepts(VCDocumentInfo info, Date now) {
		Version version = info.getVersion();
		// narrowed only once asked: by default, any kind of document a list shows (geometries too, where it shows them)
		if (!this.types.equals(DEFAULTS.types) && !this.types.contains(ModelListRenderer.getDocumentType(info))) return false;
		if (!this.owner.isEmpty()) {
			String ownerName = null == version.getOwner() ? "" : version.getOwner().getName();
			if (!ownerName.toLowerCase(Locale.ROOT).contains(this.owner.toLowerCase(Locale.ROOT))) return false;
		}
		if (Saved.BETWEEN == this.saved) {
			if (!this.savedBetween(version.getDate())) return false;
		} else if (Saved.ANY_TIME != this.saved) {
			long since = now.getTime() - TimeUnit.DAYS.toMillis(this.saved.days);
			if (null == version.getDate() || version.getDate().getTime() < since) return false;
		}
		if (!this.approaches.equals(DEFAULTS.approaches) && !this.approachesOf(info)) return false;
		if (!this.spaces.equals(DEFAULTS.spaces) && SearchOptions.spacesOf(info).stream().noneMatch(this.spaces::contains)) return false;
		return this.access.equals(DEFAULTS.access) || SearchOptions.accessOf(info).stream().anyMatch(this.access::contains);
	}

	// Saved on a day from savedFrom to savedTo, both included, as days go here (the computer's time zone)
	private boolean savedBetween(Date saved) {
		if (null == this.savedFrom && null == this.savedTo) return true;
		if (null == saved) return false;
		LocalDate day = saved.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (null != this.savedFrom && day.isBefore(this.savedFrom)) return false;
		return null == this.savedTo || !day.isAfter(this.savedTo);
	}

	// Whether any of the model's applications (a MathModel: the model itself) uses an approach asked for
	private boolean approachesOf(VCDocumentInfo info) {
		Set<MathType> mathTypes = EnumSet.noneOf(MathType.class);
		if (info instanceof BioModelInfo bioModel && null != bioModel.getBioModelChildSummary()) {
			BioModelChildSummary summary = bioModel.getBioModelChildSummary();
			if (null != summary.getAppTypes()) for (MathType mathType : summary.getAppTypes()) if (null != mathType) mathTypes.add(mathType);
		}
		if (info instanceof MathModelInfo mathModel && null != mathModel.getMathModelChildSummary()) {
			MathModelChildSummary summary = mathModel.getMathModelChildSummary();
			if (null != summary.getModelType()) mathTypes.add(summary.getModelType());
		}
		return this.approaches.stream().anyMatch(approach -> mathTypes.contains(approach.mathType));
	}

	/**
	 * Whether a model is spatial, non-spatial or (a BioModel with both kinds of application) both: by its geometries'
	 * dimensions, 0 being none. A BioModel with no applications has no geometry: non-spatial.
	 */
	static Set<Space> spacesOf(VCDocumentInfo info) {
		Set<Space> spaces = EnumSet.noneOf(Space.class);
		if (info instanceof BioModelInfo bioModel) {
			BioModelChildSummary summary = bioModel.getBioModelChildSummary();
			int[] dimensions = null == summary ? null : summary.getGeometryDimensions();
			if (null != dimensions) for (int dimension : dimensions) spaces.add(dimension > 0 ? Space.SPATIAL : Space.NON_SPATIAL);
		} else if (info instanceof MathModelInfo mathModel && null != mathModel.getMathModelChildSummary()) {
			spaces.add(mathModel.getMathModelChildSummary().getGeometryDimension() > 0 ? Space.SPATIAL : Space.NON_SPATIAL);
		} else if (info instanceof cbit.vcell.geometry.GeometryInfo geometry) {
			spaces.add(geometry.getDimension() > 0 ? Space.SPATIAL : Space.NON_SPATIAL);
		}
		if (spaces.isEmpty()) spaces.add(Space.NON_SPATIAL);
		return spaces;
	}

	/** Which permission groups the model is in: private, shared or public; and published or curated, if so. */
	static Set<Access> accessOf(VCDocumentInfo info) {
		Version version = info.getVersion();
		BigDecimal groupId = null == version.getGroupAccess() ? GroupAccess.GROUPACCESS_NONE : version.getGroupAccess().getGroupid();
		Set<Access> access = EnumSet.of(GroupAccess.GROUPACCESS_ALL.equals(groupId) ? Access.PUBLIC
				: GroupAccess.GROUPACCESS_NONE.equals(groupId) ? Access.PRIVATE : Access.SHARED);
		PublicationInfo[] publications = info.getPublicationInfos();
		if (null != publications && publications.length > 0) {
			access.add(null != version.getFlag() && version.getFlag().compareEqual(VersionFlag.Published) ? Access.PUBLISHED : Access.CURATED);
		}
		return access;
	}
}
