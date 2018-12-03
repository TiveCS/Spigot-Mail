package team.creativecode.diamail.manager;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

public class CommandManager {

	private String cmdName;
	private CommandMap cmdMap;
	private Command cmdClass;
	
	
	public CommandManager(String cmd, Command cmdclass) {
		this.cmdClass = cmdclass;
		this.cmdName = cmd;
		
		try {
			Field map = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			map.setAccessible(true);
			
			this.cmdMap = (CommandMap) map.get(Bukkit.getServer());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public CommandManager(String cmd) {
		
		this.cmdName = cmd;
		
		try {
			Field map = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			map.setAccessible(true);
			
			this.cmdMap = (CommandMap) map.get(Bukkit.getServer());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public void register() {
		try {
			this.cmdMap.register(this.cmdName, this.cmdClass);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void register(Command cmd) {
		this.cmdMap.register(this.cmdName, cmd);
	}
	
	public void setDescription(String msg) {
		this.cmdMap.getCommand(this.cmdName).setDescription(msg);
	}
	
	public void setPermission(String permission) {
		this.cmdMap.getCommand(this.cmdName).setPermission(permission);
	}
	
	public void setPermissionMsg(String msg) {
		this.cmdMap.getCommand(this.cmdName).setPermissionMessage(msg);
	}
	
	public void setAliases(List<String> aliases) {
		this.cmdMap.getCommand(this.cmdName).setAliases(aliases);
	}
	
	public String getDescription() {
		return this.cmdMap.getCommand(this.cmdName).getDescription();
	}
	
	public String getPermission(){
		return this.cmdMap.getCommand(this.cmdName).getPermission();
	}
	
	public String getPermissionMsg() {
		return this.cmdMap.getCommand(this.cmdName).getPermissionMessage();
	}
	
	public List<String> getAliases(){
		return this.cmdMap.getCommand(this.cmdName).getAliases();
	}
	
	public String getCommandName() {
		return this.cmdName;
	}
	
	public CommandMap getCommandMap() {
		return this.cmdMap;
	}
	
	public Command getCommandClass() {
		return this.cmdClass;
	}
	
}
