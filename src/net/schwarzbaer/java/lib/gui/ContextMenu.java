package net.schwarzbaer.java.lib.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;
import java.util.function.Supplier;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ContextMenu extends JPopupMenu {
	private static final long serialVersionUID = 7336661746627669558L;
	
	private Vector<ContextMenuInvokeListener> listeners;
	
	public ContextMenu() {
		listeners = new Vector<>();
	}
	
	public void addTo(Component comp) {
		addTo(comp, null);
	}
	
	public void addTo(Component comp, Supplier<Point> computeSurrogateMousePosWhenContextMenuKeyWasPressed) {
		comp.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON3)
					showMenu(comp, e.getX(), e.getY());
			}
		});
		if (computeSurrogateMousePosWhenContextMenuKeyWasPressed != null) {
			comp.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent e) {
					if (e.getExtendedKeyCode() != KeyEvent.VK_CONTEXT_MENU) return;
					Point surrogateMousePos = computeSurrogateMousePosWhenContextMenuKeyWasPressed.get();
					if (surrogateMousePos==null) return;
					e.consume();
					showMenu(comp, surrogateMousePos.x, surrogateMousePos.y);
				}
			});
		}
	}
	
	public static Point computeSurrogateMousePos(JTable table, JScrollPane tableScrollPane, int columnM)
	{
		int[] rows = table.getSelectedRows();
		Arrays.sort(rows);
		int columnV = Math.max( 0, columnM<0 ? -1 : table.convertColumnIndexToView(columnM) );
		Rectangle viewRect = tableScrollPane.getViewport().getViewRect();
		for (int rowV : rows) {
			Rectangle cellRect = table.getCellRect(rowV, columnV, false);
			//System.out.printf("CellRect:  ( r:%d, c:%d )  ->  ( x:%d, y:%d, w:%d, d:%d )%n", rowV, columnV, cellRect.x, cellRect.y, cellRect.width, cellRect.height);
			int centerX = cellRect.x + cellRect.width /2;
			int centerY = cellRect.y + cellRect.height/2;
			if (viewRect.contains(centerX, centerY))
				return new Point(centerX, centerY);
		}
		//System.out.printf("Can't find SurrogateMousePos for %s%n", Arrays.toString(rows));
		int centerX = viewRect.x + viewRect.width /2;
		int centerY = viewRect.y + viewRect.height/2;
		return new Point(centerX, centerY);
	}
	
	private void showMenu(Component comp, int x, int y)
	{
		for (ContextMenuInvokeListener listener:listeners)
			listener.contextMenuWillBeInvoked(comp, x, y);
		show(comp, x, y);
	}
	
	public void    addContextMenuInvokeListener( ContextMenuInvokeListener listener ) { listeners.   add(listener); } 
	public void removeContextMenuInvokeListener( ContextMenuInvokeListener listener ) { listeners.remove(listener); }
	
	public interface ContextMenuInvokeListener {
		public void contextMenuWillBeInvoked(Component comp, int x, int y);
	}
}
