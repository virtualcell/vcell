package org.vcell.util.gui;

import cbit.vcell.client.task.AsynchClientTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ShortcutsWizardTest {
	private static final Logger lg = LogManager.getLogger(ShortcutsWizardTest.class);
	private static final JComponent dummyComponent = new JPanel();
	private ShortcutsWizard shortcutsWizard;

	@BeforeAll
	public static void setUpBeforeClass() {
		ShortcutsWizardTest.dummyComponent.setEnabled(true);
	}

	@BeforeEach
	public void setUp() {
		this.shortcutsWizard = new ShortcutsWizard(ShortcutsWizardTest.dummyComponent);
	}

	@Test
	public void testButtonsAreCreated(){
		List<Boolean> ignored = new ArrayList<>();
		ShortcutsWizard.Action action = this.shortcutsWizard.createAction("test", AsynchClientTask.TASKTYPE_SWING_BLOCKING, e -> ignored.add(true));
		Assertions.assertInstanceOf(JButton.class, this.shortcutsWizard.configureCopy(action, JButton.class));
		Assertions.assertInstanceOf(JMenuItem.class, this.shortcutsWizard.configurePaste(action, JMenuItem.class));
	}

	@Test
	public void testThatActionsTrigger() throws InterruptedException{
		final List<Boolean> successfullyRan = new ArrayList<>();
		final Hashtable<String, Object> hashTable = new Hashtable<>();
		final Semaphore semaphore = new Semaphore(1);

		/// Test 1
		AsynchClientTask basicTask = new AsynchClientTask("test1", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) {
				successfullyRan.add(true);
				semaphore.release();
			}
		};
		ShortcutsWizard.Action action1 = this.shortcutsWizard.createAction(basicTask);
		JButton button1 = this.shortcutsWizard.configureCopy(action1, JButton.class); // Configure type doesn't matter for this test
		semaphore.acquire();
		button1.doClick();
		semaphore.acquire();
		Assertions.assertEquals(1, successfullyRan.size());
		Assertions.assertTrue(successfullyRan.get(successfullyRan.size() - 1));
		semaphore.release();

		/// Test 2
		hashTable.put("key", true);
		AsynchClientTask hashtableTask = new AsynchClientTask("test2", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) {
				if (hashTable.containsKey("key") && hashTable.remove("key") instanceof Boolean boolValue)
					successfullyRan.add(boolValue);
				semaphore.release();
			}
		};
		ShortcutsWizard.Action action2 = this.shortcutsWizard.createAction(hashtableTask, hashTable);
		JButton button2 = this.shortcutsWizard.configureCopy(action2, JButton.class);
		semaphore.acquire();
		button2.doClick();
		semaphore.acquire();
		Assertions.assertEquals(2, successfullyRan.size());
		Assertions.assertTrue(successfullyRan.get(successfullyRan.size() - 1));
		semaphore.release();

		/// Test 3
		ShortcutsWizard.Action action3 = this.shortcutsWizard.createAction("test3", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,
				e -> { successfullyRan.add(true); semaphore.release(); } );
		JButton button3 = this.shortcutsWizard.configureCopy(action3, JButton.class);
		semaphore.acquire();
		button3.doClick();
		semaphore.acquire();
		Assertions.assertEquals(3, successfullyRan.size());
		Assertions.assertTrue(successfullyRan.get(successfullyRan.size() - 1));
		semaphore.release();
	}

	@Test
	public void testCopyConfiguration() {
		List<Boolean> ignored = new ArrayList<>();
		ActionMap actionMap = ShortcutsWizardTest.dummyComponent.getActionMap();

		ShortcutsWizard.Action action = this.shortcutsWizard.createAction("test", AsynchClientTask.TASKTYPE_SWING_BLOCKING, e -> ignored.add(true));
		this.shortcutsWizard.configureCopy(action);
		Assertions.assertEquals(1, actionMap.size());
		Assertions.assertEquals(1, ShortcutsWizardTest.dummyComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).size());
	}

	@Test
	public void testCopyConfigurationWithNonDefaultFocus(){
		List<Boolean> ignored = new ArrayList<>();
		ActionMap actionMap = ShortcutsWizardTest.dummyComponent.getActionMap();

		ShortcutsWizard.Action action = this.shortcutsWizard.createAction("test", AsynchClientTask.TASKTYPE_SWING_BLOCKING, e -> ignored.add(true));
		this.shortcutsWizard.configureCopy(action, ShortcutsWizard.FocusCondition.WHEN_FOCUSED);
		Assertions.assertEquals(1, actionMap.size());
		Assertions.assertEquals(1, ShortcutsWizardTest.dummyComponent.getInputMap(JComponent.WHEN_FOCUSED).size());
	}

	@Test
	public void testPasteConfiguration() {
		List<Boolean> ignored = new ArrayList<>();
		ActionMap actionMap = ShortcutsWizardTest.dummyComponent.getActionMap();

		ShortcutsWizard.Action action = this.shortcutsWizard.createAction("test", AsynchClientTask.TASKTYPE_SWING_BLOCKING, e -> ignored.add(true));
		this.shortcutsWizard.configurePaste(action);
		Assertions.assertEquals(1, actionMap.size());
	}

	@Test
	public void testSelectAllConfiguration() {
		List<Boolean> ignored = new ArrayList<>();
		ActionMap actionMap = ShortcutsWizardTest.dummyComponent.getActionMap();

		ShortcutsWizard.Action action = this.shortcutsWizard.createAction("test", AsynchClientTask.TASKTYPE_SWING_BLOCKING, e -> ignored.add(true));
		this.shortcutsWizard.configureSelectAll(action);
		Assertions.assertEquals(1, actionMap.size());
	}
}
