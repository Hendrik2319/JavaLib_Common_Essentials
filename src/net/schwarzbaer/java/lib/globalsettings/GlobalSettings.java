package net.schwarzbaer.java.lib.globalsettings;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.schwarzbaer.java.lib.system.Settings;

public final class GlobalSettings extends Settings<GlobalSettings.ValueGroup, GlobalSettings.Key>
{
	public enum Key {
		JavaVM, Browser, VideoPlayer,
	}
	
	enum ValueGroup implements Settings.GroupKeys<Key> {
		;
		Key[] keys;
		ValueGroup(Key...keys) { this.keys = keys;}
		@Override public Key[] getKeys() { return keys; }
	}
	
	private static GlobalSettings instance = null;
	
	public static GlobalSettings getInstance()
	{
		if (instance == null)
			instance = new GlobalSettings();
		return instance;
	}

	private final JFileChooser exeFileChooser;

	private GlobalSettings()
	{
		super(GlobalSettings.class);
		
		exeFileChooser = new JFileChooser("./");
		exeFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		exeFileChooser.setMultiSelectionEnabled(false);
		exeFileChooser.setFileFilter(new FileNameExtensionFilter("Executable (*.exe)","exe"));
	}
	
	public boolean hasExecutable(Key key) {
		if (!contains(key)) return false;
		File executable = getExecutable(key);
		return executable!=null && executable.isFile();
	}
	
	public File getExecutableOrAskUser(Component parent, String dialogTitle, Key key) {
		if (!contains(key))
			return askUserForExecutable(parent, dialogTitle, key);
		return getExecutable(key);
	}

	public File getExecutable(Key key)
	{
		return getFile(key, null);
	}
	
	public File askUserForExecutable(Component parent, String dialogTitle, Key key) {
		File currentValue = getExecutable(key);
		if (currentValue!=null && currentValue.isFile())
			exeFileChooser.setSelectedFile(currentValue);
		exeFileChooser.setDialogTitle(dialogTitle);
		
		if (exeFileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION)
			return null;
		
		File selectedValue = exeFileChooser.getSelectedFile();
		putFile(key, selectedValue);
		return selectedValue;
	}
}
