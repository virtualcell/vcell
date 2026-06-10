package org.vcell.sbml;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.vcell.SBMLImporter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies SBML import reads non-ASCII attribute values byte-for-byte from a
 * UTF-8 source. The two reaction-name patterns chosen here (en-dash U+2013 and
 * Greek mu U+03BC) are common in scientific notation and would mojibake under
 * the previous {@code Charset.defaultCharset()} read on a non-UTF-8 JVM.
 */
@Tag("Fast")
public class SBMLImportCharsetTest {

    private static class CapturingVCLogger extends VCLogger {
        @Override public boolean hasMessages() { return false; }
        @Override public void sendAllMessages() { }
        @Override public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {
            if (p == Priority.HighPriority) {
                throw new VCLoggerException(p + " " + et + ": " + message);
            }
        }
    }

    private static final String SBML_WITH_NON_ASCII_BIOLOGY =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<sbml xmlns=\"http://www.sbml.org/sbml/level2/version4\" level=\"2\" version=\"4\">\n" +
            "  <model id=\"charsetTestModel\">\n" +
            "    <listOfCompartments>\n" +
            "      <compartment id=\"c1\" size=\"1.0\"/>\n" +
            "    </listOfCompartments>\n" +
            "    <listOfSpecies>\n" +
            "      <species id=\"s1\" name=\"μ-prot\" compartment=\"c1\" initialConcentration=\"1.0\"/>\n" +
            "    </listOfSpecies>\n" +
            "    <listOfReactions>\n" +
            "      <reaction id=\"r1\" name=\"k_14–3–3\">\n" +
            "        <listOfProducts>\n" +
            "          <speciesReference species=\"s1\"/>\n" +
            "        </listOfProducts>\n" +
            "        <kineticLaw>\n" +
            "          <math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
            "            <cn>1.0</cn>\n" +
            "          </math>\n" +
            "        </kineticLaw>\n" +
            "      </reaction>\n" +
            "    </listOfReactions>\n" +
            "  </model>\n" +
            "</sbml>\n";

