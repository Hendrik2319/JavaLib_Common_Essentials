package net.schwarzbaer.java.lib.gui;

import java.awt.Component;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import net.schwarzbaer.java.lib.system.Settings;

public class LookAndFeelSwitch<ValueKey extends Enum<ValueKey>>
{
	public static void setSystemLookAndFeel()
	{
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
	}

	public static <ValueKey extends Enum<ValueKey>> JMenu createMenu(String title, Settings<?, ValueKey> settings, ValueKey storageKey)
	{
		return new LookAndFeelSwitch<>(settings, storageKey).createMenu(title);
	}

	private final Vector<LAFInfo> installedLookAndFeels;
	private final LAFInfo crossPlatformLookAndFeel;
	private final LAFInfo systemLookAndFeel;
	private final Settings<?, ValueKey> settings;
	private final ValueKey storageKey;
	private Component uiTreeRoot;

	public LookAndFeelSwitch(Settings<?, ValueKey> settings, ValueKey storageKey)
	{
		uiTreeRoot = null;
		this.settings = settings;
		this.storageKey = storageKey;
		installedLookAndFeels = new Vector<>();
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
			installedLookAndFeels.add(new LAFInfo(laf.getName(), laf.getClassName()));
		
		crossPlatformLookAndFeel = new LAFInfo("<Cross Platform>", UIManager.getCrossPlatformLookAndFeelClassName());
		systemLookAndFeel        = new LAFInfo("<System>"        , UIManager.getSystemLookAndFeelClassName());
	}
	
	record LAFInfo( String name, String className ) {}
	
	public void setInitialLookAndFeel()
	{
		String selectedLAF = settings==null || storageKey==null ? null : settings.getString(storageKey, null);
		if (selectedLAF!=null)
			try
			{
				UIManager.setLookAndFeel(selectedLAF);
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
			{
				System.err.printf("%s while setting initial Look&Feel to <%s>: %s%n", ex.getClass().getSimpleName(), selectedLAF, ex.getMessage());
				// ex.printStackTrace();
			}
}
	
	public void setUITreeRoot(Component uiTreeRoot)
	{
		this.uiTreeRoot = uiTreeRoot;
	}

	public JMenu createMenu(String title)
	{
		String selectedLAF = settings==null || storageKey==null ? null : settings.getString(storageKey, null);
		ButtonGroup bg = new ButtonGroup();
		
		JMenu menu = new JMenu(title);
		menu.add(createMenuItem(systemLookAndFeel       , bg, selectedLAF));
		menu.add(createMenuItem(crossPlatformLookAndFeel, bg, selectedLAF));
		if (!installedLookAndFeels.isEmpty())
		{
			menu.addSeparator();
			for (LAFInfo laf : installedLookAndFeels)
				menu.add(createMenuItem(laf, bg, selectedLAF));
		}
		
		return menu;
	}

	private JCheckBoxMenuItem createMenuItem(LAFInfo lafInfo, ButtonGroup bg, String selectedLAF)
	{
		JCheckBoxMenuItem comp = new JCheckBoxMenuItem(lafInfo.name, lafInfo.className.equalsIgnoreCase(selectedLAF));
		bg.add(comp);
		
		comp.addActionListener(e->{
			try
			{
				UIManager.setLookAndFeel(lafInfo.className);
				storeSelectedLAF(lafInfo.className);
				if (uiTreeRoot != null) SwingUtilities.updateComponentTreeUI(uiTreeRoot);
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
			{
				System.err.printf("%s while changing Look&Feel to \"%s\" <%s>: %s%n", ex.getClass().getSimpleName(), lafInfo.name, lafInfo.className, ex.getMessage());
				// ex.printStackTrace();
			}
		});
		
		return comp;
	}

	private void storeSelectedLAF(String className)
	{
		if (settings != null && storageKey != null)
			settings.putString(storageKey, className);
	}
}
