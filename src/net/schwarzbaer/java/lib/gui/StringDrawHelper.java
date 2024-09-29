package net.schwarzbaer.java.lib.gui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class StringDrawHelper
{
	public enum VerticalAnchorPos {
		Original,
		Top,
		Center,
		Bottom,
	}
	public enum HorizontalAnchorPos {
		Original,
		Left,
		Center,
		Right,
	}
	
	public static Rectangle2D drawString(Graphics2D g2, int x, int y, String str, HorizontalAnchorPos anchorX, VerticalAnchorPos anchorY)
	{
		int offsetX = 0;
		int offsetY = 0;
		
		Rectangle2D strBounds = g2.getFont().getStringBounds(str, g2.getFontRenderContext());
		
		if (anchorX==null)
			anchorX = HorizontalAnchorPos.Original;
		switch (anchorX)
		{
		case Original: offsetX = 0; break;
		case Left    : offsetX = (int) Math.round(-strBounds.getMinX   ()); break;
		case Center  : offsetX = (int) Math.round(-strBounds.getCenterX()); break;
		case Right   : offsetX = (int) Math.round(-strBounds.getMaxX   ()); break;
		}
		
		if (anchorY==null)
			anchorY = VerticalAnchorPos.Original;
		switch (anchorY)
		{
		case Original: offsetY = 0; break;
		case Top     : offsetY = (int) Math.round(-strBounds.getMinY   ()); break;
		case Center  : offsetY = (int) Math.round(-strBounds.getCenterY()); break;
		case Bottom  : offsetY = (int) Math.round(-strBounds.getMaxY   ()); break;
		}
	
		g2.drawString(str, x+offsetX, y+offsetY);
		
		return new Rectangle2D.Double(strBounds.getX()+offsetX, strBounds.getY()+offsetY, strBounds.getWidth(), strBounds.getHeight());
	}
}