    private static final String SBML_WITH_NON_ASCII_TITLE = """
    <?xml version='1.0' encoding='UTF-8' standalone='no'?>
    <sbml xmlns="http://www.sbml.org/sbml/level3/version2/core" level="3" version="2">
      <notes>
        <body xmlns="http://www.w3.org/1999/xhtml">
         <p>Exported by VCell 8.0.0.03</p>
      </body>
      </notes>
      <model areaUnits="Unit_dm2" extentUnits="Unit_umol" id="QuickNSTest2_☢_Application0" name="QuickNSTest2_☢_Application0" lengthUnits="Unit_dm" substanceUnits="Unit_umol" timeUnits="Unit_s" volumeUnits="Unit_l">
        <listOfUnitDefinitions>
          <unitDefinition id="Unit_umol">
            <listOfUnits>
              <unit exponent="1" kind="mole" multiplier="1" scale="-6"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_l">
            <listOfUnits>
              <unit exponent="1" kind="litre" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_dm2">
            <listOfUnits>
              <unit exponent="2" kind="metre" multiplier="1" scale="-1"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_dm">
            <listOfUnits>
              <unit exponent="1" kind="metre" multiplier="1" scale="-1"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_s">
            <listOfUnits>
              <unit exponent="1" kind="second" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_K">
            <listOfUnits>
              <unit exponent="1" kind="kelvin" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit__1">
            <listOfUnits>
              <unit exponent="1" kind="dimensionless" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_C_mol_1">
            <listOfUnits>
              <unit exponent="1" kind="coulomb" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="mole" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_C_nmol_1">
            <listOfUnits>
              <unit exponent="1" kind="coulomb" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="mole" multiplier="1" scale="-9"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_molecules_pmol_1">
            <listOfUnits>
              <unit exponent="1" kind="item" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="mole" multiplier="1" scale="-12"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit__1000000000">
            <listOfUnits>
              <unit exponent="1" kind="dimensionless" multiplier="1000000000" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_mV_C_K_1_mol_1">
            <listOfUnits>
              <unit exponent="1" kind="volt" multiplier="1" scale="-3"/>
              <unit exponent="1" kind="coulomb" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="kelvin" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="mole" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_mV_V_1">
            <listOfUnits>
              <unit exponent="1" kind="dimensionless" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_uM_um3_molecules_1">
            <listOfUnits>
              <unit exponent="1" kind="mole" multiplier="1" scale="-6"/>
              <unit exponent="-1" kind="litre" multiplier="1" scale="0"/>
              <unit exponent="3" kind="metre" multiplier="1" scale="-6"/>
              <unit exponent="-1" kind="item" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_s_1">
            <listOfUnits>
              <unit exponent="-1" kind="second" multiplier="1" scale="0"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_l_s_1_umol_1">
            <listOfUnits>
              <unit exponent="1" kind="litre" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="second" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="mole" multiplier="1" scale="-6"/>
            </listOfUnits>
          </unitDefinition>
          <unitDefinition id="Unit_l2_s_1_umol_2">
            <listOfUnits>
              <unit exponent="2" kind="litre" multiplier="1" scale="0"/>
              <unit exponent="-1" kind="second" multiplier="1" scale="0"/>
              <unit exponent="-2" kind="mole" multiplier="1" scale="-6"/>
            </listOfUnits>
          </unitDefinition>
        </listOfUnitDefinitions>
        <listOfCompartments>
          <compartment constant="true" id="c0" name="c0" size="5E-11" spatialDimensions="3" units="Unit_l"/>
        </listOfCompartments>
        <listOfSpecies>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s0" initialConcentration="100" name="s0" substanceUnits="Unit_umol"/>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s1" initialConcentration="0" name="s1" substanceUnits="Unit_umol"/>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s2" initialConcentration="0" name="s2" substanceUnits="Unit_umol"/>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s3" initialConcentration="100" name="s3" substanceUnits="Unit_umol"/>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s4" initialConcentration="0" name="s4" substanceUnits="Unit_umol"/>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s5" initialConcentration="0" name="s5" substanceUnits="Unit_umol"/>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s6" initialConcentration="100" name="s6" substanceUnits="Unit_umol"/>
          <species boundaryCondition="false" compartment="c0" constant="false" hasOnlySubstanceUnits="false" id="s7" initialConcentration="0" name="s7" substanceUnits="Unit_umol"/>
        </listOfSpecies>
        <listOfParameters>
          <parameter constant="true" id="_F_" units="Unit_C_mol_1" value="9.64853321E4"/>
          <parameter constant="true" id="Kf_r0" units="Unit_s_1" value="2"/>
          <parameter constant="true" id="Kr_r0" units="Unit_l_s_1_umol_1" value="1"/>
          <parameter constant="true" id="Kf_r1" units="Unit_s_1" value="3"/>
          <parameter constant="true" id="Kr_r1" units="Unit_l_s_1_umol_1" value="1"/>
          <parameter constant="true" id="Kf_r2" units="Unit_l2_s_1_umol_2" value="2"/>
          <parameter constant="true" id="Kr_r2" units="Unit_s_1" value="5"/>
        </listOfParameters>
        <listOfReactions>
          <reaction compartment="c0" id="r0" name="r0" reversible="true">
            <listOfReactants>
              <speciesReference constant="true" id="r0_s0r" species="s0" stoichiometry="1"/>
            </listOfReactants>
            <listOfProducts>
              <speciesReference constant="true" id="r0_s1p" species="s1" stoichiometry="1"/>
              <speciesReference constant="true" id="r0_s2p" species="s2" stoichiometry="1"/>
            </listOfProducts>
            <kineticLaw>
              <math xmlns="http://www.w3.org/1998/Math/MathML">         \s
                <apply>
                  <times/>
                  <apply>
                    <plus/>
                    <apply>
                      <times/>
                      <ci> Kf_r0 </ci>
                      <ci> s0 </ci>
                    </apply>
                    <apply>
                      <minus/>
                      <apply>
                        <times/>
                        <apply>
                          <times/>
                          <ci> Kr_r0 </ci>
                          <ci> s1 </ci>
                        </apply>
                        <ci> s2 </ci>
                      </apply>
                    </apply>
                  </apply>
                  <ci> c0 </ci>
                </apply>
              </math>
                    </kineticLaw>
          </reaction>
          <reaction compartment="c0" id="r1" name="r1" reversible="true">
            <listOfReactants>
              <speciesReference constant="true" id="r1_s3r" species="s3" stoichiometry="1"/>
            </listOfReactants>
            <listOfProducts>
              <speciesReference constant="true" id="r1_s4p" species="s4" stoichiometry="1"/>
              <speciesReference constant="true" id="r1_s5p" species="s5" stoichiometry="1"/>
            </listOfProducts>
            <kineticLaw>
              <math xmlns="http://www.w3.org/1998/Math/MathML">         \s
                <apply>
                  <times/>
                  <apply>
                    <plus/>
                    <apply>
                      <times/>
                      <ci> Kf_r1 </ci>
                      <ci> s3 </ci>
                    </apply>
                    <apply>
                      <minus/>
                      <apply>
                        <times/>
                        <apply>
                          <times/>
                          <ci> Kr_r1 </ci>
                          <ci> s4 </ci>
                        </apply>
                        <ci> s5 </ci>
                      </apply>
                    </apply>
                  </apply>
                  <ci> c0 </ci>
                </apply>
              </math>
                    </kineticLaw>
          </reaction>
          <reaction compartment="c0" id="r2" name="r2" reversible="true">
            <listOfReactants>
              <speciesReference constant="true" id="r2_s6r" species="s6" stoichiometry="1"/>
              <speciesReference constant="true" id="r2_s1r" species="s1" stoichiometry="1"/>
              <speciesReference constant="true" id="r2_s4r" species="s4" stoichiometry="1"/>
            </listOfReactants>
            <listOfProducts>
              <speciesReference constant="true" id="r2_s7p" species="s7" stoichiometry="1"/>
            </listOfProducts>
            <kineticLaw>
              <math xmlns="http://www.w3.org/1998/Math/MathML">         \s
                <apply>
                  <times/>
                  <apply>
                    <plus/>
                    <apply>
                      <times/>
                      <apply>
                        <times/>
                        <apply>
                          <times/>
                          <ci> Kf_r2 </ci>
                          <ci> s6 </ci>
                        </apply>
                        <ci> s1 </ci>
                      </apply>
                      <ci> s4 </ci>
                    </apply>
                    <apply>
                      <minus/>
                      <apply>
                        <times/>
                        <ci> Kr_r2 </ci>
                        <ci> s7 </ci>
                      </apply>
                    </apply>
                  </apply>
                  <ci> c0 </ci>
                </apply>
              </math>
                    </kineticLaw>
          </reaction>
        </listOfReactions>
      </model>
    </sbml>
    """.strip();

