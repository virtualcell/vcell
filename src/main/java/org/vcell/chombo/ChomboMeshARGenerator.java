package org.vcell.chombo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.vcell.chombo.ChomboMeshValidator.DomainARInfo;
import org.vcell.util.Extent;

import com.google.gson.Gson;

public class ChomboMeshARGenerator {
	private final static int[][] NyNxList = { { 0, 1 }, { 1, 16 }, { 1, 15 }, { 1, 14 }, { 1, 13 }, { 1, 12 }, { 1, 11 },
			{ 1, 10 }, { 1, 9 }, { 1, 8 }, { 2, 15 }, { 1, 7 }, { 2, 13 }, { 1, 6 }, { 2, 11 }, { 3, 16 }, { 1, 5 },
			{ 3, 14 }, { 2, 9 }, { 3, 13 }, { 1, 4 }, { 4, 15 }, { 3, 11 }, { 2, 7 }, { 3, 10 }, { 4, 13 }, { 5, 16 },
			{ 1, 3 }, { 5, 14 }, { 4, 11 }, { 3, 8 }, { 5, 13 }, { 2, 5 }, { 5, 12 }, { 3, 7 }, { 7, 16 }, { 4, 9 },
			{ 5, 11 }, { 6, 13 }, { 7, 15 }, { 1, 2 }, { 8, 15 }, { 7, 13 }, { 6, 11 }, { 5, 9 }, { 9, 16 }, { 4, 7 },
			{ 7, 12 }, { 3, 5 }, { 8, 13 }, { 5, 8 }, { 7, 11 }, { 9, 14 }, { 2, 3 }, { 11, 16 }, { 9, 13 }, { 7, 10 },
			{ 5, 7 }, { 8, 11 }, { 11, 15 }, { 3, 4 }, { 10, 13 }, { 7, 9 }, { 11, 14 }, { 4, 5 }, { 13, 16 }, { 9, 11 },
			{ 5, 6 }, { 11, 13 }, { 6, 7 }, { 13, 15 }, { 7, 8 }, { 8, 9 }, { 9, 10 }, { 10, 11 }, { 11, 12 }, { 12, 13 },
			{ 13, 14 }, { 14, 15 }, { 15, 16 }, { 1, 1 }, };

	private static final double aspRatTol = 1.e-4;;
	private boolean isValidAR = false;
	private List<DomainARInfo> myARList = new ArrayList<>();
	private int closestIndex = -1;

	private double computeAR(int[] arr) {
		return arr[0] * 1.0 / arr[1];
	}

	public void findMyAR(final int dim, Extent extent) {
//		System.out.println("extent:" + new Gson().toJson(extent));
		double Ex = extent.getX();
		double Ey = extent.getY();
		double Ez = extent.getZ();
		double[] sortedExtent = Ex <= Ey ? new double[] { Ex, Ey } : new double[] { Ey, Ex };
		if (dim == 3) {
			sortedExtent = new double[] { Ex, Ey, Ez };
			Arrays.sort(sortedExtent);
		}
		double[] ARvector = new double[dim];
		for (int d = 0; d < dim; ++d) {
			ARvector[d] = sortedExtent[d] / sortedExtent[0];
		}

		int m2 = (int) Math.floor(ARvector[1]);
		double AR2 = ARvector[1] - m2;
		int m3 = 0;
		double AR3 = 0;

		List<Integer> indexList2d = new ArrayList<>();
		isValidAR = findMyAR_2d(AR2, m2, indexList2d);
//		System.out.println("indexList2d");
//		for (int i : indexList2d) {
//			System.out.println(i);
//		}

		if (dim == 3 && ARvector[2] != ARvector[1]) {
			// 3d
			m3 = (int) Math.floor(ARvector[2]);
			AR3 = ARvector[2] - m3;
			List<Integer> indexList3d = new ArrayList<>();
			isValidAR &= findMyAR_2d(AR3, m3, indexList3d);
//			System.out.println("indexList3d");
//			for (int i : indexList3d) {
//				System.out.println(i);
//			}
			makeList_3D(isValidAR, indexList2d, indexList3d, m2, m3);
		} else {
			for (int idx : indexList2d) {
				int newNmax = m2 * NyNxList[idx][1] + NyNxList[idx][0];
				
				double[] ar = new double[dim - 1];
				ar[0] = m2 + computeAR(NyNxList[idx]);
				
				int[] minNx = new int[dim];
				minNx[0] = NyNxList[idx][1];
				minNx[1] = newNmax;
				if (dim == 3)
				{
					// duplicate 2D numbers to make it 3D
					ar[1] = ar[0];
					minNx[2] = newNmax;
				}
				DomainARInfo arInfo = new DomainARInfo(ar, minNx);
				myARList.add(arInfo);
			}
		}
		// sort AR list
		if (isValidAR) {
			// only one, no need to sort
		} else {
			Collections.sort(myARList);
			double minDist = Double.MAX_VALUE;
			for (int i = 0; i < myARList.size(); ++i) {
				DomainARInfo arInfo = myARList.get(i);
				double disti = dim == 2 ? Math.abs(arInfo.ar[0] - m2 - AR2)
						: Math.max(Math.abs(arInfo.ar[0] - m2 - AR2), Math.abs(arInfo.ar[1] - m3 - AR3));
				if (disti < minDist) {
					minDist = disti;
					closestIndex = i;
				}
			}
		}
//		System.out.println("isValidAR:" + isValidAR);
//		System.out.println("closestIndex:" + closestIndex);
//		System.out.println("myARList:");
//		for (DomainARInfo arInfo : myARList) {
//			System.out.println(arInfo);
//		}
	}

