package cbit.vcell.solvers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * command string. Encapsulates information about the command
 * and changes Strings to unix style paths
 * @author gweatherby
 *
 */
public class ExecutableCommand {
	
	public static class LibraryPath {
		public final String path;
		public LibraryPath(String path){
			this.path = path;
		}
	}
	
	private final LibraryPath ld_library_path;
	private final boolean messaging; 
	private final boolean parallel; 
	private List<String> cmds;
	private String exitCodeToken;
	
	/**
	 * cmds as single string
	 */
	private String joined;
	
	/**
	 * is {@link #joined} up to date?
	 */
	private boolean dirty;
	
	
	/**
	 * @param messaging
	 * @param parallel
	 * @param input variable list of Strings to pass
	 */
	public ExecutableCommand(LibraryPath ld_library_path, boolean messaging, boolean parallel, String ... input) {
		super();
		this.ld_library_path = ld_library_path;
		this.messaging = messaging;
		this.parallel = parallel;
		this.cmds = new ArrayList<>(); 
		this.exitCodeToken = null;
		this.joined = null;
		this.dirty = true;
		for (String filePath : input) {
			 //cmds.add(filePath.replace("C:","").replace("D:","").replace("\\","/"));
			 cmds.add(filePath.replace("\\","/"));
		}
	}
	
	/**
	 * @param messaging
	 * @param parallel
	 * @param input variable list of Strings to pass
	 */
	public ExecutableCommand(LibraryPath ld_library_path, boolean messaging, boolean parallel, List<String> input) {
		super();
		this.ld_library_path = ld_library_path;
		this.messaging = messaging;
		this.parallel = parallel;
		this.exitCodeToken = null;
		this.joined = null;
		this.dirty = true;
		List<String> cleaned = input.stream().map( (p) -> p.replace("\\", "/") ).collect(Collectors.toList());
		this.cmds = new ArrayList<>(cleaned); 
	}

	/**
	 * default to messaging, not parallel
	 * @param input
	 */
	public ExecutableCommand(LibraryPath ld_library_path, String ... input) {
		this(ld_library_path,true,false,input);
	}
	
	public List<String> getCommands( ) {
		return cmds;
	}

	public boolean isParallel() {
		return parallel;
	}
	
	public boolean isMessaging() {
		return messaging;
	}
	
	/**
	 * indicate whether this has an exit code token
	 * @return true if it does
	 */
	public boolean hasExitCodeSubstitution( ) {
		return exitCodeToken != null;
	}
	
	public String getJoinedCommands( )  {
		if (dirty) {
			if (hasExitCodeSubstitution()) {
				throw new IllegalStateException("exit code string required");
			}
			joined = "";
			for (String c : cmds) {
				joined += c;
				joined += " ";
			}
			dirty = false;
		}
		return joined; 
	}
	
	/**
	 * get joined command, substituting for exitToken
	 * @param exitCodeString
	 * @return single String
	 * @throws IllegalStateException if {@link #setExitCodeToken(String)} was not called
	 */
	public String getJoinedCommands(String exitCodeString)  {
		if (!hasExitCodeSubstitution()) {
			throw new IllegalStateException("no exit code string provided");
		}
		joined = "";
		for (String c : cmds) {
			if (!c.equals(exitCodeToken)) {
				joined += c;
			}
			else {
				joined += exitCodeString;
			}
			joined += " ";
		}
		dirty = false;
		return joined; 
	}
	

	public LibraryPath getLdLibraryPath() {
		return ld_library_path;
	}

	/**
	 * add argument to existing args
	 * @param arg
	 */
	public void addArgument(String arg) {
		cmds.add(unixfy(arg));
		dirty = true;
	}
	
	/**
	 * add argument to existing args
	 * @param arg
	 */
	public void addArgument(int arg) {
		cmds.add(Integer.toString(arg));
		dirty = true;
	}
	
	/**
	 * set token representing part of command to be replaced by exit code of previous process * @param token
	 * @param token new token
	 */
	public void setExitCodeToken(String token) {
		exitCodeToken = token;
		dirty = true;
	}
	
	/***
	 * change path to unix style 
	 */
	private String unixfy(String filePath) {
		 return filePath.replace("C:","").replace("D:","").replace("\\","/");
	}
	
	/**
	 * container for set of {@link ExecutableCommand} 
	 * ensures only one command has exit status code 
	 */
	public static class Container {
		private List<ExecutableCommand> execCommands;
		private ExecutableCommand exitCodeCommand;
		public Container( ) {
			execCommands = new ArrayList<>(4);
			exitCodeCommand = null;
		}
		
		
		public List<ExecutableCommand> getExecCommands() {
			return execCommands;
		}

		public boolean hasExitCodeCommand() {
			return exitCodeCommand != null;
		}
		public ExecutableCommand getExitCodeCommand() {
			return exitCodeCommand;
		}


		/**
		 * add command. Only one command in container may have {@link ExecutableCommand#hasExitCodeSubstitution()} true
		 * @param ec
		 * @throws UnsupportedOperationException 
		 */
		public void add(ExecutableCommand ec) {
			if (!ec.hasExitCodeSubstitution()) {
				execCommands.add(ec);
				return;
			}
			if (exitCodeCommand == null) {
				exitCodeCommand = ec;
				return;
			}
			throw new UnsupportedOperationException("only single exit substation command supported");
		}
	}


}
