package net.schwarzbaer.java.lib.gui;

import java.util.Objects;

import javax.swing.Icon;

public final class GeneralIcons
{
	public interface IconGroup {
		public Icon getEnabledIcon ();
		public Icon getDisabledIcon();
	}
	
	public enum GrayCommandIcons {
		Muted, UnMuted, Up, Down, Power_IsOn, Power_IsOff, Reload, Download, Image, Save, Add, Copy, Paste, Cut, Delete, Folder, AddFolder, ReloadFolder,
		Muted_Dis, UnMuted_Dis, Up_Dis, Down_Dis, Power_IsOn_Dis, Power_IsOff_Dis, Reload_Dis, Download_Dis, Image_Dis, Save_Dis, Add_Dis, Copy_Dis, Paste_Dis, Cut_Dis, Delete_Dis, Folder_Dis, AddFolder_Dis, ReloadFolder_Dis,
		;
		public Icon getIcon() { return iconSource.getCachedIcon(this); }
		private static IconSource.CachedIcons<GrayCommandIcons> iconSource = IconSource.createCachedIcons(16, 16, 18, "GeneralIcons.GrayCommandIcons.png", GrayCommandIcons.values());
		
		public enum IconGroup implements GeneralIcons.IconGroup {
			Muted        (GrayCommandIcons.Muted       , GrayCommandIcons.Muted_Dis       ),
			UnMuted      (GrayCommandIcons.UnMuted     , GrayCommandIcons.UnMuted_Dis     ),
			Up           (GrayCommandIcons.Up          , GrayCommandIcons.Up_Dis          ),
			Down         (GrayCommandIcons.Down        , GrayCommandIcons.Down_Dis        ),
			Power_IsOn   (GrayCommandIcons.Power_IsOn  , GrayCommandIcons.Power_IsOn_Dis  ),
			Power_IsOff  (GrayCommandIcons.Power_IsOff , GrayCommandIcons.Power_IsOff_Dis ),
			Reload       (GrayCommandIcons.Reload      , GrayCommandIcons.Reload_Dis      ),
			Download     (GrayCommandIcons.Download    , GrayCommandIcons.Download_Dis    ),
			Image        (GrayCommandIcons.Image       , GrayCommandIcons.Image_Dis       ),
			Save         (GrayCommandIcons.Save        , GrayCommandIcons.Save_Dis        ),
			Add          (GrayCommandIcons.Add         , GrayCommandIcons.Add_Dis         ),
			Copy         (GrayCommandIcons.Copy        , GrayCommandIcons.Copy_Dis        ),
			Paste        (GrayCommandIcons.Paste       , GrayCommandIcons.Paste_Dis       ),
			Cut          (GrayCommandIcons.Cut         , GrayCommandIcons.Cut_Dis         ),
			Delete       (GrayCommandIcons.Delete      , GrayCommandIcons.Delete_Dis      ),
			Folder       (GrayCommandIcons.Folder      , GrayCommandIcons.Folder_Dis      ),
			AddFolder    (GrayCommandIcons.AddFolder   , GrayCommandIcons.AddFolder_Dis   ),
			ReloadFolder (GrayCommandIcons.ReloadFolder, GrayCommandIcons.ReloadFolder_Dis),
			;
			public final GrayCommandIcons  enabledIcon;
			public final GrayCommandIcons disabledIcon;
			IconGroup() { this(null, null); }
			IconGroup(GrayCommandIcons enabledIcon, GrayCommandIcons disabledIcon) {
				this. enabledIcon = Objects.requireNonNull( enabledIcon);
				this.disabledIcon = Objects.requireNonNull(disabledIcon);
			}
			@Override public Icon getEnabledIcon () { return  enabledIcon.getIcon(); }
			@Override public Icon getDisabledIcon() { return disabledIcon.getIcon(); }
		}
	}
	
}
