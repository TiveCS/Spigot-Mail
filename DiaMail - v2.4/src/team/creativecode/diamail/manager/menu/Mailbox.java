package team.creativecode.diamail.manager.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import team.creativecode.diamail.manager.Menu;

public class Mailbox extends Menu{

	public Mailbox() {
		super();
	}

	@Override
	public void action(Player clicker, int slot) {
		if (this.getClickData().containsKey(slot)) {
			List<String> input = new ArrayList<String>(this.getClickData().get(slot).get(ClickDataType.ANY));
			
			for (String s : input) {
				if (s.equalsIgnoreCase("NEXT_PAGE")) {
					this.nextPage();
				}else if (s.equalsIgnoreCase("PREVIOUS_PAGE")) {
					this.previousPage();
				}
			}
		}
	}

}
