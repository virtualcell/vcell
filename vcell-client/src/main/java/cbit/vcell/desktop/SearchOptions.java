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
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

	/** Who may see a model, and whether it was published with a paper (Published) or curated. */
	public enum Access {
		PRIVATE("Private"), SHARED("Shared"), PUBLIC("Public"), PUBLISHED("Published"), CURATED("Curated");

		public final String label;

		Access(String label) {
			this.label = label;
		}
	}

	/** How recently a model was saved. */
	public enum Saved {
		ANY_TIME("Any time", 0), PAST_WEEK("Past week", 7), PAST_MONTH("Past month", 30), PAST_YEAR("Past year", 365);

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
	private final EnumSet<Approach> approaches;
	private final EnumSet<Access> access;
	private final Order order;

	private static final Set<VCDocumentType> ALL_TYPES = Set.of(VCDocumentType.BIOMODEL_DOC, VCDocumentType.MATHMODEL_DOC);
	private static final SearchOptions DEFAULTS = new SearchOptions(EnumSet.allOf(Field.class), ALL_TYPES, "", Saved.ANY_TIME,
			EnumSet.allOf(Approach.class), EnumSet.allOf(Access.class), Order.BEST_MATCH);

	public SearchOptions(Set<Field> fields, Set<VCDocumentType> types, String owner, Saved saved, Set<Approach> approaches,
			Set<Access> access, Order order) {
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
	public Set<Approach> getApproaches() { return EnumSet.copyOf(this.approaches); }
	public Set<Access> getAccess() { return EnumSet.copyOf(this.access); }
	public Order getOrder() { return this.order; }

	@Override
	public boolean equals(Object other) {
		return other instanceof SearchOptions that && this.fields.equals(that.fields) && this.types.equals(that.types)
				&& this.owner.equals(that.owner) && this.saved == that.saved && this.approaches.equals(that.approaches)
				&& this.access.equals(that.access) && this.order == that.order;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.fields, this.types, this.owner, this.saved, this.approaches, this.access, this.order);
	}

	/** How many of the options are narrowed (or reordered) from the defaults. */
	public int changedCount() {
		int changed = 0;
		if (!this.fields.equals(DEFAULTS.fields)) changed++;
		if (!this.types.equals(DEFAULTS.types)) changed++;
		if (!this.owner.isEmpty()) changed++;
		if (this.saved != DEFAULTS.saved) changed++;
		if (!this.approaches.equals(DEFAULTS.approaches)) changed++;
		if (!this.access.equals(DEFAULTS.access)) changed++;
		if (this.order != DEFAULTS.order) changed++;
		return changed;
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
		if (Saved.ANY_TIME != this.saved) {
			long since = now.getTime() - TimeUnit.DAYS.toMillis(this.saved.days);
			if (null == version.getDate() || version.getDate().getTime() < since) return false;
		}
		if (!this.approaches.equals(DEFAULTS.approaches) && !this.approachesOf(info)) return false;
		return this.access.equals(DEFAULTS.access) || SearchOptions.accessOf(info).stream().anyMatch(this.access::contains);
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
