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
		Muted, UnMuted, Up, Down, Power_IsOn, Power_IsOff,
		Reload, ReloadCCW, Download, Image, Save, Add,
		Copy, Paste, Cut, Delete,
		Folder, AddFolder, ReloadFolder,
		Play, Pause, Stop, Skip2Prev, Skip2Next,
		Print,Trash,Memory,
		
		Muted_Dis, UnMuted_Dis, Up_Dis, Down_Dis, Power_IsOn_Dis, Power_IsOff_Dis,
		Reload_Dis, ReloadCCW_Dis, Download_Dis, Image_Dis, Save_Dis, Add_Dis,
		Copy_Dis, Paste_Dis, Cut_Dis, Delete_Dis,
		Folder_Dis, AddFolder_Dis, ReloadFolder_Dis,
		Play_Dis, Pause_Dis, Stop_Dis, Skip2Prev_Dis, Skip2Next_Dis,
		Print_Dis,Trash_Dis,Memory_Dis,
		;
		public Icon getIcon() { return iconSource.getCachedIcon(this); }
		private static IconSource.CachedIcons<GrayCommandIcons> iconSource = IconSource.createCachedIcons(16, 16, 27, "GeneralIcons.GrayCommandIcons.png", GrayCommandIcons.values());
		
		public enum IconGroup implements GeneralIcons.IconGroup {
			Muted        (GrayCommandIcons.Muted       , GrayCommandIcons.Muted_Dis       ),
			UnMuted      (GrayCommandIcons.UnMuted     , GrayCommandIcons.UnMuted_Dis     ),
			Up           (GrayCommandIcons.Up          , GrayCommandIcons.Up_Dis          ),
			Down         (GrayCommandIcons.Down        , GrayCommandIcons.Down_Dis        ),
			Power_IsOn   (GrayCommandIcons.Power_IsOn  , GrayCommandIcons.Power_IsOn_Dis  ),
			Power_IsOff  (GrayCommandIcons.Power_IsOff , GrayCommandIcons.Power_IsOff_Dis ),
			Reload       (GrayCommandIcons.Reload      , GrayCommandIcons.Reload_Dis      ),
			ReloadCCW    (GrayCommandIcons.ReloadCCW   , GrayCommandIcons.ReloadCCW_Dis   ),
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
			Play         (GrayCommandIcons.Play        , GrayCommandIcons.Play_Dis        ),
			Pause        (GrayCommandIcons.Pause       , GrayCommandIcons.Pause_Dis       ),
			Stop         (GrayCommandIcons.Stop        , GrayCommandIcons.Stop_Dis        ),
			Skip2Prev    (GrayCommandIcons.Skip2Prev   , GrayCommandIcons.Skip2Prev_Dis   ),
			Skip2Next    (GrayCommandIcons.Skip2Next   , GrayCommandIcons.Skip2Next_Dis   ),
			Print        (GrayCommandIcons.Print       , GrayCommandIcons.Print_Dis       ),
			Trash        (GrayCommandIcons.Trash       , GrayCommandIcons.Trash_Dis       ),
			Memory       (GrayCommandIcons.Memory      , GrayCommandIcons.Memory_Dis      ),
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
