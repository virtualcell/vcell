package org.vcell.sbml;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sbml.OmexPythonUtils.OmexValidationError;
import org.vcell.sbml.OmexPythonUtils.OmexValidationErrorType;
import org.vcell.sbml.OmexPythonUtils.OmexValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The validation errors have to reach {@code getMessage()}.
 * <p>
 * They used to be assembled only in {@code toString()}, which meant JUnit, surefire and any caller
 * logging {@code e.getMessage()} all reported nothing: a nightly regression failure appeared in CI
 * as a bare {@code » OmexValidation} for ten consecutive nights, with the actual SED-ML errors
 * recoverable only by downloading the surefire XML.
 */
@Tag("Fast")
public class OmexValidationExceptionTest {

	private static final String XPATH_ERROR =
			"XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='Src_plasmamembrane']` "
					+ "does not match any elements of model `full_model`";

	@Test
	public void messageCarriesEveryError() {
		OmexValidationException e = new OmexValidationException(List.of(
				new OmexValidationError(OmexValidationErrorType.OMEX_PARSE_ERROR, XPATH_ERROR),
				new OmexValidationError(OmexValidationErrorType.OMEX_VALIDATION_ERROR, "second problem")));

		String message = e.getMessage();
		assertNotNull(message, "getMessage() must not be null - CI reports it");
		assertTrue(message.contains(XPATH_ERROR), message);
		assertTrue(message.contains("second problem"), message);
		assertTrue(message.contains("OMEX_PARSE_ERROR"), "error type should be identifiable: " + message);
		assertTrue(message.contains("2 error(s)"), "should say how many: " + message);
	}

	/** Stack traces and log lines render the exception via toString(), which derives from the message. */
	@Test
	public void toStringCarriesTheErrorsToo() {
		OmexValidationException e = new OmexValidationException(List.of(
				new OmexValidationError(OmexValidationErrorType.OMEX_PARSE_ERROR, XPATH_ERROR)));
		assertTrue(e.toString().contains(XPATH_ERROR), e.toString());
	}

	/** Callers branch on the error types, so the list stays available alongside the message. */
	@Test
	public void errorsRemainAccessible() {
		OmexValidationError error =
				new OmexValidationError(OmexValidationErrorType.OMEX_PARSE_ERROR, XPATH_ERROR);
		OmexValidationException e = new OmexValidationException(List.of(error));
		assertTrue(e.errors.stream().anyMatch(err -> err.type == OmexValidationErrorType.OMEX_PARSE_ERROR));
	}

	@Test
	public void emptyErrorListStillProducesAMessage() {
		assertNotNull(new OmexValidationException(List.of()).getMessage());
	}
}