	private static int gcf(int a, int b) {
		int min = a;
		int max = b;
		if (a > b) {
			min = b;
			max = a;
		}
		while (true) {
			int c = max % min;
			if (c == 0) {
				return min;
			}
			max = min;
			min = c;
		}
	}

	private static int lcm(int a, int b) {
		return a * b / gcf(a, b);
	}

	private void makeList_3D(boolean isValidAR, List<Integer> indexListY, List<Integer> indexListZ, int my, int mz) {
		if (isValidAR) {
			int iz = indexListZ.get(0);
			int iy = indexListY.get(0);
			double ar_iy = computeAR(NyNxList[iy]);
			double ar_iz = computeAR(NyNxList[iz]);

			int Nmin_z = NyNxList[iz][1];
			int Nmin_y = NyNxList[iy][1];
			int newNmin = lcm(Nmin_y, Nmin_z);
			int ay = newNmin / Nmin_y;
			int az = newNmin / Nmin_z;
			int newnNy = newNmin * my + ay * NyNxList[iy][0];
			int newnNz = newNmin * mz + az * NyNxList[iz][0];

			DomainARInfo arInfo = new DomainARInfo(new double[] { my + ar_iy, mz + ar_iz },
					new int[] { newNmin, newnNy, newnNz });
			myARList.add(arInfo);
			return;
		}

		List<int[]> ar_z_list = new ArrayList<>();
		for (int idx : indexListZ) {
			ar_z_list.add(NyNxList[idx]);
		}
		if (indexListZ.size() > 0 && !isValidAR) {
			if (indexListZ.get(0) != 0) {
				ar_z_list.add(0, new int[] { 1, mz });
			}
			ar_z_list.add(new int[] { 1, mz + 1 });
		}

		List<int[]> ar_y_list = new ArrayList<>();
		for (int idx : indexListY) {
			ar_y_list.add(NyNxList[idx]);
		}
		if (indexListY.size() > 0 && !isValidAR) {
			if (indexListY.get(0) != 0) {
				ar_y_list.add(0, new int[] { 1, my });
			}
			ar_y_list.add(new int[] { 1, my + 1 });
		}

		for (int[] ar_z : ar_z_list) {
			int Nmin_z = ar_z[1];
			for (int[] ar_y : ar_y_list) {
				int Nmin_y = ar_y[1];
				int newNmin = lcm(Nmin_y, Nmin_z);
				if (newNmin <= 16) {
					int ay = newNmin / Nmin_y;
					int az = newNmin / Nmin_z;
					int newnNy = newNmin * my + ay * ar_y[0];
					int newnNz = newNmin * mz + az * ar_z[0];
					// [Nmin_y Nmin_z newNmin ay az newnNy newnNz]
					if (newNmin * newnNy * newnNz <= Math.pow(2, 15)) {
						double thisar_z = newnNz * 1.0 / newNmin;
						double thisar_y = newnNy * 1.0 / newNmin;
						if (thisar_y < thisar_z) {
							DomainARInfo arInfo = new DomainARInfo(new double[] { thisar_y, thisar_z },
									new int[] { newNmin, newnNy, newnNz });
							if (!myARList.contains(arInfo)) {
								myARList.add(arInfo);
							}
						}
					}
				}
			}
		}
	}

	private boolean findMyAR_2d(double AR, int m, List<Integer> indexList) {
		for (int i = 0; i < NyNxList.length; i++) {
			double ar = computeAR(NyNxList[i]);
			double arE = AR - ar;
			if (Math.abs(arE) < aspRatTol) {
				// exact aspect ratio
				addToIndexList(indexList, i);
				return true;
			}
		}

		// did not find exact aspect ratio in the list
		int[] mv = { 16, 8, 4, 2, 1 };
		int[] Ntol = { 1, 4, 8, 16, 32 };

		if (m > mv[0]) {
			addToIndexList(indexList, 0);
		} else {
			for (int i = 0; i < mv.length - 1; ++i) {
				if ((m <= mv[i]) && (m > mv[i + 1]) || m == 1) {

					List<Integer> jj = new ArrayList<>();
					for (int a = 0; a < NyNxList.length; ++a) {
						if (NyNxList[a][1] <= Ntol[i]) {
							jj.add(a);
						}
					}

					if (jj.size() > 1) {
						for (int a = 0; a < jj.size() - 1; ++a) {

							int k = jj.get(a);
							int k_next = jj.get(a + 1);

							double ar_k = computeAR(NyNxList[k]);
							double ar_k_next = computeAR(NyNxList[k_next]);

							if (AR >= ar_k && AR < ar_k_next) {
								addToIndexList(indexList, k);
								addToIndexList(indexList, k_next);
							}
						}
					} else if (jj.size() > 0) {
						addToIndexList(indexList, jj.get(0));
					}
				}
			}
		}

		addToIndexList(indexList, 0); // always add the first row
		addToIndexList(indexList, NyNxList.length - 1); // always add the last one
		Collections.sort(indexList);
		return false;
	}

	private void addToIndexList(List<Integer> indexList, int i) {
		if (!indexList.contains(i)) {
			indexList.add(i);
		}
	}

	public static void main(String[] args) {
		ChomboMeshARGenerator generator = new ChomboMeshARGenerator();
		Extent extent = new Extent(1.175,  7.333, 2);
		generator.findMyAR(2, extent);
	}

	public boolean isValidAR() {
		return isValidAR;
	}

	public List<DomainARInfo> getMyARList() {
		return myARList;
	}

	public int getClosestIndex() {
		return closestIndex;
	}
}
