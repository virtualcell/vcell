package cbit.vcell.client.task;

import java.awt.Window;
import java.util.Hashtable;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

/**
 * task to start Virtual FRAP
 */
public class LaunchVirtualFRAP extends AsynchClientTask {
	/**
	 * parent window of frame
	 */
	private final DocumentWindowManager dwm;

	LaunchVirtualFRAP(DocumentWindowManager dwm) {
		super("Start Virtual FRAP", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
		this.dwm = dwm;
	}

	/**
	 * launch Virtual Frap Frame
	 */
	@Override
	public void run(Hashtable<String, Object> hashTable) throws Exception {
		VirtualFrapLoader.loadVFRAP(null, false,dwm);
	}

	/**
	 * list of tasks needed to LaunchVirtual FRAP
	 * @param dwm
	 * @return list of tasks (launch, raise to top)
	 */
	public static AsynchClientTask[] taskList(DocumentWindowManager dwm) {
		AsynchClientTask tl[] = new AsynchClientTask[2];
		tl[0]  = new LaunchVirtualFRAP(dwm);
		tl[1]  = new RaiseVirtualFrap(); 
		return tl;
	}

	/**
	 * find {@link VirtualFrapMainFrame} window and bring it to front
	 */
	private static class RaiseVirtualFrap extends AsynchClientTask {
		public RaiseVirtualFrap() {
			super("Raise Virtual FRAP", AsynchClientTask.TASKTYPE_SWING_BLOCKING);
		}
		/**
		 * find {@link VirtualFrapMainFrame} window and bring it to front
		 */
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			for (Window w : Window.getWindows()) {
				if (w instanceof VirtualFrapMainFrame) {
					VirtualFrapMainFrame vfmf = (VirtualFrapMainFrame) w;
					vfmf.toFront();
					return;
				}
			}
		}
	}
}
