package cbit.vcell.simdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * class to package arbitrary {@link PortableCommand} into JSON
 * @author gweatherby
 */
public class PortableCommandWrapper {
	
	private String classname;
	/**
	 * command to package. Transient so {@link Gson} doesn't include in top level JSON 
	 */
	private transient PortableCommand command;
	/**
	 * token embedded in submit files for postprocessor command end
	 */
	public static final String POSTPROCESSOR_CMD_END_TOKEN = "Embedded Postprocessor Command End";
	/**
	 * token embedded in submit files for postprocessor command start
	 */
	public static final String POSTPROCESSOR_CMD_TOKEN = "Embedded Postprocessor Command Begin";
	/**
	 * symbol used to indicate comment line in script
	 */
	public static final char SCRIPT_COMMENT = '#' ;
	
	private static Logger lg = LogManager.getLogger(PortableCommandWrapper.class);
	
	/**
	 * create empty wrapper. {@link #setCommand(PortableCommand)} must be called
	 * before use 
	 */
	public PortableCommandWrapper() {
		classname = null;
		command = null;
	}
	
	/**
	 * create from an object implementing {@link PortableCommand} interface. 
	 * May not be nested class.
	 * @param obj may not be null
	 */
	public PortableCommandWrapper(PortableCommand obj) {
		setCommand(obj);
	}
	
	/**
	 * set new object implementing {@link PortableCommand} interface. 
	 * May not be nested class.
	 * @param obj may not be null
	 */
	//final because called from constructor
	public final void setCommand(PortableCommand obj) {
		if (obj != null) {
			classname = obj.getClass().getCanonicalName();
			command = obj;
			return;
		}
		throw new IllegalArgumentException("Null object passed");
	}
	
	/**
	 * get JSON representation of this, including packaged command
	 * @return String
	 * @throws IllegalStateException if command not set
	 */
	public String asJson( ) {
		if (command != null) {
			Gson gson = new Gson();
			String s = gson.toJson(this);
			String sub = gson.toJson(command);
			JPair jp = new JPair(s,sub);
			return gson.toJson(jp);
		}
		throw new IllegalStateException("command not set");
	}
	
	/**
	 * @return the command. Should not be null.
	 */
	public PortableCommand getCommand( ) {
		return command;
	}
	
	/**
	 * parse filename for embedded portable commands
	 * @param filename
	 * @return collection, may be empty
	 */
	public static Collection<PortableCommand> getCommands(String filename)  {
		Collection<PortableCommand> rval = null; 
		try {
			String startOfCommand = Character.toString(SCRIPT_COMMENT)  + POSTPROCESSOR_CMD_TOKEN;
			String endOfCommand = Character.toString(SCRIPT_COMMENT)  + POSTPROCESSOR_CMD_END_TOKEN;
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while (br.ready()) {
				String line = br.readLine();
				if (line.startsWith(startOfCommand)) {
					PortableCommand pc = readCommand(br,sb,endOfCommand);
					if (rval == null) {
						rval = new ArrayList<PortableCommand>();
					}
					rval.add(pc);
				}
			}
		} catch (Exception e) {
			if (lg.isWarnEnabled()) {
				lg.warn("Error parsing " + filename + " for PortableCommands", e);
			}
		}
		if (rval != null) {
			return rval;
		}
		return Collections.emptySet();
	}

	/**
	 * read single command from reader
	 * @param br source
	 * @param sb working buffer
	 * @param terminator end of command symbol
	 * @return new object
	 * @throws IOException
	 */
	private static PortableCommand readCommand(BufferedReader br, StringBuilder sb, String terminator) throws IOException {
		sb.delete(0, sb.length());
		while (br.ready( )) {
			String content = br.readLine();
			if (!content.startsWith(terminator)) {
				if (content.charAt(0) != SCRIPT_COMMENT) {
					throw new RuntimeException("command line: '" + content + "' must start with " + SCRIPT_COMMENT);
				}
				sb.append(content.substring(1)); //skip SCRIPT_COMMENT
				continue;
			}
			return PortableCommandWrapper.fromJson(sb.toString());
		}
		throw new RuntimeException("end of command terminator " + terminator + " not found");
	}

	/**
	 * insert commands into StringBuilder 
	 * @param sb destination
	 * @param commands 
	 * @throws IllegalArgumentException if either argument null
	 */
	public static void insertCommands(StringBuilder sb, Collection<PortableCommand> commands) {
		if (sb == null || commands == null) {
			throw new IllegalArgumentException("StringWriter " + sb + "or commands " + commands + " null");
		}
		if (!commands.isEmpty()) {
			PortableCommandWrapper wrapper = new PortableCommandWrapper( );
			//PrintWriter pw = new PrintWriter(sb);
			for (PortableCommand cmd: commands) {
				final char NEWLINE = '\n';
				wrapper.setCommand(cmd);
				sb.append(SCRIPT_COMMENT);
				sb.append(POSTPROCESSOR_CMD_TOKEN);
				sb.append(NEWLINE);
				String json = wrapper.asJson();
				//don't assume json single line -- 
				StringTokenizer tokenizer = new StringTokenizer(json, "\n", true);
				while (tokenizer.hasMoreTokens()) {
					sb.append(SCRIPT_COMMENT);
					sb.append(tokenizer.nextToken());
					sb.append(NEWLINE);
				}
				sb.append(SCRIPT_COMMENT);
				sb.append(POSTPROCESSOR_CMD_END_TOKEN);
				sb.append(NEWLINE);
			}
		}
	}

	/**
	 * convert name to class, deal with exception
	 * @param name
	 * @return Class<?>
	 * @throws RuntimeException
	 */
	private static Class<?> forName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Can't rebuild stored class" + name,e);
		}
	}
	
	/**
	 * Convert String into {@link PortableCommand}
	 * @param json String containing stored object
	 * @return new object
	 */
	public static PortableCommand fromJson(String json) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
		PortableCommandWrapper.JPair jp = gson.fromJson(json,PortableCommandWrapper.JPair.class);
		PortableCommandWrapper vs = gson.fromJson(jp.packageString, PortableCommandWrapper.class);
		Class<?> clzz = forName(vs.classname);
		PortableCommand pc = (PortableCommand) gson.fromJson(jp.commandString, clzz);
		return pc;
	}
	
	/**
	 * internal class to package Strings together 
	 */
	private static class JPair {
		String packageString;
		String commandString;
		public JPair(String shell, String command) {
			this.packageString = shell;
			this.commandString = command;
		}
	}
}
