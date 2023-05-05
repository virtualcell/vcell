package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.test.SBML_IT;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Category(SBML_IT.class)
@RunWith(Parameterized.class)
public class SBMLTestSuiteTest {
	private Integer testCase;

	public SBMLTestSuiteTest(Integer testCase) {
		this.testCase = testCase;
	}

	@BeforeClass
	public static void before() {
		System.setProperty(PropertyLoader.installationRoot,"..");
	}

	@Parameterized.Parameters
	public static Collection<Integer> testCases() {
		HashMap<Integer,FAULT> faults = new HashMap();
		faults.put(22, FAULT.NONINTEGER_STOICH);
		faults.put(45, FAULT.COMPARTMENT_1D);
		faults.put(39, FAULT.ALGEBRAIC_RULES);
		faults.put(40, FAULT.ALGEBRAIC_RULES);
		faults.put(46, FAULT.COMPARTMENT_1D);
		faults.put(47, FAULT.COMPARTMENT_1D);
		faults.put(48, FAULT.COMPARTMENT_0D);
		faults.put(49, FAULT.COMPARTMENT_0D);
		faults.put(50, FAULT.COMPARTMENT_0D);
		faults.put(51, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(52, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(53, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(96, FAULT.COMPARTMENT_1D);
		faults.put(97, FAULT.COMPARTMENT_0D);
		faults.put(99, FAULT.COMPARTMENT_1D);
		faults.put(100, FAULT.COMPARTMENT_0D);
		faults.put(102, FAULT.COMPARTMENT_1D);
		faults.put(103, FAULT.COMPARTMENT_0D);
		faults.put(104, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(105, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(106, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);

		faults.put(68, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(69, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(70, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(129, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(130, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(131, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(388, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(391, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(394, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(445, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(448, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(451, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(516, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(517, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(518, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(519, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(520, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(521, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(609, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(610, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(725, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(726, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(727, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(728, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(729, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(730, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(731, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(827, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(828, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(829, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(898, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(899, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(900, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(973, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(989, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(990, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(991, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(992, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(994, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(1027, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(1028, FAULT.NONNUMERIC_STOICIOMETRY_MATH);
		faults.put(1029, FAULT.NONNUMERIC_STOICIOMETRY_MATH);

		faults.put(139, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(140, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(141, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(142, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(143, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(144, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(310, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(311, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(312, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(313, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(314, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(315, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(316, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(317, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(318, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(480, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(481, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(482, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(483, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(484, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(485, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(492, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(493, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(494, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(495, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(496, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(497, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(781, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(782, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(783, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(784, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(785, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(786, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(794, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(795, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(796, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(946, FAULT.NONCONSTANT_COMPARTMENT);
		faults.put(948, FAULT.NONCONSTANT_COMPARTMENT);

		faults.put(153, FAULT.COMPARTMENT_1D);
		faults.put(154, FAULT.COMPARTMENT_1D);
		faults.put(155, FAULT.COMPARTMENT_0D);
		faults.put(156, FAULT.COMPARTMENT_0D);
		faults.put(182, FAULT.ALGEBRAIC_RULES);
		faults.put(184, FAULT.ALGEBRAIC_RULES);
		faults.put(201, FAULT.XOR_MISSING);
		faults.put(205, FAULT.COMPARTMENT_DIM_MISSING);
		faults.put(206, FAULT.COMPARTMENT_1D);
		faults.put(207, FAULT.COMPARTMENT_1D);
		faults.put(223, FAULT.COMPARTMENT_1D);
		faults.put(224, FAULT.COMPARTMENT_1D);
		faults.put(225, FAULT.COMPARTMENT_1D);
		faults.put(226, FAULT.COMPARTMENT_1D);
		faults.put(227, FAULT.COMPARTMENT_1D);
		faults.put(228, FAULT.COMPARTMENT_1D);
		faults.put(229, FAULT.COMPARTMENT_1D);
		faults.put(230, FAULT.COMPARTMENT_1D);
		faults.put(231, FAULT.COMPARTMENT_1D);
		faults.put(232, FAULT.COMPARTMENT_1D);
		faults.put(233, FAULT.COMPARTMENT_1D);
		faults.put(234, FAULT.COMPARTMENT_1D);
		faults.put(235, FAULT.COMPARTMENT_1D);
		faults.put(236, FAULT.COMPARTMENT_1D);
		faults.put(237, FAULT.COMPARTMENT_1D);
		faults.put(238, FAULT.COMPARTMENT_0D);
		faults.put(239, FAULT.COMPARTMENT_0D);
		faults.put(240, FAULT.COMPARTMENT_0D);
		faults.put(241, FAULT.COMPARTMENT_0D);
		faults.put(242, FAULT.COMPARTMENT_0D);
		faults.put(243, FAULT.COMPARTMENT_0D);
		faults.put(244, FAULT.COMPARTMENT_0D);
		faults.put(245, FAULT.COMPARTMENT_0D);
		faults.put(246, FAULT.COMPARTMENT_0D);
		faults.put(247, FAULT.COMPARTMENT_0D);
		faults.put(254, FAULT.COMPARTMENT_1D);
		faults.put(255, FAULT.COMPARTMENT_1D);
		faults.put(256, FAULT.COMPARTMENT_1D);
		faults.put(257, FAULT.COMPARTMENT_0D);
		faults.put(258, FAULT.COMPARTMENT_0D);
		faults.put(259, FAULT.COMPARTMENT_0D);
		faults.put(261, FAULT.COMPARTMENT_1D);
		faults.put(262, FAULT.COMPARTMENT_0D);
		faults.put(264, FAULT.COMPARTMENT_1D);
		faults.put(265, FAULT.COMPARTMENT_0D);
		faults.put(267, FAULT.COMPARTMENT_1D);
		faults.put(268, FAULT.COMPARTMENT_0D);
		faults.put(279, FAULT.XOR_MISSING);
		faults.put(281, FAULT.COMPARTMENT_1D);
		faults.put(283, FAULT.COMPARTMENT_1D);
		faults.put(285, FAULT.COMPARTMENT_1D);
		faults.put(320, FAULT.COMPARTMENT_1D);
		faults.put(321, FAULT.COMPARTMENT_0D);
		faults.put(323, FAULT.COMPARTMENT_1D);
		faults.put(324, FAULT.COMPARTMENT_0D);
		faults.put(326, FAULT.COMPARTMENT_1D);
		faults.put(327, FAULT.COMPARTMENT_0D);
		faults.put(361, FAULT.COMPARTMENT_1D);
		faults.put(362, FAULT.COMPARTMENT_0D);
		faults.put(364, FAULT.COMPARTMENT_1D);
		faults.put(365, FAULT.COMPARTMENT_0D);
		faults.put(367, FAULT.COMPARTMENT_1D);
		faults.put(368, FAULT.COMPARTMENT_0D);
		faults.put(418, FAULT.COMPARTMENT_1D);
		faults.put(419, FAULT.COMPARTMENT_0D);
		faults.put(421, FAULT.COMPARTMENT_1D);
		faults.put(422, FAULT.COMPARTMENT_0D);
		faults.put(424, FAULT.COMPARTMENT_1D);
		faults.put(425, FAULT.COMPARTMENT_0D);
		faults.put(486, FAULT.COMPARTMENT_1D);
		faults.put(487, FAULT.COMPARTMENT_1D);
		faults.put(488, FAULT.COMPARTMENT_1D);
		faults.put(489, FAULT.COMPARTMENT_0D);
		faults.put(490, FAULT.COMPARTMENT_0D);
		faults.put(491, FAULT.COMPARTMENT_0D);
		faults.put(531, FAULT.ALGEBRAIC_RULES);
		faults.put(532, FAULT.ALGEBRAIC_RULES);
		faults.put(533, FAULT.ALGEBRAIC_RULES);
		faults.put(534, FAULT.ALGEBRAIC_RULES);
		faults.put(535, FAULT.ALGEBRAIC_RULES);
		faults.put(536, FAULT.ALGEBRAIC_RULES);
		faults.put(537, FAULT.ALGEBRAIC_RULES);
		faults.put(538, FAULT.ALGEBRAIC_RULES);
		faults.put(539, FAULT.ALGEBRAIC_RULES);
		faults.put(540, FAULT.ALGEBRAIC_RULES);
		faults.put(541, FAULT.ALGEBRAIC_RULES);
		faults.put(542, FAULT.ALGEBRAIC_RULES);
		faults.put(543, FAULT.COMPARTMENT_0D);
		faults.put(544, FAULT.ALGEBRAIC_RULES);
		faults.put(545, FAULT.ALGEBRAIC_RULES);
		faults.put(546, FAULT.COMPARTMENT_0D);
		faults.put(547, FAULT.ALGEBRAIC_RULES);
		faults.put(548, FAULT.ALGEBRAIC_RULES);
		faults.put(549, FAULT.ALGEBRAIC_RULES);
		faults.put(550, FAULT.ALGEBRAIC_RULES);
		faults.put(551, FAULT.ALGEBRAIC_RULES);
		faults.put(552, FAULT.ALGEBRAIC_RULES);
		faults.put(553, FAULT.ALGEBRAIC_RULES);
		faults.put(554, FAULT.ALGEBRAIC_RULES);
		faults.put(555, FAULT.ALGEBRAIC_RULES);
		faults.put(556, FAULT.ALGEBRAIC_RULES);
		faults.put(557, FAULT.ALGEBRAIC_RULES);
		faults.put(558, FAULT.ALGEBRAIC_RULES);
		faults.put(559, FAULT.ALGEBRAIC_RULES);
		faults.put(560, FAULT.ALGEBRAIC_RULES);
		faults.put(561, FAULT.ALGEBRAIC_RULES);
		faults.put(562, FAULT.ALGEBRAIC_RULES);
		faults.put(563, FAULT.ALGEBRAIC_RULES);
		faults.put(564, FAULT.ALGEBRAIC_RULES);
		faults.put(565, FAULT.ALGEBRAIC_RULES);
		faults.put(566, FAULT.ALGEBRAIC_RULES);
		faults.put(567, FAULT.ALGEBRAIC_RULES);
		faults.put(568, FAULT.ALGEBRAIC_RULES);
		faults.put(569, FAULT.ALGEBRAIC_RULES);
		faults.put(570, FAULT.ALGEBRAIC_RULES);
		faults.put(571, FAULT.ALGEBRAIC_RULES);
		faults.put(572, FAULT.ALGEBRAIC_RULES);
		faults.put(573, FAULT.ALGEBRAIC_RULES);
		faults.put(574, FAULT.ALGEBRAIC_RULES);
		faults.put(575, FAULT.ALGEBRAIC_RULES);
		faults.put(576, FAULT.ALGEBRAIC_RULES);
		faults.put(592, FAULT.COMPARTMENT_1D);
		faults.put(593, FAULT.COMPARTMENT_1D);
		faults.put(594, FAULT.COMPARTMENT_1D);
		faults.put(613, FAULT.ALGEBRAIC_RULES);
		faults.put(614, FAULT.ALGEBRAIC_RULES);
		faults.put(615, FAULT.ALGEBRAIC_RULES);
		faults.put(628, FAULT.ALGEBRAIC_RULES);
		faults.put(629, FAULT.ALGEBRAIC_RULES);
		faults.put(630, FAULT.ALGEBRAIC_RULES);
		faults.put(658, FAULT.ALGEBRAIC_RULES);
		faults.put(659, FAULT.ALGEBRAIC_RULES);
		faults.put(660, FAULT.ALGEBRAIC_RULES);
		faults.put(661, FAULT.ALGEBRAIC_RULES);
		faults.put(662, FAULT.ALGEBRAIC_RULES);
		faults.put(663, FAULT.ALGEBRAIC_RULES);
		faults.put(664, FAULT.ALGEBRAIC_RULES);
		faults.put(665, FAULT.ALGEBRAIC_RULES);
		faults.put(666, FAULT.ALGEBRAIC_RULES);
		faults.put(673, FAULT.ALGEBRAIC_RULES);
		faults.put(674, FAULT.ALGEBRAIC_RULES);
		faults.put(675, FAULT.ALGEBRAIC_RULES);
		faults.put(687, FAULT.ALGEBRAIC_RULES);
		faults.put(695, FAULT.ALGEBRAIC_RULES);
		faults.put(696, FAULT.ALGEBRAIC_RULES);
		faults.put(705, FAULT.ALGEBRAIC_RULES);
		faults.put(760, FAULT.ALGEBRAIC_RULES);
		faults.put(761, FAULT.ALGEBRAIC_RULES);
		faults.put(762, FAULT.ALGEBRAIC_RULES);
		faults.put(777, FAULT.ALGEBRAIC_RULES);
		faults.put(778, FAULT.ALGEBRAIC_RULES);
		faults.put(779, FAULT.ALGEBRAIC_RULES);
		faults.put(780, FAULT.ALGEBRAIC_RULES);
		faults.put(844, FAULT.ALGEBRAIC_RULES);
		faults.put(876, FAULT.ALGEBRAIC_RULES);
		faults.put(901, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(902, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(903, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(904, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(905, FAULT.COMPARTMENT_1D);
		faults.put(906, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(907, FAULT.COMPARTMENT_1D);
		faults.put(908, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(909, FAULT.COMPARTMENT_1D);
		faults.put(910, FAULT.COMPARTMENT_1D);
		faults.put(911, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(912, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(913, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(914, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(915, FAULT.COMPARTMENT_1D);
		faults.put(916, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(917, FAULT.COMPARTMENT_1D);
		faults.put(918, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(919, FAULT.COMPARTMENT_1D);
		faults.put(926, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(927, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(937, FAULT.DELAY);
		faults.put(938, FAULT.DELAY);
		faults.put(939, FAULT.DELAY);
		faults.put(940, FAULT.DELAY);
		faults.put(941, FAULT.DELAY);
		faults.put(942, FAULT.DELAY);
		faults.put(943, FAULT.DELAY);
		faults.put(945, FAULT.STRUCTURE_SIZE_IN_ASSIGNMENT_RULE);
		faults.put(947, FAULT.STRUCTURE_SIZE_IN_ASSIGNMENT_RULE);
		faults.put(950, FAULT.VALUE_NAN_INF_OR_MISSING);
		faults.put(951, FAULT.VALUE_NAN_INF_OR_MISSING);
		faults.put(957, FAULT.XOR_MISSING);
		faults.put(958, FAULT.XOR_MISSING);
		faults.put(959, FAULT.INCONSISTENT_UNIT_SYSTEM);
		faults.put(960, FAULT.AVOGADRO);
		faults.put(961, FAULT.AVOGADRO);
		faults.put(972, FAULT.STOICHIOMETRY_IN_EXPRESSION);
		faults.put(974, FAULT.RUNTIME_ERROR); // dummy stoichiometry STE used in expression in rate rule - not supported.
		faults.put(981, FAULT.DELAY);
		faults.put(982, FAULT.DELAY);
		faults.put(983, FAULT.DELAY);
		faults.put(984, FAULT.DELAY);
		faults.put(985, FAULT.DELAY);
		faults.put(987, FAULT.FAST_SYSTEM_INCOMPATIBILITY);
		faults.put(988, FAULT.FAST_SYSTEM_INCOMPATIBILITY);
		faults.put(993, FAULT.ALGEBRAIC_RULES);
		faults.put(999, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(1000, FAULT.AVOGADRO);
		faults.put(1011, FAULT.COMPARTMENT_1D);
		faults.put(1012, FAULT.COMPARTMENT_1D);
		faults.put(1013, FAULT.COMPARTMENT_1D);
		faults.put(1117, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(1118, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(1120, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(1122, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(1123, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(1044, FAULT.ALGEBRAIC_RULES);
		faults.put(1051, FAULT.JSBML_ERROR); // The order of subelements within <reaction> ... with id 'slowerReaction1' does not comply.
		faults.put(1052, FAULT.JSBML_ERROR); // The order of subelements within <reaction> ... with id 'slowerReaction1' does not comply.
		faults.put(1053, FAULT.JSBML_ERROR); // The order of subelements within <reaction> ... with id 'slowerReaction1' does not comply.
		faults.put(1054, FAULT.ALGEBRAIC_RULES);
		faults.put(1083, FAULT.ALGEBRAIC_RULES);
		faults.put(1084, FAULT.ALGEBRAIC_RULES);
		faults.put(1085, FAULT.ALGEBRAIC_RULES);
		faults.put(1086, FAULT.ALGEBRAIC_RULES);
		faults.put(1198, FAULT.STRUCTURE_SIZE_IN_RATE_RULE);
		faults.put(1103, FAULT.OVERDETERMINED_SYSTEM);
		faults.put(1108, FAULT.ALGEBRAIC_RULES);
		faults.put(1112, FAULT.XOR_MISSING);
		faults.put(1113, FAULT.XOR_MISSING);
		faults.put(1114, FAULT.XOR_MISSING);
		faults.put(1115, FAULT.XOR_MISSING);
		faults.put(1121, FAULT.AVOGADRO);
		faults.put(1124, FAULT.COMP_PACKAGE);
		faults.put(1125, FAULT.COMP_PACKAGE);
		faults.put(1126, FAULT.COMP_PACKAGE);
		faults.put(1127, FAULT.COMP_PACKAGE);
		faults.put(1128, FAULT.COMP_PACKAGE);
		faults.put(1129, FAULT.COMP_PACKAGE);
		faults.put(1130, FAULT.COMP_PACKAGE);
		faults.put(1131, FAULT.COMP_PACKAGE);
		faults.put(1132, FAULT.COMP_PACKAGE);
		faults.put(1133, FAULT.COMP_PACKAGE);
		faults.put(1134, FAULT.COMP_PACKAGE);
		faults.put(1135, FAULT.COMP_PACKAGE);
		faults.put(1136, FAULT.COMP_PACKAGE);
		faults.put(1137, FAULT.COMP_PACKAGE);
		faults.put(1138, FAULT.COMP_PACKAGE);
		faults.put(1139, FAULT.COMP_PACKAGE);
		faults.put(1140, FAULT.COMP_PACKAGE);
		faults.put(1141, FAULT.COMP_PACKAGE);
		faults.put(1142, FAULT.COMP_PACKAGE);
		faults.put(1143, FAULT.COMP_PACKAGE);
		faults.put(1144, FAULT.COMP_PACKAGE);
		faults.put(1145, FAULT.COMP_PACKAGE);
		faults.put(1146, FAULT.COMP_PACKAGE);
		faults.put(1147, FAULT.COMP_PACKAGE);
		faults.put(1148, FAULT.COMP_PACKAGE);
		faults.put(1149, FAULT.COMP_PACKAGE);
		faults.put(1150, FAULT.COMP_PACKAGE);
		faults.put(1151, FAULT.COMP_PACKAGE);
		faults.put(1152, FAULT.COMP_PACKAGE);
		faults.put(1153, FAULT.COMP_PACKAGE);
		faults.put(1154, FAULT.COMP_PACKAGE);
		faults.put(1155, FAULT.COMP_PACKAGE);
		faults.put(1156, FAULT.COMP_PACKAGE);
		faults.put(1157, FAULT.COMP_PACKAGE);
		faults.put(1158, FAULT.COMP_PACKAGE);
		faults.put(1159, FAULT.COMP_PACKAGE);
		faults.put(1160, FAULT.COMP_PACKAGE);
		faults.put(1161, FAULT.COMP_PACKAGE);
		faults.put(1162, FAULT.COMP_PACKAGE);
		faults.put(1163, FAULT.COMP_PACKAGE);
		faults.put(1164, FAULT.COMP_PACKAGE);
		faults.put(1165, FAULT.COMP_PACKAGE);
		faults.put(1166, FAULT.COMP_PACKAGE);
		faults.put(1167, FAULT.COMP_PACKAGE);
		faults.put(1168, FAULT.COMP_PACKAGE);
		faults.put(1169, FAULT.COMP_PACKAGE);
		faults.put(1170, FAULT.COMP_PACKAGE);
		faults.put(1171, FAULT.COMP_PACKAGE);
		faults.put(1172, FAULT.COMP_PACKAGE);
		faults.put(1173, FAULT.COMP_PACKAGE);
		faults.put(1174, FAULT.COMP_PACKAGE);
		faults.put(1175, FAULT.COMP_PACKAGE);
		faults.put(1176, FAULT.COMP_PACKAGE);
		faults.put(1177, FAULT.COMP_PACKAGE);
		faults.put(1178, FAULT.COMP_PACKAGE);
		faults.put(1179, FAULT.COMP_PACKAGE);
		faults.put(1180, FAULT.COMP_PACKAGE);
		faults.put(1181, FAULT.COMP_PACKAGE);
		faults.put(1182, FAULT.COMP_PACKAGE);
		faults.put(1183, FAULT.COMP_PACKAGE);
		faults.put(1186, FAULT.FBC_PACKAGE);
		faults.put(1187, FAULT.FBC_PACKAGE);
		faults.put(1188, FAULT.FBC_PACKAGE);
		faults.put(1189, FAULT.FBC_PACKAGE);
		faults.put(1190, FAULT.FBC_PACKAGE);
		faults.put(1191, FAULT.FBC_PACKAGE);
		faults.put(1192, FAULT.FBC_PACKAGE);
		faults.put(1193, FAULT.FBC_PACKAGE);
		faults.put(1194, FAULT.FBC_PACKAGE);
		faults.put(1195, FAULT.FBC_PACKAGE);
		faults.put(1196, FAULT.FBC_PACKAGE);
		faults.put(1197, FAULT.INVALID); // compartment spatial dimension 7

		return Arrays.stream(SbmlTestSuiteFiles.getSbmlTestSuiteCases()).boxed().filter(t -> !faults.containsKey(t)).collect(Collectors.toList());
	}

	public enum FAULT {
		SBML_SYMBOL_MAPPING,
		INVALID, // invalid model (e.g. compartment spatialDimension=7)
		RUNTIME_ERROR,
		RESERVED_WORD,
		DELAY,
		AVOGADRO,
		COMP_PACKAGE,
		FBC_PACKAGE,
		QUAL_PACKAGE,
		MATHML_PARSING,
		VALUE_NAN_INF_OR_MISSING,
		OVERDETERMINED_SYSTEM,
		NONINTEGER_STOICH,
		COMPARTMENT_0D,
		COMPARTMENT_1D,
		COMPARTMENT_DIM_MISSING,
		ALGEBRAIC_RULES,
		INCONSISTENT_UNIT_SYSTEM,
		EXPRESSION_BINDING_EXCEPTION,
		XOR_MISSING,
		JSBML_ERROR,
		REACTANT_AND_MODIFIER,
		UNCATEGORIZED,
		STRUCTURE_SIZE_IN_RATE_RULE,
		STRUCTURE_SIZE_IN_ASSIGNMENT_RULE,
//		STRUCTURE_SIZE_IN_BIOEVENT,
		STOICHIOMETRY_IN_EXPRESSION,
		FAST_SYSTEM_INCOMPATIBILITY,
		NONCONSTANT_COMPARTMENT,
		NONNUMERIC_STOICIOMETRY_MATH
	};



	@Test
	public void testSbmlTestSuiteImport() throws Exception{
		HashMap<Integer,FAULT> faults = new HashMap();
		faults.put(22, FAULT.NONINTEGER_STOICH);

		TLogger vcl = new TLogger();
		InputStream testFileInputStream = SbmlTestSuiteFiles.getSbmlTestCase(testCase);
		boolean bValidateSBML = true;
		SBMLImporter importer = new SBMLImporter(testFileInputStream, vcl, bValidateSBML);
		try {
			BioModel bioModel = importer.getBioModel();
			//
			// check math generation, checks for BioModel error issues.
			//
			bioModel.updateAll(false);

			//
			// TEMPORARY - skip tests which don't have any VolVariables (e.g. no ODEs)
			// in the future, solvers should tolerate such models and generate trivial results anyway.
			//
			MathDescription mathDesc = bioModel.getSimulationContext(0).getMathDescription();
			Optional<VolVariable> volVariable = mathDesc.getVariableList().stream()
					.filter(var -> var instanceof VolVariable).map(var -> (VolVariable)var).findAny();
			if (!volVariable.isPresent()){
				System.err.println("SKIPPING SIMULATION, Math has no Variables, nothing to solve");
				return;
			}

			//
			// run simulations and compare with known results from SBML Test Suite.
			//
			SimulationContext simContext = bioModel.getSimulationContext(0);
			SBMLResults expectedCSV = SBMLResults.fromCSV(SbmlTestSuiteFiles.getSbmlTestCaseResultsAsCSV(testCase));
			SBMLSimulationSpec simSpec = SBMLSimulationSpec.fromSpecFile(
					SbmlTestSuiteFiles.getSbmlTestCaseSettingsAsText(testCase));
			Simulation sim = new Simulation(simContext.getMathDescription(), simContext);
			sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, simSpec.start + simSpec.duration));
			sim.getSolverTaskDescription().setOutputTimeSpec(new ExplicitOutputTimeSpec(expectedCSV.values.get(0)));
			sim.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-12, 1e-9));
			SBMLSolver solver = new SBMLSolver();
			Path workingDir = Files.createTempDirectory("sbml-test-suite-working-dir-");
			SBMLResults computedResults = solver.simulate(workingDir.toFile(), sim, simSpec, simContext);
			boolean bEquiv = SBMLResults.compareEquivalent(expectedCSV, computedResults, simSpec);
			if (!bEquiv){
				System.err.println(SBMLResults.toCSV(expectedCSV,computedResults));
			}
			Assert.assertTrue("testCase "+testCase+" not within tolerance", bEquiv);
			System.err.println("<< SIMULATIONS MATCH >>");
		}catch (Exception e){
			System.err.println("unexpected exception in test case "+testCase);
			throw e;
		}
		Assert.assertArrayEquals("testCase "+testCase+" failed", new String[0], vcl.highPriority.toArray());
	}
}
