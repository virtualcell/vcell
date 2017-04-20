package cbit.vcell.client.constants;

import java.util.HashMap;
import java.util.Map;

import org.vcell.util.ProgrammingException;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;

/**
 * characteristics of action commands which create {@link SimulationContext}, 
 * whether by copying or creating 
 * @author GWeatherby
 */
public abstract class ApplicationActionCommand {
	private final String label;
	private static Map<String,ApplicationActionCommand> commands = new HashMap<>( );
	
	public enum Act {
		CREATE,
		COPY_AS_IS,
		COPY_CHANGE
	}

	//package
	ApplicationActionCommand(String label) {
		super();
		this.label = label;
		commands.put(label, this);
	}

	public String getLabel() {
		return label;
	} 

	/**
	 * @param actionCmd non null
	 * @return matching ActionCopyCommand for actionCmd
	 * @throws ProgrammingException if actionCmd not valid String instance (from {@link GuiConstants}
	 */
	public static ApplicationActionCommand lookup(String actionCmd) {
		ApplicationActionCommand acc = commands.get(actionCmd);
		if (acc != null) {
			return acc;
		}
		throw new ProgrammingException("unmapped action copy command " + actionCmd);
	}

	/**
	 * @return action type 
	 */
	public abstract Act actionType( ) ;
	
	/**
	 * for {@link Act#COPY_CHANGE} or {@link Act#CREATE}
	 * @throws UnsupportedOperationException if not copy change or create 
	 */
	public abstract SimulationContext.Application getAppType( );
	/**
	 * for copying with change
	 * @throws UnsupportedOperationException if not {@link Act#COPY_CHANGE} 
	 */
	public abstract boolean isSourceSpatial() throws UnsupportedOperationException;

	/**
	 * for copying with change
	 * @throws UnsupportedOperationException if not {@link Act#COPY_CHANGE} 
	 */
	public abstract boolean isDestSpatial()throws UnsupportedOperationException;
	
	private static abstract class Typed extends ApplicationActionCommand {

		private final SimulationContext.Application appType;

		Typed(String label, Application appType) {
			super(label);
			this.appType = appType;
		}

		@Override
		public SimulationContext.Application getAppType() {
			return appType;
		}
	}

	/**
	 * copy change command
	 */
	static class CopyChange extends Typed {
		private final boolean bSourceSpatial;
		private final boolean bDestSpatial;

		CopyChange(String label, boolean isSourceSpatial, boolean isDestSpatial, Application appType) {
			super(label, appType);
			this.bSourceSpatial = isSourceSpatial;
			this.bDestSpatial = isDestSpatial;
		}

		@Override
		public boolean isSourceSpatial() {
			return bSourceSpatial;
		}

		@Override
		public boolean isDestSpatial() {
			return bDestSpatial;
		}

		@Override
		public Act actionType() {
			return Act.COPY_CHANGE; 
		}

	}

	/**
	 * create command
	 */
	static class Create extends Typed {

		Create(String label, Application appType) {
			super(label, appType);
		}


		@Override
		public boolean isSourceSpatial() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isDestSpatial() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}


		@Override
		public Act actionType() {
			return Act.CREATE; 
		}
	}
	
	static class CopyAsIs extends ApplicationActionCommand {
		CopyAsIs(String label) {
			super(label);
		}

		@Override
		public Act actionType() {
			return Act.COPY_AS_IS; 
		}

		@Override
		public Application getAppType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isSourceSpatial() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isDestSpatial() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}
		
	}
}
