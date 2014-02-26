package asciiWorld.ui;

import asciiWorld.entities.HotKeyInfo;

public interface HotKeySelectedEvent {
	
	void selected(HotKeyPanel sender, HotKeyInfo info);
}