package org.vcell.util.gui;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import org.intellij.lang.annotations.MagicConstant;
import org.vcell.util.OperatingSystemInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Class dedicated to facilitating key-bind support to different VCell classes.
 * <br/>
 * Proper keybind handling code can be very finicky, so this class serves to make it as clear yet convenient as possible.
 * <br/>
 * At the time of writing, there are three known ways to track keybinds in java Swing to track keybinds
 * 1) <code>JComponent::registerKeyboardAction</code> // Note that this is deprecated, but still seemingly works.
 * 2) Using Swing's <code>InputMap</code> & <code>ActionMap</code> combo // The replacement for option #1
 * 3) Using Accelerators (useful when using JMenuItems)
 * <br/>
 * To use this class:
 * 1) Create an instance using the constructor
 * 2) Use the Action subclass to create wrapped, "asynch" actions to perform on keybind press.
 * 3) Call the desired methods of the object instance with the wrapped actions to set up the desired bindings.
 */
public class ShortcutsWizard {
	@MagicConstant(flagsFromClass = java.awt.event.InputEvent.class)
	private static final int menuKeyMaskCode = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

	private static final OperatingSystemInfo operatingSystemInfo = OperatingSystemInfo.getInstance();

	private final JComponent targetJComponent;
	private final Map<FocusCondition, InputMap> inputMapMapping;
	private final ActionMap actionMap;

	private enum KeybindEncoding {
		KEYBIND_COPY(KeyEvent.VK_C),
		KEYBIND_PASTE(KeyEvent.VK_V),
		KEYBIND_SELECT_ALL(KeyEvent.VK_A);

		public final int keyEventCode;
		KeybindEncoding(int keyEventCode){
			this.keyEventCode = keyEventCode;
		}
	}

	public enum FocusCondition {
		WHEN_FOCUSED,
		WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
		WHEN_IN_FOCUSED_WINDOW,
	}

	public class Action {
		private final AbstractAction wrappedAction;

		private Action(AsynchClientTask actionToPerform){
			this(actionToPerform, new Hashtable<>());
		}

