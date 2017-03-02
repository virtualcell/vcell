package org.vcell.chombo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.vcell.util.Extent;

import com.google.gson.Gson;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.solver.SolverDescription;

public class ChomboMeshValidator {
	private Extent extent;
	private int blockFactor;
	private int dim;

	public static class DomainARInfo implements Comparable<DomainARInfo> {
		double[] ar;
		int[] minNx;

		public DomainARInfo(double ar, int[] minNx) {
			super();
			this.ar = new double[] { ar };
			this.minNx = minNx;
		}

		public DomainARInfo(double[] ar, int[] minNx) {
			super();
			this.ar = ar;
			this.minNx = minNx;
		}
		
		@Override
		public String toString()
		{
			String s = StringUtils.join(ar, ' ') + " " + StringUtils.join(minNx, ' ');
			return s;
		}

		@Override
		public int compareTo(DomainARInfo o) {
			int compareTo = new Double(ar[0]).compareTo(o.ar[0]);
			if (ar.length == 2 && compareTo == 0)
			{
				compareTo = new Double(ar[1]).compareTo(o.ar[1]);
			}
			return compareTo;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(ar).append(minNx).hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DomainARInfo)
			{
				DomainARInfo rhs = (DomainARInfo)obj;
				return Objects.deepEquals(ar, rhs.ar) && Objects.deepEquals(minNx, rhs.minNx);
			}
			return false;
		}
	}

	public static class ChomboMeshRecommendation {
		private int dim;
		public List<ChomboMeshSpec> validMeshSpecList;
		public List<int[]> recommendedNxList;
		public double[] currentAR;
		public int[] bestRecommendedNx;
		private String[] dialogOptions;
		private String errorMessage;
		public static final String optionClose = "Close";
		public static final String optionSuggestions = "Domain Aspect Ratio Suggestions";
		
		private ChomboMeshRecommendation(int dim)
		{
			this.dim = dim;
		}
		
		public String getMeshSuggestions()
		{
			StringBuffer sb = new StringBuffer();
			sb.append("Suggestions for valid domain sizes (aspect ratios):\n"
					+ "Domain sizes that are proportional to any of the following sets of values are compatible with "
					+ SolverDescription.Chombo.getShortDisplayLabel() + " solver. "
							+ "In the Geometry Size panel, define SizeX, SizeY and SizeZ if 3D as one of the suggested sets below, multiplied by a scale factor. "
							+ "(In general, aspect ratios proportional to small integers are preferable.)\n");
			for (int[] nx : recommendedNxList)
			{
				sb.append(nx[0]);
				if (dim > 1)
				{
					sb.append(" : ").append(nx[1]);
					if (dim > 2)
					{
						sb.append(" : ").append(nx[2]);
					}
				}
				sb.append("\n");
			}
			return sb.toString();
		}
		
		public boolean validate()
		{
			boolean bGood = true;
			if (validMeshSpecList == null || validMeshSpecList.size() == 0) {
				bGood = false;
				dialogOptions = new String[]{optionClose};
				errorMessage = SolverDescription.Chombo.getShortDisplayLabel()
						+ " solver does not currently allow geometry domain sizes with arbitrary aspect ratios. "
						+ "The domain size for this geometry has aspect ratios that are proportional to : "
						+ currentAR[0] + (dim > 1 ? " : " + currentAR[1] : "")
						+ (dim > 2 ? " : " + currentAR[2] : "")
						+ " which is incompatible with this solver.\n"
						+ "Try creating a new geometry (in a new application/model), "
						+ "with domain sizes proportional to a valid aspect ratio, for example, "
						+ bestRecommendedNx[0] + (dim > 1 ? " : " + bestRecommendedNx[1] : "")
						+ (dim > 2 ? " : " + bestRecommendedNx[2] : "")
						+ ", i.e. SizeX=" + bestRecommendedNx[0] + "\u00b7d and SizeY=" + bestRecommendedNx[1] + "\u00b7d" 
						+	(dim > 2 ? " and SizeZ=" + bestRecommendedNx[2] + "\u00b7d" : "") + " where d is an arbitrary scale factor.\n";
				if (recommendedNxList != null && recommendedNxList.size() > 0) {
					errorMessage += "For other mesh size suggestions, click \"" + optionSuggestions + "\" below.";
					dialogOptions = new String[] {optionClose, optionSuggestions};
				}
			}
			return bGood;
		}
		
		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
		public String[] getDialogOptions() {
			return dialogOptions;
		}
		public String getErrorMessage() {
			return errorMessage;
		}
	}

	public static class ChomboMeshSpec {
		public int[] Nx;
		public double H;

		public ChomboMeshSpec(int[] n, double h) {
			super();
			Nx = n;
			H = h;
		}

		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}

		public String getFormattedH() {
			return String.format("%20.5e", H);
		}
	}

	private static final int[] NxFactors = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 24, 25, 27, 28,
			30, 32, 35, 36, 40, 42, 45, 48, 50, 54, 56, 60, 63, 64, 70, 72, 75, 80, 81, 84, 90, 96, 100, 105, 108, 112, 120,
			125, 126, 128, 135, 140, 144, 150, 160, 162, 168, 175, 180, 189, 192, 200, 210, 216, 224, 225, 240, 243, 250, 252,
			256, 270, 280, 288, 300, 315, 320, 324, 336, 350, 360, 375, 378, 384, 400, 405, 420, 432, 448, 450, 480, 486,
			500 };

	public ChomboMeshValidator(Geometry geometry, ChomboSolverSpec chomboSolverSpec) {
		this.dim = geometry.getDimension();
		this.extent = geometry.getExtent();
		this.blockFactor = chomboSolverSpec.getBlockFactor();
	}

	public ChomboMeshValidator(int dim, Extent extent, int blockFactor) {
		this.dim = dim;
		this.extent = extent;
		this.blockFactor = blockFactor;
	}

	/**
	 * sort x, y, z as indexes based on extent
	 * 
	 * @param extentValues
	 * @return
	 */
	private int[] sortDir(double[] extentValues) {
		int[] orderIndexes = new int[] { 0, 1, 2 };

		if (extentValues[0] > extentValues[1]) {
			orderIndexes[0] = 1;
			orderIndexes[1] = 0;
		}
		if (dim == 3) {
			if (extentValues[2] < extentValues[orderIndexes[0]]) {
				orderIndexes[2] = orderIndexes[1];
				orderIndexes[1] = orderIndexes[0];
				orderIndexes[0] = 2;
			} else if (extentValues[2] < extentValues[orderIndexes[1]]) {
				orderIndexes[2] = orderIndexes[1];
				orderIndexes[1] = 2;
			}
		}
		return orderIndexes;
	}

	public ChomboMeshRecommendation computeMeshSpecs() {
		ChomboMeshRecommendation chomboMeshRecommendation = new ChomboMeshRecommendation(dim);
		
		double[] extentValues = new double[] { extent.getX(), extent.getY(), extent.getZ() };
		int[] orderedIndexes = sortDir(extentValues);

		ChomboMeshARGenerator chomboMeshARGenerator = new ChomboMeshARGenerator();
		chomboMeshARGenerator.findMyAR(dim, extent);
		
		List<DomainARInfo> listDomainARInfo = chomboMeshARGenerator.getMyARList();
		
		if (chomboMeshARGenerator.isValidAR())
		{
			// should only be one in the list
			DomainARInfo validDomainARInfo = listDomainARInfo.get(0);
			chomboMeshRecommendation.validMeshSpecList = new ArrayList<>();

			int[] coarseNx = new int[dim];
			for (int d = 0; d < dim; ++d) {
				coarseNx[d] = blockFactor * validDomainARInfo.minNx[d];
			}

			long MaxNAllowed = (long)5.0E+7; // limit max number of mesh points
			for (int nextNx : NxFactors) {
				int[] Nx = new int[dim];
				int totalN = 1; // compute total number of points
				for (int d = 0; d < dim; ++d) {
					int dir = orderedIndexes[d];
					Nx[dir] = nextNx * coarseNx[d];
					totalN = totalN * Nx[d];
				}
				if (totalN < MaxNAllowed) {
					double H = extent.getX() / Nx[0];
					chomboMeshRecommendation.validMeshSpecList.add(new ChomboMeshSpec(Nx, H));
				}
			}
		}
		else
		{
			chomboMeshRecommendation.recommendedNxList = new ArrayList<>();
			for (int i = 0; i < listDomainARInfo.size(); ++i) {
					int[] recommendedNx = new int[dim];
					for (int d = 0; d < dim; ++d) {
						recommendedNx[orderedIndexes[d]] = listDomainARInfo.get(i).minNx[d];
					}

					chomboMeshRecommendation.recommendedNxList.add(recommendedNx);
			}
			Collections.sort(chomboMeshRecommendation.recommendedNxList, new Comparator<int[]>() {

				@Override
				public int compare(int[] o1, int[] o2) {
					if (o1[0] < o2[0]) {
						return -1;
					}
					if (o1[0] > o2[0]) {
						return 1;
					}

					if (dim > 1) {
						if (o1[1] < o2[1]) {
							return -1;
						}
						if (o1[1] > o2[1]) {
							return 1;
						}

						if (dim > 2) {
							if (o1[2] < o2[2]) {
								return -1;
							}
							if (o1[2] > o2[2]) {
								return 1;
							}
						}
					}
					return 0;
				}
			});
			
			chomboMeshRecommendation.bestRecommendedNx = new int[dim];
			for (int d = 0; d < dim; ++d) {
				chomboMeshRecommendation.bestRecommendedNx[orderedIndexes[d]] = listDomainARInfo.get(chomboMeshARGenerator.getClosestIndex()).minNx[d];
			}
			
			// compute AR again in original order
			double minExtent = extentValues[orderedIndexes[0]];
			chomboMeshRecommendation.currentAR = new double[dim];
			chomboMeshRecommendation.currentAR[0] = extent.getX() / minExtent;
			if (dim > 1) {
				chomboMeshRecommendation.currentAR[1] = extent.getY() / minExtent;
				if (dim > 2) {
					chomboMeshRecommendation.currentAR[2] = extent.getZ() / minExtent;
				}
			}
		}
		
		return chomboMeshRecommendation;
	}

	public static void main(String[] args) {
		int blockFactor = 4;
//		Extent[] extents2D = new Extent[] { new Extent(1, 1, 1), new Extent(2, 3, 1), new Extent(5.00001, 2.5, 1) };
//		for (Extent extent : extents2D) {
//			ChomboMeshValidator validator = new ChomboMeshValidator(2, extent, blockFactor);
//			ChomboMeshRecommendation chomboMeshRecommendation = validator.computeMeshSpecs();
//			System.out.println("==Test Case 2D==" + validator);
//			for (ChomboMeshSpec ms : chomboMeshRecommendation.validMeshSpecList) {
//				System.out.println(ms);
//			}
//		}
		Extent[] extents3D = new Extent[] { new Extent(158, 34, 4) };
		for (Extent extent : extents3D) {
			ChomboMeshValidator validator = new ChomboMeshValidator(3, extent, blockFactor);
			ChomboMeshRecommendation chomboMeshRecommendation = validator.computeMeshSpecs();
			System.out.println("==Test Case 3D==" + validator);
			System.out.println(chomboMeshRecommendation);
		}
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
