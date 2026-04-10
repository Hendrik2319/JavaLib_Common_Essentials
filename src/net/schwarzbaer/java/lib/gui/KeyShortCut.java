package net.schwarzbaer.java.lib.gui;

import java.awt.event.KeyEvent;

public record KeyShortCut(
		int keyCode,
		boolean withShift,
		boolean withCtrl,
		boolean withAlt,
		boolean withAltGr,
		String keyLabel
) {
	public KeyShortCut(int keyCode)
	{
		this(keyCode, false, false, false, false, getKeyText(keyCode, false, false, false, false));
	}
	public KeyShortCut(
			int keyCode,
			boolean withShift,
			boolean withCtrl ,
			boolean withAlt  ,
			boolean withAltGr
	) {
		this(
				keyCode,
				withShift,
				withCtrl ,
				withAlt  ,
				withAltGr,
				getKeyText(
						keyCode,
						withShift,
						withCtrl ,
						withAlt  ,
						withAltGr
				)
		);
	}
	
	public static String getKeyText(
			int keyCode,
			boolean withShift,
			boolean withCtrl ,
			boolean withAlt  ,
			boolean withAltGr
	) {
		StringBuilder sb = new StringBuilder();
		if (withCtrl ) sb.append("Ctrl+");
		if (withAlt  ) sb.append("Alt+");
		if (withShift) sb.append("Shift+");
		if (withAltGr) sb.append("AltGr+");
		sb.append(KeyEvent.getKeyText(keyCode));
		return sb.toString();
	}
	public static <ContainerType extends Container> ContainerType getFrom(KeyEvent e, ContainerType[] values)
	{
		return getFrom(e.getKeyCode(), e.getModifiersEx(), values);
	}
	public static <ContainerType extends Container> ContainerType getFrom(int keyCode, int modifiersEx, ContainerType[] values)
	{
		boolean withShift = (modifiersEx & KeyEvent.SHIFT_DOWN_MASK    ) != 0;
		boolean withCtrl  = (modifiersEx & KeyEvent.CTRL_DOWN_MASK     ) != 0;
		boolean withAlt   = (modifiersEx & KeyEvent.ALT_DOWN_MASK      ) != 0;
		boolean withAltGr = (modifiersEx & KeyEvent.ALT_GRAPH_DOWN_MASK) != 0;
		for (ContainerType container : values)
		{
			KeyShortCut val = container.getKeyShortCut();
			if ( val!=null &&
				 (val.keyCode   == keyCode  ) &&
				 (val.withShift == withShift) &&
				 (val.withCtrl  == withCtrl ) &&
				 (val.withAlt   == withAlt  ) &&
				 (val.withAltGr == withAltGr) )
				return container;
		}
		return null;
	}
	
	public String addKeyLabel(String baseStr)
	{
		return "%s (%s)".formatted(baseStr, keyLabel);
	}
	
	public interface Container
	{
		KeyShortCut getKeyShortCut();
		
		default String addKeyLabel(String baseStr)
		{
			return getKeyShortCut().addKeyLabel(baseStr);
		}
	}
}

