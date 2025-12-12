package net.schwarzbaer.java.lib.gui;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public record ScrollPosition(int min, int max, int ext, int val)
{
	public enum ScrollBarType {
		Vertical, Horizontal
	}
	
	public static JScrollBar getScrollBar(JScrollPane scrollPane, ScrollBarType scrollBarType)
	{
		if (scrollBarType!=null && scrollPane!=null)
			switch (scrollBarType)
			{
				case Horizontal: return scrollPane.getHorizontalScrollBar();
				case Vertical  : return scrollPane.getVerticalScrollBar();
			}
		return null;
	}

	public static ScrollPosition get(JScrollBar scrollBar)
	{
		if (scrollBar==null) return null;
		int min = scrollBar.getMinimum();
		int max = scrollBar.getMaximum();
		int ext = scrollBar.getVisibleAmount();
		int val = scrollBar.getValue();
		return new ScrollPosition(min, max, ext, val);
	}
	
	public static void keepScrollPos(JScrollPane scrollPane, ScrollBarType scrollBarType, Runnable changingTask)
	{
		ScrollPosition scrollPos = get(scrollPane, scrollBarType);
		
		changingTask.run();
		
		if (scrollPos!=null)
			SwingUtilities.invokeLater(()->scrollPos.set(scrollPane, scrollBarType));
	}

	public static ScrollPosition get(JScrollPane scrollPane, ScrollBarType scrollBarType)
	{
		return get(getScrollBar(scrollPane, scrollBarType));
	}

	public void set(JScrollPane scrollPane, ScrollBarType scrollBarType)
	{
		JScrollBar scrollBar = getScrollBar(scrollPane, scrollBarType);
		if (scrollBar==null) return;
		ScrollPosition newValues = get(scrollBar);
		if (newValues==null) return;
		//System.err.printf("scroll pos (%s): %s -> %s%n", scrollBarType, this, newValues);
		
		if (val==0) // start of page -> start of page
			scrollBar.setValue(val);
		
		else if (val+ext >= max) // end of page -> end of page
			scrollBar.setValue(newValues.max-newValues.ext);
		
		else if (val+newValues.ext >= newValues.max) // old val > max -> end of page
			scrollBar.setValue(newValues.max-newValues.ext);
		
		else
			scrollBar.setValue(val);
		
		//System.err.printf("scroll pos (%s): %s -> %s -> %s%n", scrollBarType, this, newValues, get(scrollBar));
	}

	public static ScrollPosition getVertical  (JScrollPane scrollPane) { return get(scrollPane, ScrollBarType.Vertical  ); }
	public static ScrollPosition getHorizontal(JScrollPane scrollPane) { return get(scrollPane, ScrollBarType.Horizontal); }
	public void                  setVertical  (JScrollPane scrollPane) {        set(scrollPane, ScrollBarType.Vertical  ); }
	public void                  setHorizontal(JScrollPane scrollPane) {        set(scrollPane, ScrollBarType.Horizontal); }
}
