package org.vcell.sbml;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.VolVariable;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.sbml.vcell.SBMLSymbolMapping;
import org.vcell.test.SBML_IT;

import javax.xml.stream.XMLStreamException;
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
@Tag("SBML_IT")
public class SBMLTestSuiteTest {
	private Integer testCase;

	public SBMLTestSuiteTest(Integer testCase) {
		this.testCase = testCase;
	}

	@BeforeClass
	public static void before() {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
		Logger.getLogger(SBMLExporter.class).addAppender(new ConsoleAppender());
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

		faults.put(970, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1064, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1065, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1066, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1067, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1068, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1069, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1070, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1071, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1072, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1073, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1074, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1075, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1076, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1077, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1078, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1079, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1080, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1081, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1082, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1087, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1088, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1089, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1090, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1091, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1092, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1093, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1094, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1095, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1096, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1097, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1098, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1099, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1100, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1101, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1102, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1105, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1106, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1110, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);
		faults.put(1111, FAULT.STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3);

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

		// Runtime Comparison Failures
		faults.put(59, FAULT.LOCAL_SHADOWS_GLOBAL); // TODO: id of the local parameter S1 shadows the species S1
		faults.put(134, FAULT.LOCAL_SHADOWS_GLOBAL); // TODO: id of the local parameter S1 shadows the species S1
		faults.put(170, FAULT.NONCONSTANT_PARAM_NOT_IN_OUTPUT); // TODO: doesn't export 'constant=false' parameter S2 to results (it is a constant parameter in vcell)
		faults.put(172, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch
		faults.put(396, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch - ignores rate rule
		faults.put(397, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch - ignores rate rule
		faults.put(398, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch - ignores rate rule
		faults.put(402, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch - ignores rate rule
		faults.put(403, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch - ignores rate rule
		faults.put(404, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch - ignores rate rule
		faults.put(453, FAULT.EVENT_AND_RATE_SAME_PARAM); // TODO: rate rules and event on same parameter - mismatch - ignores rate rule
		faults.put(454, FAULT.EVENT_AND_RATE_SAME_PARAM_AND_DELAY); // TODO: rate rules and event on same parameter (and event delay) - mismatch - ignores rate rule
		faults.put(455, FAULT.EVENT_AND_RATE_SAME_PARAM_AND_DELAY); // TODO: rate rules and event on same parameter (and event delay) - mismatch - ignores rate rule
		faults.put(456, FAULT.EVENT_DELAY); // TODO: events with delay - mismatch
		faults.put(457, FAULT.EVENT_DELAY); // TODO: events with delay - mismatch
		faults.put(458, FAULT.EVENT_DELAY); // TODO: events with delay - mismatch
		faults.put(459, FAULT.EVENT_DELAY); // TODO: events with delay - mismatch
		faults.put(460, FAULT.EVENT_DELAY); // TODO: events with delay - mismatch
		faults.put(461, FAULT.EVENT_DELAY); // TODO: events with delay - mismatch
		faults.put(471, FAULT.NONCONSTANT_COMPARTMENT); // TODO: initial assignment with expression for compartment size
		faults.put(525, FAULT.NONCONSTANT_COMPARTMENT); // TODO: initial assignment with expression for compartment size
		faults.put(597, FAULT.LOCAL_SHADOWS_GLOBAL); // TODO: id of the local parameter S1 shadows the species S1
		faults.put(849, FAULT.EVENT_DELAY); // TODO: events with delay - mismatch
		faults.put(879, FAULT.TIME_IN_INITIAL_ASSIGNMENT); // TODO: time in initial assignment - mismatch
		faults.put(928, FAULT.EVENT_TRIGGER_AT_ZERO); // TODO: event trigger at zero - mismatch
		faults.put(929, FAULT.EVENT_TRIGGER_AT_ZERO); // TODO: event trigger at zero - mismatch
		faults.put(931, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - mismatch
		faults.put(932, FAULT.EVENT_PERSISTENT_TRIGGER); // TODO: events with persistent trigger - mismatch
		faults.put(934, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - mismatch
		faults.put(952, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch
		faults.put(953, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch
		faults.put(962, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch
		faults.put(963, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch
		faults.put(964, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch
		faults.put(965, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch
		faults.put(966, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch (also Java Heap Space)
		faults.put(967, FAULT.EVENT_PRIORITY_ORDERING); // TODO: multiple events ordered by priority - same priority - crazy test case - mismatch (also Java Heap Space)
		faults.put(969, FAULT.STOCH_INIT_ASSIGN_AND_VALUE); // TODO: initial assigment should override stoichiometry
		faults.put(971, FAULT.STOCH_INIT_ASSIGN_AND_VALUE); // TODO: initial assigment should override stoichiometry
		faults.put(975, FAULT.MODEL_CONVERSION_FACTOR); // TODO: implement or reject as not supported - model conversion factor ???
		faults.put(976, FAULT.MODEL_CONVERSION_FACTOR); // TODO: implement or reject as not supported - model conversion factor ???
		faults.put(977, FAULT.SPECIES_CONVERSION_FACTOR); // TODO: implement or reject as not supported - model conversion factor ???
		faults.put(978, FAULT.EVENT_CORNER_CASES); // TODO: Several events conspire within the same time step to trigger three events multiple times, with different outcomes.
		faults.put(995, FAULT.EVENT_INITIAL_VALUE); // TODO: Several events conspire within the same time step to trigger three events multiple times, with different outcomes.
		faults.put(996, FAULT.EVENT_INITIAL_VALUE); // TODO: Several events conspire within the same time step to trigger three events multiple times, with different outcomes.
		faults.put(997, FAULT.EVENT_INITIAL_VALUE); // TODO: Several events conspire within the same time step to trigger three events multiple times, with different outcomes.
		faults.put(998, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1001, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1002, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1003, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1004, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1005, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1006, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1007, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1008, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1009, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1010, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1014, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1015, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1016, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1017, FAULT.HAS_SUBSTANCE_ONLY); // TODO: look into has substance only - mismatch
		faults.put(1049, FAULT.EVENT_DELAY); // TODO: event delay - mismatch
		faults.put(1104, FAULT.NONCONSTANT_STOICH); // TODO: reject non-constant stoichiometry - mismatch
		faults.put(1107, FAULT.NONCONSTANT_STOICH); // TODO: reject non-constant stoichiometry - mismatch
		faults.put(1109, FAULT.NONCONSTANT_STOICH); // TODO: reject non-constant stoichiometry - mismatch
		faults.put(1119, FAULT.EVENT_DELAY); // TODO: events with delay - <<SOLVER FAILED>>
		faults.put(1184, FAULT.PARAM_INIT_ASSIGN_AND_VALUE); // TODO: vcell says y is a constant parameter - failed to compare - not sure which value vcell uses
		faults.put(1185, FAULT.PARAM_INIT_ASSIGN_AND_VALUE); // TODO: vcell says y is a constant parameter - failed to compare - not sure which value vcell uses
		return Arrays.stream(SbmlTestSuiteFiles.getSbmlTestSuiteCases()).boxed().filter(t -> !faults.containsKey(t)).collect(Collectors.toList());
	}

	public enum FAULT {
		// Runtime Comparison Failures
		LOCAL_SHADOWS_GLOBAL,
		NONCONSTANT_PARAM_NOT_IN_OUTPUT,
		EVENT_AND_RATE_SAME_PARAM,
		EVENT_AND_RATE_SAME_PARAM_AND_DELAY,
		EVENT_DELAY,
		TIME_IN_INITIAL_ASSIGNMENT,
		EVENT_TRIGGER_AT_ZERO,
		EVENT_PRIORITY_ORDERING,
		EVENT_PERSISTENT_TRIGGER,
		STOCH_INIT_ASSIGN_AND_VALUE,
		MODEL_CONVERSION_FACTOR, // What is this?
		SPECIES_CONVERSION_FACTOR, // What is this?
		EVENT_CORNER_CASES,
		EVENT_INITIAL_VALUE,
		HAS_SUBSTANCE_ONLY,
		NONCONSTANT_STOICH,
		PARAM_INIT_ASSIGN_AND_VALUE,


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
		NONNUMERIC_STOICIOMETRY_MATH,
		STOICHIOMETRY_MISSING_OR_RULE_TARGET_LEVEL3,
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
			SBMLSymbolMapping sbmlSymbolMapping = importer.getSymbolMapping();
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
			SBMLResults computedResults = solver.simulate(workingDir.toFile(), sim, simSpec, simContext, sbmlSymbolMapping);
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

	@Test
	public void roundTripVerify() throws XMLStreamException, SbmlException, VCLoggerException, MappingException {
		System.out.println("testCase "+testCase);
		InputStream testFileInputStream = SbmlTestSuiteFiles.getSbmlTestCase(testCase);
		boolean bValidateSBML = true;
		TLogger vcl = new TLogger();
		SBMLImporter importer = new SBMLImporter(testFileInputStream, vcl, bValidateSBML);
		BioModel bioModel = importer.getBioModel();
		bioModel.updateAll(false);
//		{
//			boolean bRoundTripValidation = false;
//			SBMLExporter sbmlExporter = new SBMLExporter(bioModel.getSimulationContext(0), 3, 1, bRoundTripValidation);
//			String sbmlString = sbmlExporter.getSBMLString();
//			System.out.println(sbmlString);
//		}
		{
			boolean bRoundTripValidation = true;
			SBMLExporter sbmlExporter = new SBMLExporter(bioModel.getSimulationContext(0), 3, 1, bRoundTripValidation);
			sbmlExporter.getSBMLString();
		}
	}
}
