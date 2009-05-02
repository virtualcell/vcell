package org.vcell.util.gui;
/**
 * Insert the type's description here.
 * Creation date: (12/18/2006 8:58:49 AM)
 * @author: Jim Schaff
 */
public class SwingHacks {
public static void cleanup() {
    cleanupJPopupMenuGlobals();
    cleanupOthers();
}


private static void cleanupJPopupMenuGlobals() {
    try {
        javax.swing.MenuSelectionManager aMenuSelectionManager = javax.swing.MenuSelectionManager.defaultManager();
        Object anObject = SafelyGetReflectedField("javax.swing.MenuSelectionManager", "listenerList", aMenuSelectionManager);
        if (anObject != null) {
            javax.swing.event.EventListenerList anEventListenerList = (javax.swing.event.EventListenerList) anObject;
            Object[] listeners = anEventListenerList.getListenerList();

			for (int i = listeners.length - 1; i >= 0; i -= 2) {
				aMenuSelectionManager.removeChangeListener((javax.swing.event.ChangeListener) listeners[i]);
    		}
        }
    } catch (Exception e) {
       //e.printStackTrace(System.out);
    }

    try {
        javax.swing.ActionMap anActionMap = (javax.swing.ActionMap) javax.swing.UIManager.getLookAndFeelDefaults().get("PopupMenu.actionMap");
        while (anActionMap != null) {
            Object[] keys = { "press", "release" };
            boolean anyFound = false;
            for (int i = 0; i < keys.length; i++) {
                Object aKey = keys[i];
                Object aValue = anActionMap.get(aKey);
                anyFound = anyFound || aValue != null;
                anActionMap.remove(aKey);
            }
            if (!anyFound) {
                break;
            }
            anActionMap = anActionMap.getParent();
        }
    } catch (Exception e) {
       //e.printStackTrace(System.out);
    }

    SafelySetReflectedFieldToNull("javax.swing.plaf.basic.BasicPopupMenuUI", "menuKeyboardHelper", null);

    Object anObject = SafelyGetReflectedField("com.sun.java.swing.plaf.windows.WindowsPopupMenuUI",  "mnemonicListener", null);
    if (anObject != null) {
        SafelySetReflectedFieldToNull(anObject.getClass(), "repaintRoot", anObject);
    }
}


private static void cleanupOthers() {
	try {
	    SafelySetReflectedFieldToNull("com.sun.java.swing.plaf.windows.WindowsRootPaneUI$AltProcessor", "root", null);
	    SafelySetReflectedFieldToNull("com.sun.java.swing.plaf.windows.WindowsRootPaneUI$AltProcessor", "winAncestor", null);
	} catch (Exception ex) {
		//ex.printStackTrace(System.out);
	}
}


private static Object SafelyGetReflectedField(String aClassName, String aFieldName, Object anObject) {
    try {
        Class aClass = Class.forName(aClassName);
        java.lang.reflect.Field aField = aClass.getDeclaredField(aFieldName);
        aField.setAccessible(true);
        return aField.get(anObject);
    } catch (Exception e) {
        //e.printStackTrace(System.out);
        return null;
    }
}


private static void SafelySetReflectedFieldToNull(Class aClass, String aFieldName, Object anObject) {
    try {
        java.lang.reflect.Field aField = aClass.getDeclaredField(aFieldName);
        aField.setAccessible(true);
        aField.set(anObject, null);
    } catch (Exception e) {
       //e.printStackTrace(System.out);
    }
}


private static void SafelySetReflectedFieldToNull(String aClassName, String aFieldName, Object anObject) {
    try {
        Class aClass = Class.forName(aClassName);
        SafelySetReflectedFieldToNull(aClass, aFieldName, anObject);
    } catch (Exception e) {
        //e.printStackTrace(System.out);
    }
}
}