package team.creativecode.diamail.manager;

import java.io.IOException;
import java.util.HashMap;

public class PlayerSetting {

	final String path = "player-setting";
	
	private PlayerData pd;
	private HashMap<String, Object> setting = new HashMap<String, Object>();
	
	public PlayerSetting(PlayerData pd) {
		this.pd = pd;
		
		for (String key : this.pd.getConfig().getConfigurationSection(path).getKeys(false)) {
			this.setting.put(key, pd.getConfig().get(path + "." + key));
		}
	}
	
	public void changeSetting(String key, Object value) {
		this.setting.put(key, value);
		updateSetting();
	}
	
	public void updateSetting() {
		for (String key : this.setting.keySet()) {
			pd.getConfig().set(path + "." + key, this.setting.get(key));
		}
		try {
			pd.getConfig().save(pd.getFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PlayerData getPlayerData() {
		return this.pd;
	}
	
	public HashMap<String, Object> getSettings(){
		return this.setting;
	}
	
}
