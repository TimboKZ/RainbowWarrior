package kz.kuzhagaliyev.TimboKZ.RainbowWarrior;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	boolean pluginEnabled = true;
	int updateRate = 4;
	boolean usePermissions = true;
	
	public Map<String, Boolean> rainbowWarriorActive = new HashMap<String, Boolean>();
	public Map<String, Integer> rainbowWarriorTaskID = new HashMap<String, Integer>();
	
    @Override
    public void onEnable(){
    	
    	try {
    	    Metrics metrics = new Metrics(this);
    	    metrics.start();
    	} catch (IOException e) {
    	    getLogger().info(e.getMessage());
    	}
    	
    	this.saveDefaultConfig();
    	pluginEnabled = this.getConfig().getBoolean("enablePlugin");
    	updateRate = this.getConfig().getInt("updateRate");
    	usePermissions = this.getConfig().getBoolean("usePermissions");
    	Bukkit.getPluginManager().registerEvents(this, this);
    	getLogger().info("Plugin was enabled!");
    }
 
    @Override
    public void onDisable() {
    	for(Entry<String, Boolean> playerActiveList : rainbowWarriorActive.entrySet()) {
    		if(playerActiveList.getValue() != null || playerActiveList.getValue()) {
    			Player player = Bukkit.getPlayer(playerActiveList.getKey());
    			player.getInventory().setHelmet(null);
    			player.getInventory().setChestplate(null);
    			player.getInventory().setLeggings(null);
    			player.getInventory().setBoots(null);
    			player.sendMessage(pref+"Plugin was disabled, you are no longer a "+it+"Rainbow Warrior"+itW+".");
    		}
    	}
    	getLogger().info("Plugin was disabled!");
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
	    if(event.getSlotType().toString().equalsIgnoreCase("ARMOR")) {
	    	if(event.getCurrentItem().getType() == Material.LEATHER_HELMET
	    		|| event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE
	    		|| event.getCurrentItem().getType() == Material.LEATHER_LEGGINGS
	    		|| event.getCurrentItem().getType() == Material.LEATHER_BOOTS) {
	    		if(rainbowWarriorActive.get(event.getWhoClicked().getName().toString()) != null && rainbowWarriorActive.get(event.getWhoClicked().getName().toString()) == true) {
		    		event.setCancelled(true);
		    		event.getWhoClicked().closeInventory();
		    	}
	    	}
	    }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	
    	final Player player = (Player) sender;
    	String playerName = player.getName().toString();
    	
    	if (cmd.getName().equalsIgnoreCase("rainbowhelp")) {
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
    	}
    	if (cmd.getName().equalsIgnoreCase("rainbowreload")) {
    		if(player.hasPermission("rainbowwarrior.reload")) {
    			this.reloadConfig();
    	    	pluginEnabled = this.getConfig().getBoolean("enablePlugin");
    	    	if(!pluginEnabled) {
    	    		for(Entry<String, Boolean> playerActiveList : rainbowWarriorActive.entrySet()) {
    	        		if(playerActiveList.getValue() != null || playerActiveList.getValue()) {
    	        			Player currentPlayer = Bukkit.getPlayer(playerActiveList.getKey());
    	        			Bukkit.getScheduler().cancelTask(rainbowWarriorTaskID.get(playerActiveList.getKey()));
    	        			currentPlayer.getInventory().setHelmet(null);
    	        			currentPlayer.getInventory().setChestplate(null);
    	        			currentPlayer.getInventory().setLeggings(null);
    	        			currentPlayer.getInventory().setBoots(null);
    	        			currentPlayer.sendMessage(pref+"Plugin was disabled, you are no longer a "+it+"Rainbow Warrior"+itW+".");
    	        		}
    	        	}
    	    	}
    	    	updateRate = this.getConfig().getInt("updateRate");
    	    	usePermissions = this.getConfig().getBoolean("usePermissions");
    			sender.sendMessage(pref+it+"config.yml"+itW+" has been reloaded.");
    		} else {
    			sender.sendMessage(pref+"You're not allowed to reload the plugin.");
    		}
    	}
    	if (cmd.getName().equalsIgnoreCase("rainbowme")) {
    		if(pluginEnabled) {
	    		if(player.hasPermission("rainbowwarrior.me") || !usePermissions) {
					if(rainbowWarriorActive.get(playerName) == null || rainbowWarriorActive.get(playerName) == false) {
						if(player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null) {
	    					rainbowWarriorTaskID.put(playerName, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	        	    		    public void run() {
	        	    		    	rainbowWarrior(player);
	        	    		    }
	        	    		}, 0, updateRate));
	            			rainbowWarriorActive.put(playerName, true);
	            			player.sendMessage(pref+"You are a "+it+"Rainbow Warrior"+itW+"!");
						} else {
		    				player.sendMessage(pref+"Sorry, to become a "+it+"Rainbow Warrior"+itW+" you need to empty all your armor slots.");
		    			}
	        		} else {
	        			Bukkit.getScheduler().cancelTask(rainbowWarriorTaskID.get(playerName));
	        			rainbowWarriorActive.put(playerName, false);
	        			rainbowWarriorTaskID.put(playerName, null);
	        			player.getInventory().setHelmet(null);
	        			player.getInventory().setChestplate(null);
	        			player.getInventory().setLeggings(null);
	        			player.getInventory().setBoots(null);
	        			player.sendMessage(pref+"You are no longer a "+it+"Rainbow Warrior"+itW+"!");
	        		}
	    		} else {
	    			player.sendMessage(pref+"Sorry, you're not allowed to use "+it+"/rainbowme"+itW+".");
	    		}
    		} else {
    			sender.sendMessage(pref+"Plugin is disabled in "+it+"config.yml"+itW+".");
    		}
    	}
    	if (cmd.getName().equalsIgnoreCase("rainbowother")) {
    		if(pluginEnabled) {
	    		if(player.hasPermission("rainbowwarrior.other") || !usePermissions) {
	    			if(args.length == 1) {
	    				List<Player> matchedPlayer = Bukkit.matchPlayer(args[0]);
	    				if(matchedPlayer.size() == 1) {
	    					final Player rainbowTarget = matchedPlayer.get(0);
	    					playerName = rainbowTarget.getName().toString();
	    					if(rainbowWarriorActive.get(playerName) == null || rainbowWarriorActive.get(playerName) == false) {
								if(rainbowTarget.getInventory().getHelmet() == null && rainbowTarget.getInventory().getChestplate() == null && rainbowTarget.getInventory().getLeggings() == null && rainbowTarget.getInventory().getBoots() == null) {
			    					rainbowWarriorTaskID.put(playerName, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			        	    		    public void run() {
			        	    		    	rainbowWarrior(rainbowTarget);
			        	    		    }
			        	    		}, 0, updateRate));
			            			rainbowWarriorActive.put(playerName, true);
			            			rainbowTarget.sendMessage(pref+"You are a "+it+"Rainbow Warrior"+itW+"!");
			            			player.sendMessage(pref+"Player "+it+playerName+itW+" is now a "+it+"Rainbow Warrior"+itW+"!");
								} else {
									rainbowTarget.sendMessage(pref+"Sorry, to become a "+it+"Rainbow Warrior"+itW+" you need to empty all your armor slots.");
				    				player.sendMessage(pref+"Sorry, "+it+playerName+itW+" needs to empty his/her armor slots to become a "+it+"Rainbow Warrior"+itW+".");
				    			}
			        		} else {
			        			Bukkit.getScheduler().cancelTask(rainbowWarriorTaskID.get(playerName));
			        			rainbowWarriorActive.put(playerName, false);
			        			rainbowWarriorTaskID.put(playerName, null);
			        			rainbowTarget.getInventory().setHelmet(null);
			        			rainbowTarget.getInventory().setChestplate(null);
			        			rainbowTarget.getInventory().setLeggings(null);
			        			rainbowTarget.getInventory().setBoots(null);
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
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }    
}