    @Test
    public void importUtf8Title() throws Exception {
        Path tmp = Files.createTempFile("vcell-charset-test-", ".xml");
        try {
            Files.write(tmp, SBML_WITH_NON_ASCII_TITLE.getBytes(StandardCharsets.UTF_8));

            SBMLImporter importer = new SBMLImporter(tmp.toAbsolutePath().toString(), new CapturingVCLogger(), false);
            BioModel bioModel = importer.getBioModel();
            assertNotNull(bioModel);

            assertEquals("QuickNSTest2_☢_Application0", bioModel.getName(),
                    "Name of sim should contain `\\u2622`");
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void importsUtf8ReactionAndSpeciesNames() throws Exception {
        Path tmp = Files.createTempFile("vcell-charset-test-", ".xml");
        try {
            Files.write(tmp, SBML_WITH_NON_ASCII_BIOLOGY.getBytes(StandardCharsets.UTF_8));

            SBMLImporter importer = new SBMLImporter(tmp.toAbsolutePath().toString(), new CapturingVCLogger(), false);
            BioModel bioModel = importer.getBioModel();
            assertNotNull(bioModel);

            ReactionStep r1 = null;
            for (ReactionStep rs : bioModel.getModel().getReactionSteps()) {
                if ("r1".equals(rs.getName())) { r1 = rs; break; }
            }
            assertNotNull(r1, "expected reaction with id 'r1' in imported model");
            assertEquals("k_14–3–3", r1.getSbmlName(),
                    "reaction sbmlName must preserve U+2013 EN DASH characters");

            SpeciesContext s1 = null;
            for (SpeciesContext sc : bioModel.getModel().getSpeciesContexts()) {
                if ("s1".equals(sc.getName())) { s1 = sc; break; }
            }
            assertNotNull(s1, "expected species with id 's1' in imported model");
            assertEquals("μ-prot", s1.getSbmlName(),
                    "species sbmlName must preserve U+03BC GREEK SMALL LETTER MU");
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void inputStreamPathPreservesUtf8() throws Exception {
        try (java.io.ByteArrayInputStream in =
                     new java.io.ByteArrayInputStream(SBML_WITH_NON_ASCII_BIOLOGY.getBytes(StandardCharsets.UTF_8))) {
            SBMLImporter importer = new SBMLImporter(in, new CapturingVCLogger(), false);
            BioModel bioModel = importer.getBioModel();
            assertNotNull(bioModel);

            ReactionStep r1 = null;
            for (ReactionStep rs : bioModel.getModel().getReactionSteps()) {
                if ("r1".equals(rs.getName())) { r1 = rs; break; }
            }
            assertNotNull(r1);
            assertEquals("k_14–3–3", r1.getSbmlName());
        }
    }
}
