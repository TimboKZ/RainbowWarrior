package kz.kuzhagaliyev.TimboKZ.RainbowWarrior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public final class RainbowWarrior extends JavaPlugin implements Listener {
	
	String pref = ChatColor.DARK_AQUA + "[Rainbow Warrior] " + ChatColor.WHITE;
	String brO = ChatColor.DARK_GRAY + "(";
	String brC = ")" + ChatColor.WHITE;
	String it = ChatColor.YELLOW + "";
	String itW = ChatColor.WHITE + "";
    Random rand = new Random();
	boolean pluginEnabled = true;
	int updateRate = 4;
	boolean usePermissions = true;
	
	public List<String> rainbowWarriorActive = new ArrayList<String>();
	public HashMap<String, Integer> rainbowWarriorTaskID = new HashMap<String, Integer>();
	
    @Override
    public void onEnable(){
    	try {
    	    new Metrics(this).start();
    	} catch (IOException e) {
    	    getLogger().info(e.getMessage());
    	}
    	this.saveDefaultConfig();
    	pluginEnabled = this.getConfig().getBoolean("enablePlugin");
    	updateRate = this.getConfig().getInt("updateRate");
    	usePermissions = this.getConfig().getBoolean("usePermissions");
    	Bukkit.getPluginManager().registerEvents(this, this);
    }
    @Override
    public void onDisable() {
    	for(String s : rainbowWarriorActive) {
    		Player player = Bukkit.getPlayer(s);
    		if(player != null) {
    			player.getInventory().setArmorContents(null);
    			player.sendMessage(pref+"Plugin was disabled, you are no longer a "+it+"Rainbow Warrior"+itW+".");
    		}
    	}
    	rainbowWarriorActive.clear();
    } 
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
	    if(event.getSlotType().equals(SlotType.ARMOR)) {
	    	if(event.getCurrentItem().getType() == Material.LEATHER_HELMET
	    		|| event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE
	    		|| event.getCurrentItem().getType() == Material.LEATHER_LEGGINGS
	    		|| event.getCurrentItem().getType() == Material.LEATHER_BOOTS) {
	    		if(rainbowWarriorActive.contains(event.getWhoClicked().getName())) {
		    		event.setCancelled(true);
		    		event.getWhoClicked().closeInventory();
		    	}
	    	}
	    }
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if (sender instanceof Player) {
    		final Player player = (Player) sender;
    		String playerName = player.getName().toString();
	    	if (label.equalsIgnoreCase("rainbowhelp")) {
	    		if(player.hasPermission("rainbowwarrior.help")) {
	        		if(pluginEnabled) {
			    		sender.sendMessage(pref+"Plugin's help section "+it+"==============="+itW);
			    		sender.sendMessage(it+"/rainbowme"+itW+" Turns you into a Rainbow Warrior.");
			    		sender.sendMessage(it+"/rainbowother {Player}"+itW+" Turns specified player into a Rainbow Warrior.");
			    		sender.sendMessage(it+"/rainbowreload"+itW+" Reload plugin's config.yml.");
			    		sender.sendMessage(it+"================================================"+itW);
	        		} else {
	        			sender.sendMessage(pref+"Plugin is disabled in "+it+"config.yml"+itW+".");
	        			sender.sendMessage(pref+it+"/rainbowreload"+itW+" Reload plugin's config.yml.");
	        		}
	    		} else {
	    			sender.sendMessage(pref+"You're not allowed to access help section.");
	    		}
	    	} else if (label.equalsIgnoreCase("rainbowreload")) {
	    		if(player.hasPermission("rainbowwarrior.reload")) {
	    			this.reloadConfig();
	    	    	pluginEnabled = this.getConfig().getBoolean("enablePlugin");
	    	    	if(!pluginEnabled) {
	    	        	for(String s : rainbowWarriorActive) {
	    	        		Player p = Bukkit.getPlayer(s);
	    	        		if(p != null) {
	    	        			p.getInventory().setArmorContents(null);
	    	        			p.sendMessage(pref+"Plugin was disabled, you are no longer a "+it+"Rainbow Warrior"+itW+".");
	    	        		}
	    	        	}
	    	        	rainbowWarriorActive.clear();
	    	    	}
	    	    	updateRate = this.getConfig().getInt("updateRate");
	    	    	usePermissions = this.getConfig().getBoolean("usePermissions");
	    			sender.sendMessage(pref+it+"config.yml"+itW+" has been reloaded.");
	    		} else {
	    			sender.sendMessage(pref+"You're not allowed to reload the plugin.");
	    		}
	    	} else if (label.equalsIgnoreCase("rainbowme")) {
	    		if(pluginEnabled) {
		    		if(player.hasPermission("rainbowwarrior.me") || !usePermissions) {
						if(!rainbowWarriorActive.contains(player.getName())) {
							if(player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null) {
		    					rainbowWarriorTaskID.put(playerName, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		        	    		    public void run() {
		        	    		    	rainbowWarrior(player);
		        	    		    }
		        	    		}, 0, updateRate));
		            			rainbowWarriorActive.add(playerName);
		            			player.sendMessage(pref+"You are a "+it+"Rainbow Warrior"+itW+"!");
							} else {
			    				player.sendMessage(pref+"Sorry, to become a "+it+"Rainbow Warrior"+itW+" you need to empty all your armor slots.");
			    			}
		        		} else {
		        			Bukkit.getScheduler().cancelTask(rainbowWarriorTaskID.get(playerName));
		        			rainbowWarriorActive.remove(playerName);
		        			rainbowWarriorTaskID.remove(playerName);
		        			player.getInventory().setArmorContents(null);
		        			player.sendMessage(pref+"You are no longer a "+it+"Rainbow Warrior"+itW+"!");
		        		}
		    		} else {
		    			player.sendMessage(pref+"Sorry, you're not allowed to use "+it+"/rainbowme"+itW+".");
		    		}
	    		} else {
	    			sender.sendMessage(pref+"Plugin is disabled in "+it+"config.yml"+itW+".");
	    		}
	    	} else if (label.equalsIgnoreCase("rainbowother")) {
	    		if(pluginEnabled) {
		    		if(player.hasPermission("rainbowwarrior.other") || !usePermissions) {
		    			if(args.length == 1) {
		    				List<Player> matchedPlayer = Bukkit.matchPlayer(args[0]);
		    				if(matchedPlayer.size() == 1) {
		    					final Player rainbowTarget = matchedPlayer.get(0);
		    					playerName = rainbowTarget.getName().toString();
		    					if(!rainbowWarriorActive.contains(playerName)) {
									if(player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null) {
				    					rainbowWarriorTaskID.put(playerName, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				        	    		    public void run() {
				        	    		    	rainbowWarrior(rainbowTarget);
				        	    		    }
				        	    		}, 0, updateRate));
				            			rainbowWarriorActive.add(playerName);
				            			rainbowTarget.sendMessage(pref+"You are a "+it+"Rainbow Warrior"+itW+"!");
				            			player.sendMessage(pref+"Player "+it+playerName+itW+" is now a "+it+"Rainbow Warrior"+itW+"!");
									} else {
										rainbowTarget.sendMessage(pref+"Sorry, to become a "+it+"Rainbow Warrior"+itW+" you need to empty all your armor slots.");
					    				player.sendMessage(pref+"Sorry, "+it+playerName+itW+" needs to empty his/her armor slots to become a "+it+"Rainbow Warrior"+itW+".");
					    			}
				        		} else {
				        			Bukkit.getScheduler().cancelTask(rainbowWarriorTaskID.get(playerName));
				        			rainbowWarriorActive.remove(playerName);
				        			rainbowWarriorTaskID.remove(playerName);
				        			rainbowTarget.getInventory().setArmorContents(null);
				        			rainbowTarget.sendMessage(pref+"You are no longer a "+it+"Rainbow Warrior"+itW+"!");
				        			player.sendMessage(pref+"Player "+it+playerName+itW+" is no longer a "+it+"Rainbow Warrior"+itW+"!");
				        		}
		    				} else {
		    					player.sendMessage(pref+"Cannot find the player matching "+it+args[0]+itW+". Either player not found or there are too many matches.");
		    				}
		    			} else {
		    				player.sendMessage(pref+"Invalid amount of arguments. Only specify "+it+"target's"+itW+" name.");
		    			}
		    		} else {
		    			player.sendMessage(pref+"Sorry, you're not allowed to use "+it+"/rainbowother"+itW+".");
		    		}
	    		} else {
	    			sender.sendMessage(pref+"Plugin is disabled in "+it+"config.yml"+itW+".");
	    		}
	    	}
    	}
    	return true;
    }
    
    public void rainbowWarrior(Player player) {
    	PlayerInventory playerInventory = player.getInventory();
    	
    	ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
    	LeatherArmorMeta lam = (LeatherArmorMeta)lhelmet.getItemMeta();
    	lam.setColor(Color.fromRGB(randInt(1, 255), randInt(1, 255), randInt(1, 255)));
    	lhelmet.setItemMeta(lam);
    	playerInventory.setHelmet(lhelmet);
    	
    	ItemStack lchestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
    	lam.setColor(Color.fromRGB(randInt(1, 255), randInt(1, 255), randInt(1, 255)));
    	lchestplate.setItemMeta(lam);
    	playerInventory.setChestplate(lchestplate);
    	
    	ItemStack lleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
    	lam.setColor(Color.fromRGB(randInt(1, 255), randInt(1, 255), randInt(1, 255)));
    	lleggings.setItemMeta(lam);
    	playerInventory.setLeggings(lleggings);
    	
    	ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS, 1);
    	lam.setColor(Color.fromRGB(randInt(1, 255), randInt(1, 255), randInt(1, 255)));
    	lboots.setItemMeta(lam);
    	playerInventory.setBoots(lboots);
    }
    public int randInt(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }    
}
