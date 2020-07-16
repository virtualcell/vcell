package jscl.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.List;

public class EngineFactory implements ScriptEngineFactory {
	public String getEngineName() {
		return "JSCL Interface";
	}

	public String getEngineVersion() {
		return "1.0";
	}

	public List<String> getExtensions() {
		return Arrays.asList("txt", "TXT");
	}

	public String getLanguageName() {
		return "JSCL";
	}

	public String getLanguageVersion() {
		return "2.4";
	}

	public String getMethodCallSyntax(final String obj, final String m, final String... args) {
		return null;
	}

	public List<String> getMimeTypes() {
		return Arrays.asList("text/plain");
	}

	public List<String> getNames() {
		return Arrays.asList("jscl");
	}

	public String getOutputStatement(final String toDisplay) {
		return null;
	}

	public Object getParameter(final String key) {
		switch (key) {
		case ScriptEngine.ENGINE:
			return getEngineName();
		case ScriptEngine.ENGINE_VERSION:
			return getEngineVersion();
		case ScriptEngine.LANGUAGE:
			return getLanguageName();
		case ScriptEngine.LANGUAGE_VERSION:
			return getLanguageVersion();
		case ScriptEngine.NAME:
			return getNames().get(0);
		}
		return null;
	}

	public String getProgram(final String... statements) {
		return null;
	}

	public ScriptEngine getScriptEngine() {
		return new Engine(this);
	}
}