		private Action(AsynchClientTask actionToPerform, Hashtable<String, Object> parameters){
			this.wrappedAction = new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e) {
					ClientTaskDispatcher.dispatch(ShortcutsWizard.this.targetJComponent, parameters, new AsynchClientTask[]{actionToPerform});
				}
			};
		}

		private Action(String taskName, Consumer<ActionEvent> functionToInvoke, int taskType){
			this.wrappedAction = new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e) {
					ClientTaskDispatcher.dispatch(ShortcutsWizard.this.targetJComponent, new Hashtable<>(), new AsynchClientTask[]{
							new AsynchClientTask(taskName, taskType) {
								@Override
								public void run(Hashtable<String, Object> hashTable) {
									functionToInvoke.accept(e);
								}
							}
					});
				}
			};
		}

		public AbstractAction getWrappedAction(){
			return this.wrappedAction;
		}

	}

	/**
	 * Creates an Action from a pre-constructed AsyncClientTask that doesn't need Hashtable parameters
	 * @param actionToPerform the task to perform when triggered by some key-combo.
	 * @return the <code>Action</code> contianing the task.
	 */
	public Action createAction(AsynchClientTask actionToPerform){
		return this.createAction(actionToPerform, new Hashtable<>());
	}

	/**
	 * Creates an Action from a pre-constructed AsyncClientTask and Hashtable parameters
	 * @param actionToPerform the task to perform when triggered by some key-combo.
	 * @param parameters the hashtable of parameters needed to complete the task.
	 * @return the <code>Action</code> containing the task.
	 */
	public Action createAction(AsynchClientTask actionToPerform, Hashtable<String, Object> parameters){
		return new Action(actionToPerform, parameters);
	}

	/**
	 * Creates an Action from a lambda that accepts an "Action event" as a parameter. Note that the lambda
	 * doesn't have to do anything with the action event, and can instead ignore it.
	 * @param taskName name of the task the lambda is meant to perform.
	 * @param functionToInvoke the lambda to use as a function.
	 * @param taskType type of Asynch task to create; see <code>AsynchClientTask</code> for more information.
	 * @return the <code>Action</code> containing the function.
	 */
	public Action createAction(String taskName, int taskType, Consumer<ActionEvent> functionToInvoke){
		return new Action(taskName, functionToInvoke, taskType);
	}

	/**
	 * Constructs a KeyBindsWizard AND initializes the provided JComponent to prepare for keybinds;
	 * this process will remove keybinds previously set by previous <code>KeyBindsWizard</code>s, and
	 * may even clear other keybinds depending on how they were initialized.
	 * @param componentToAddKeyBindsTo JComponent to set key binds for
	 */
	public ShortcutsWizard(JComponent componentToAddKeyBindsTo) {
		this.inputMapMapping = new EnumMap<>(ShortcutsWizard.FocusCondition.class);
		for (FocusCondition fc : FocusCondition.values()) this.inputMapMapping.put(fc, this.createInputMap(fc, componentToAddKeyBindsTo));
		componentToAddKeyBindsTo.setActionMap(this.actionMap = new ActionMap());
		componentToAddKeyBindsTo.setFocusable(true);
		this.targetJComponent = componentToAddKeyBindsTo;
	}

	private InputMap createInputMap(FocusCondition fc, JComponent componentToAddKeyBindsTo) {
		InputMap inputMap = new ComponentInputMap(componentToAddKeyBindsTo);
		switch (fc){ // could use fc.ordinal directly in a single function call, but that gives a warning.
			case WHEN_FOCUSED -> componentToAddKeyBindsTo.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
			case WHEN_ANCESTOR_OF_FOCUSED_COMPONENT -> componentToAddKeyBindsTo.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
			case WHEN_IN_FOCUSED_WINDOW -> componentToAddKeyBindsTo.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);
		}
		return inputMap;
	}

	/*   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *\
	 *                  Public Configure Methods                 *
	\*   *   *   *   *   *   *   *   *   *   *   *   *   *   *   */

	/**
	 * Allows users to use both a JMenuItem and the keyboard to perform a copy operation
	 * @param actionToTakeOnCopy the action-object to apply to a keyboard copy when the component is
	 *                           an ancestor of a focused component
	 */
	public void configureCopy(final Action actionToTakeOnCopy) {
		this.configureCopy(actionToTakeOnCopy, FocusCondition.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
	}

	/**
	 * Allows users to use both a JMenuItem and the keyboard to perform a copy operation
	 * @param actionToTakeOnCopy the action-object to apply to a keyboard copy when the component is
	 *                           an ancestor of a focused component
	 * @param typeToCreate the type of <code>AbstractButton</code> to create an instance of. Passing in `null` will return null,
	 *                     and no object will be created.
	 */
	public <T extends AbstractButton> T configureCopy(final Action actionToTakeOnCopy, Class<T> typeToCreate) {
		return this.configureCopy(actionToTakeOnCopy, FocusCondition.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, typeToCreate);
	}

	/**
	 * Allows users to use the keyboard to perform a copy operation given a focus condition
	 * @param actionToTakeOnCopy the action-object to apply to a keyboard copy
	 * @param focusCondition the degree of focus the object should have to trigger the action on copy
	 */
	public void configureCopy(final Action actionToTakeOnCopy, final FocusCondition focusCondition){
		this.configureCopy(actionToTakeOnCopy, focusCondition, null);
	}

	/**
	 * Allows users to use the keyboard to perform a copy operation given a focus condition
	 * @param actionToTakeOnCopy the action-object to apply to a keyboard copy
	 * @param focusCondition the degree of focus the object should have to trigger the action on copy
	 * @param typeToCreate the type of <code>AbstractButton</code> to create an instance of. Passing in `null` will return null,
	 *                     and no object will be created.
	 */
	public <T extends AbstractButton> T configureCopy(final Action actionToTakeOnCopy, final FocusCondition focusCondition, Class<T> typeToCreate) {
		return this.performMapping(KeybindEncoding.KEYBIND_COPY, actionToTakeOnCopy, focusCondition, typeToCreate, "Copy");
	}

	/**
	 * Allows users to use the keyboard to perform a paste operation
	 * @param actionToTakeOnPaste the action-object to apply to a keyboard paste when the component is
	 *                           an ancestor of a focused component
	 */
	public <T extends AbstractButton> T configurePaste(final Action actionToTakeOnPaste){
		return this.configurePaste(actionToTakeOnPaste, FocusCondition.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
	}

	/**
	 * Allows users to use the keyboard to perform a paste operation
	 * @param actionToTakeOnPaste the action-object to apply to a keyboard paste when the component is
	 *                           an ancestor of a focused component
	 * @param buttonType the type of <code>AbstractButton</code> to create an instance of. Passing in `null` will return null,
	 *                     and no object will be created.
	 */
	public <T extends AbstractButton> T configurePaste(final Action actionToTakeOnPaste, Class<T> buttonType){
		return this.configurePaste(actionToTakeOnPaste, FocusCondition.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, buttonType);
	}

	/**
	 * Allows users to use the keyboard to perform a paste operation
	 * @param actionToTakeOnPaste the action-object to apply to a keyboard paste
	 * @param focusCondition the degree of focus the object should have to trigger the action on paste
	 */
	public <T extends AbstractButton> T configurePaste(final Action actionToTakeOnPaste, final FocusCondition focusCondition){
		return this.performMapping(KeybindEncoding.KEYBIND_PASTE, actionToTakeOnPaste, focusCondition, null, null);
	}

	/**
	 * Allows users to use the keyboard to perform a paste operation
	 * @param actionToTakeOnPaste the action-object to apply to a keyboard paste
	 * @param focusCondition the degree of focus the object should have to trigger the action on paste
	 * @param buttonType the type of <code>AbstractButton</code> to create an instance of. Passing in `null` will return null,
	 *                     and no object will be created.
	 */
	public <T extends AbstractButton> T configurePaste(final Action actionToTakeOnPaste, final FocusCondition focusCondition, Class<T> buttonType){
		return this.performMapping(KeybindEncoding.KEYBIND_PASTE, actionToTakeOnPaste, focusCondition, buttonType, "Paste");
	}

	/**
	 * Allows users to use the keyboard to perform a select-all operation
	 * @param actionToTakeOnSelectAll the action-object to apply to a keyboard select-all when the component is
	 * 	                              an ancestor of a focused component
	 */
	public <T extends AbstractButton> T configureSelectAll(final Action actionToTakeOnSelectAll) {
		return this.configureSelectAll(actionToTakeOnSelectAll, FocusCondition.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
	}

	/**
	 * Allows users to use the keyboard to perform a select-all operation
	 * @param actionToTakeOnSelectAll the action-object to apply to a keyboard select-all when the component is
	 * 	                              an ancestor of a focused component
	 * @param buttonType the type of <code>AbstractButton</code> to create an instance of. Passing in `null` will return null,
	 *                     and no object will be created.
	 */
	public <T extends AbstractButton> T configureSelectAll(final Action actionToTakeOnSelectAll, Class<T> buttonType) {
		return this.configureSelectAll(actionToTakeOnSelectAll, FocusCondition.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, buttonType);
	}

	/**
	 * Allows users to use the keyboard to perform a select-all operation
	 * @param actionToTakeOnSelectAll the action-object to apply to a keyboard select-all
	 * @param focusCondition the degree of focus the object should have to trigger the action on select-all
	 */
	public <T extends AbstractButton> T configureSelectAll(final Action actionToTakeOnSelectAll, final FocusCondition focusCondition) {
		return this.performMapping(KeybindEncoding.KEYBIND_SELECT_ALL, actionToTakeOnSelectAll, focusCondition, null, null);
	}

	/**
	 * Allows users to use the keyboard to perform a select-all operation
	 * @param actionToTakeOnSelectAll the action-object to apply to a keyboard select-all
	 * @param focusCondition the degree of focus the object should have to trigger the action on select-all
	 * @param buttonType the type of <code>AbstractButton</code> to create an instance of. Passing in `null` will return null,
	 *                     and no object will be created.
	 */
	public <T extends AbstractButton> T configureSelectAll(final Action actionToTakeOnSelectAll, final FocusCondition focusCondition, Class<T> buttonType) {
		return this.performMapping(KeybindEncoding.KEYBIND_SELECT_ALL, actionToTakeOnSelectAll, focusCondition, buttonType, "Select All");
	}

	/*   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *\
	 *                       Core Methods                        *
	\*   *   *   *   *   *   *   *   *   *   *   *   *   *   *   */

	/**
	 *  Performs the proper mapping and builds an appropriate button object also tied to the desired behavior.
	 * @param triggerKey the keybinding you want to create. .
	 * @param actionToTakeOnSelectAll the action the keybinding / button should cause.
	 * @param focusCondition under what focus should the parent be under to activate the behavior on keypress.
	 * @param buttonType the type of button to create.
	 * @param buttonText the text to display on the button.
	 * @return the created button; if null is passed into <code>buttonType</code>, no object will be created and <code>null</code> will be returned.
	 */
	private <T extends AbstractButton> T performMapping(final KeybindEncoding triggerKey, final Action actionToTakeOnSelectAll, final FocusCondition focusCondition, final Class<T> buttonType, final String buttonText){
		/*
		 The problem on macOS is that for whatever reason, the actual
		 "pressing" of the standard copy/paste keybinds don't work correctly.
		 To work around this, we'll set the keybind to be on release instead.
		 A better long-term solution would be desireable.
		 */
		T button = this.createButton(buttonType, buttonText, actionToTakeOnSelectAll);
		KeyStroke copyKeyStroke = KeyStroke.getKeyStroke(triggerKey.keyEventCode, ShortcutsWizard.menuKeyMaskCode, ShortcutsWizard.operatingSystemInfo.isMac());
		this.inputMapMapping.get(focusCondition).put(copyKeyStroke, triggerKey.name());
		this.actionMap.put(triggerKey.name(), actionToTakeOnSelectAll.getWrappedAction());
		return button;
	}

	private <T extends AbstractButton> T createButton(final Class<T> buttonType, final String buttonText, final Action actionToTakeOnSelectAll){
		if (null == buttonType) return null;
		final String confirmedButtonText = buttonText == null ? "" : buttonText;
		T button;
			try {
				button = buttonType.getDeclaredConstructor(javax.swing.Action.class).newInstance(actionToTakeOnSelectAll.getWrappedAction());
			} catch (Exception e){
				throw new RuntimeException("Unable to create button for select-all:", e);
			}
		button.setName(confirmedButtonText.replaceAll("\\s", "") + buttonType.getSimpleName());
		button.setText(confirmedButtonText);
		return button;
	}
}
