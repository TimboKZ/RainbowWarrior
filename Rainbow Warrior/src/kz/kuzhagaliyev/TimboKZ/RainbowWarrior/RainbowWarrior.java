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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public final class RainbowWarrior extends JavaPlugin implements Listener {
	
	String cBold = ChatColor.BOLD+"";
	String cGray = ChatColor.GRAY + "";
	String cRed = ChatColor.RED + "";
	String cYellow = ChatColor.YELLOW + "";
	String cPurple = ChatColor.LIGHT_PURPLE + "";
	String cBlack = ChatColor.BLACK + "";
	String cAqua = ChatColor.AQUA + "";
	String cReset = ChatColor.RESET + "";
	
	String rainbowString = ChatColor.AQUA + "R" + ChatColor.DARK_AQUA + "a" + ChatColor.DARK_AQUA + "i" + ChatColor.BLUE + "n" + ChatColor.BLUE + "b" + ChatColor.LIGHT_PURPLE + "o" + ChatColor.RED +  "w";
	String warriorString = ChatColor.RED + "W" + ChatColor.GOLD + "a" + ChatColor.GOLD + "r" + ChatColor.YELLOW + "r" + ChatColor.YELLOW + "i" + ChatColor.AQUA + "o" + ChatColor.DARK_AQUA + "r";
	String rainbowWarrior = rainbowString + " " + warriorString;
	String pref = cBlack + cBold + "[" + cReset + rainbowWarrior + cBlack + cBold + "] " + cReset;
	String permPref = "rainbowwarrior";
    Random rand = new Random();
	boolean pluginEnabled = true;
	int updateRate = 5;
	boolean usePermissions = true;
	
	public Integer rainbowTaskID = null;
	
	public List<String> rainbowWarriorActive = new ArrayList<String>();
	public HashMap<String, String> rainbowRainbow = new HashMap<String, String>();
	public HashMap<String, String> rainbowBlink = new HashMap<String, String>();
	public List<String> rainbowRandom = new ArrayList<String>();
	
	public HashMap<String, String> colors = new HashMap<String, String>();
	public String supportedColors = "";
	
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	
    @Override
    public void onEnable() {
    	
    	colors.put("white", "255,255,255");
    	colors.put("red", "255,0,0");
    	colors.put("orange", "255,127,0");
    	colors.put("yellow", "255,255,0");
    	colors.put("green", "0,255,0");
    	colors.put("lightblue", "173,216,230");
    	colors.put("blue", "0,0,255");
    	colors.put("indigo", "75,0,130");
    	colors.put("violet", "143,0,255");
    	colors.put("black", "0,0,0");
    	
    	int count = 0;
    	for(String color : colors.keySet()) {
    		if(count % 2 == 0) {
    			supportedColors += ChatColor.AQUA;
    		} else {
    			supportedColors += ChatColor.YELLOW;
    		}
    		supportedColors += " " + color;
    		count++;
    	}
    	
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
    	
    	rainbowRestart();
    }
    @Override
    public void onDisable() {
    	List<String> activePlayers = rainbowWarriorActive;
    	activePlayers.addAll(activePlayers);
    	for(String playerName : activePlayers) {
    		Player player = Bukkit.getPlayer(playerName);
    		if(player != null) {
    			rainbowCancel(player, "disabled");
    		}
    	}
    	Bukkit.getScheduler().cancelTask(rainbowTaskID);
    	rainbowWarriorActive.clear();
    	rainbowRainbow.clear();
    	rainbowBlink.clear();
    	rainbowRandom.clear();
    } 
    @EventHandler
    public void onPlayerQuite(PlayerQuitEvent event) {
    	if(rainbowWarriorActive.contains(event.getPlayer().getName())) {
    		rainbowCancel(event.getPlayer(), "silent");
    	}
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
	    if(event.getSlotType().equals(SlotType.ARMOR)) {
    		if(rainbowWarriorActive.contains(event.getWhoClicked().getName())) {
	    		event.setCancelled(true);
	    		event.getWhoClicked().closeInventory();
	    	}
	    }
	    if((event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getLore() != null && event.getCurrentItem().getItemMeta().getLore().size() == 1 && event.getCurrentItem().getItemMeta().getLore().get(0).equalsIgnoreCase(rainbowWarrior + cReset + " Leather Armor"))
	    	|| (event.getCursor() != null && event.getCursor().getItemMeta() != null && event.getCursor().getItemMeta().getLore() != null && event.getCursor().getItemMeta().getLore().size() == 1 && event.getCursor().getItemMeta().getLore().get(0).equalsIgnoreCase(rainbowWarrior + cReset + " Leather Armor"))) {
			event.setCancelled(true);
    		event.getWhoClicked().closeInventory();
		}
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	Player player = (Player) event.getEntity();
    	if(rainbowWarriorActive.contains(event.getEntity().getName())) {
    		ItemStack[] playerArmor = player.getInventory().getArmorContents();
    		rainbowCancel(player, "death");
    		for(ItemStack drop : playerArmor) {
    			if(drop != null && drop.getItemMeta() != null && drop.getItemMeta().getLore() != null && drop.getItemMeta().getLore().get(0) != null && drop.getItemMeta().getLore().get(0).equalsIgnoreCase(rainbowWarrior + cReset + " Leather Armor")) {
    				event.getDrops().remove(drop);
    			}
    		}
    	}
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = null;
    	String playerName = "Console";
    	if(sender instanceof Player) {
    		player = (Player) sender;
        	playerName = player.getName();
    	}
    	if(label.equalsIgnoreCase("rainbow")) {
    		if(args.length == 0 || (args.length >= 1 && args[0].equalsIgnoreCase("help"))) {
    			if(player == null || player.hasPermission(permPref + ".help") || !usePermissions) {
    				if(!pluginEnabled) {
    					sender.sendMessage(pref + "Plugin is " + cRed + "disabled" + cReset + " in " + cAqua + "config.yml" + cReset + ".");
    					sender.sendMessage(pref + "Use " + cAqua + "/rainbow reload" + cReset + " to reload the " + cAqua + "plugin" + cReset + ".");
    					return true;
    				}
    				sender.sendMessage(pref + "Plugin's help section " + cGray + "===============" + cReset);
		    		sender.sendMessage(cYellow + "/rainbow" + cRed + " ... ");
		    		sender.sendMessage(cRed + "... " + cAqua + "help [Page]" + cReset + " Displays specified page of help section.");
		    		sender.sendMessage(cRed + "... " + cAqua + "reload" + cReset + " Reloads the plugin.");
		    		sender.sendMessage(cYellow + "/rainbow me" + cRed + " ..." + cRed + " OR " + cYellow + "/rainbow other <Player>" + cRed + " ...");
		    		sender.sendMessage(cRed + "... " + cAqua + "rainbow" + cReset + " Cycles through rainbow colors.");
		    		sender.sendMessage(cRed + "... " + cAqua + "random" + cReset + " Cycles through random colors.");
		    		sender.sendMessage(cRed + "... " + cAqua + "blink " + cYellow + "<Color #1> " + cYellow + "<Color #2>" + cReset + " Cycles through the specified colors.");
		    		sender.sendMessage(cRed + "... " + cAqua + "stop" + cReset + " Removes the effect of Rainbow Warrior.");
		    		sender.sendMessage("Supported colors:" + supportedColors + ".");
		    		sender.sendMessage(cGray + "================================================" + cReset);
    			} else {
    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to access " + cAqua + "help" + cReset + " section.");
    			}
    		} else if(args[0].equalsIgnoreCase("me")) {
    			if(!pluginEnabled) {
					sender.sendMessage(pref + "Plugin is " + cRed + "disabled" + cReset + " in " + cAqua + "config.yml" + cReset + ".");
					return true;
				}
    			if(player != null) {
    				if(args.length >= 2) {
        				if(args[1].equalsIgnoreCase("random")) {
        					if(player.hasPermission(permPref + ".me.random") || !usePermissions) {
        						rainbowCancel(player, "silent");
        						if(player.getInventory().getHelmet() != null || player.getInventory().getChestplate() != null || player.getInventory().getLeggings() != null || player.getInventory().getBoots() != null) {
        							player.sendMessage(pref + "You need to empty your " + cYellow + "armour slots" + cReset + " to become a " + rainbowWarrior + ".");
        							return true;
        						}
        						rainbowWarriorActive.add(playerName);
        						rainbowRandom.add(playerName);
        						player.sendMessage(pref + "You are now a " + rainbowWarrior + "! " + cGray + "(Random)");
        						if(args.length > 2) {
        							player.sendMessage(pref + "By the way, " + cAqua + "/rainbow " + cYellow + "..." + cAqua + " random" + cReset + "doesn't require any arguments.");
        						}
        	    			} else {
        	    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "me" + cAqua + " random" + cReset + " command.");
        	    			}
        				} else if(args[1].equalsIgnoreCase("rainbow")) {
        					if(player.hasPermission(permPref + ".me.rainbow") || !usePermissions) {
        						rainbowCancel(player, "silent");
        						if(player.getInventory().getHelmet() != null || player.getInventory().getChestplate() != null || player.getInventory().getLeggings() != null || player.getInventory().getBoots() != null) {
        							player.sendMessage(pref + "You need to empty your " + cYellow + "armour slots" + cReset + " to become a " + rainbowWarrior + ".");
        							return true;
        						}
        						rainbowWarriorActive.add(playerName);
        						rainbowRainbow.put(playerName, "red");
        						player.sendMessage(pref + "You are now a " + rainbowWarrior + cReset + "! " + cGray + "(Rainbow)");
        						if(args.length > 2) {
        							player.sendMessage(pref + "By the way, " + cAqua + "/rainbow " + cYellow + "..." + cAqua + " rainbow" + cReset + "doesn't require any arguments.");
        						}
        	    			} else {
        	    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "me" + cAqua + " rainbow" + cReset + " command.");
        	    			}
        				} else if(args[1].equalsIgnoreCase("blink")) {
        					if(player.hasPermission(permPref + ".me.blink") || !usePermissions) {
        						rainbowCancel(player, "silent");
        						if(player.getInventory().getHelmet() != null || player.getInventory().getChestplate() != null || player.getInventory().getLeggings() != null || player.getInventory().getBoots() != null) {
        							player.sendMessage(pref + "You need to empty your " + cYellow + "armour slots" + cReset + " to become a " + rainbowWarrior + ".");
        							return true;
        						}
	        					if(args.length == 4) {
	        						Color color1 = getColor(args[2]);
	        						Color color2 = getColor(args[3]);
	        						if(color1 != null) {
	        							if(color2 != null) {
	        								rainbowWarriorActive.add(playerName);
	        								rainbowBlink.put(playerName, args[2] + "," + args[2] + "," + args[3]);
	        								player.sendMessage(pref + "You are now a " + rainbowWarrior + cReset + "! " + cGray + "(Blink)");
	            						} else {
	            							player.sendMessage(pref + cAqua + "Color #2 " + cGray + args[3] + cReset + " is " + cRed + "not" + cReset + " supported.");
		        							player.sendMessage(pref + "Supported colors:" + supportedColors + ".");
	            						}
	        						} else {
	        							player.sendMessage(pref + cAqua + "Color #1 " + cGray + args[2] + cReset + " is " + cRed + "not" + cReset + " supported.");
	        							player.sendMessage(pref + "Supported colors:" + supportedColors + ".");
	        						}
	        					} else {
	        						player.sendMessage(pref + "You need to specify 2 colors: " + cAqua + "/rainbow me blink " + cYellow + "Color#1" + cYellow + " Color#2" + cReset + ".");
	        						player.sendMessage(pref + "Supported colors:" + supportedColors + ".");
	        					}
	        				} else {
	    	    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "me" + cAqua + " blink" + cReset + " command.");
	    	    			}
        				} else if(args[1].equalsIgnoreCase("stop")) {
        					if(player.hasPermission(permPref + ".me.stop") || !usePermissions) {
        						rainbowCancel(player, "stopCommand");
        					}
        				} else {
        					player.sendMessage(pref + "Unrecognised command: " + cAqua + "/rainbow " + cYellow + "me " + cRed + args[1] + cReset + ".");
        					player.sendMessage(pref + "Use " + cYellow + "/rainbow help" + cReset + " for the list of commands.");
        				}
        			} else {
        				player.sendMessage(pref + cAqua + "/rainbow " + cYellow + "me" + cReset + " is not enough.");
        				player.sendMessage(pref + "Use " + cYellow + "/rainbow help" + cReset + " for the list of commands.");
        			}
    			} else {
    				sender.sendMessage(pref + "Sorry, only " + cRed + "players" + cReset + " can use " + cAqua + "/rainbow " + cYellow + "me" + cReset + ".");
    			}
    		} else if(args[0].equalsIgnoreCase("other")) {
    			if(!pluginEnabled) {
					sender.sendMessage(pref + "Plugin is " + cRed + "disabled" + cReset + " in " + cAqua + "config.yml" + cReset + ".");
					return true;
				}
    			if(args.length == 1) {
    				sender.sendMessage(pref + cAqua + "/rainbow " + cYellow + "other" + cReset + " is not enough.");
    				sender.sendMessage(pref + "Use " + cYellow + "/rainbow help" + cReset + " for the list of commands.");
    				return true;
    			}
    			Player target = Bukkit.getPlayer(args[1]);
    			if(target != null) {
    				String targetName = target.getName();
    				if(args.length >= 3) {
        				if(args[1].equalsIgnoreCase("random")) {
        					if(player == null || player.hasPermission(permPref + ".other.random") || !usePermissions) {
        						rainbowCancel(target, "silent");
        						if(target.getInventory().getHelmet() != null || target.getInventory().getChestplate() != null || target.getInventory().getLeggings() != null || target.getInventory().getBoots() != null) {
        							sender.sendMessage(pref + targetName + " needs to empty his/her " + cYellow + "armour slots" + cReset + " to become a " + rainbowWarrior + ".");
        							return true;
        						}
        						rainbowWarriorActive.add(targetName);
        						rainbowRandom.add(targetName);
        						target.sendMessage(pref + "You are now a " + rainbowWarrior + cReset + "! " + cGray + "(Random)");
								if(target != player) {
									sender.sendMessage(pref + cAqua + targetName + cReset + " is now a " + rainbowWarrior + cReset + "! " + cGray + "(Random)");
								} else if(player != null) {
									player.sendMessage(pref + "Next time you can just use " + cAqua + "/rainbow " + cYellow + "me" + cReset + ".");
								}
        						if(args.length > 3) {
        							sender.sendMessage(pref + "By the way, " + cAqua + "/rainbow " + cYellow + "..." + cAqua + " random " + cReset + "doesn't require any arguments.");
        						}
        	    			} else {
        	    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "other " + cRed + "..." + cAqua + " random" + cReset + " command.");
        	    			}
        				} else if(args[2].equalsIgnoreCase("rainbow")) {
        					if(player == null || player.hasPermission(permPref + ".other.rainbow") || !usePermissions) {
        						rainbowCancel(target, "silent");
        						if(target.getInventory().getHelmet() != null || target.getInventory().getChestplate() != null || target.getInventory().getLeggings() != null || target.getInventory().getBoots() != null) {
        							sender.sendMessage(pref + targetName + " needs to empty his/her " + cYellow + "armour slots" + cReset + " to become a " + rainbowWarrior + ".");
        							return true;
        						}
        						rainbowWarriorActive.add(targetName);
        						rainbowRainbow.put(targetName, "red");
        						target.sendMessage(pref + "You are now a " + rainbowWarrior + cReset + "! " + cGray + "(Rainbow)");
								if(target != player) {
									sender.sendMessage(pref + cAqua + targetName + cReset + " is now a " + rainbowWarrior + cReset + "! " + cGray + "(Rainbow)");
								} else if(player != null) {
									player.sendMessage(pref + "Next time you can just use " + cAqua + "/rainbow " + cYellow + "me" + cReset + ".");
								}
        						if(args.length > 3) {
        							sender.sendMessage(pref + "By the way, " + cAqua + "/rainbow " + cYellow + "..." + cAqua + " rainbow" + cReset + "doesn't require any arguments.");
        						}
        	    			} else {
        	    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "other " + cRed + "..." + cAqua + " rainbow " + cReset + " command.");
        	    			}
        				} else if(args[2].equalsIgnoreCase("blink")) {
        					if(player == null || player.hasPermission(permPref + ".other.blink") || !usePermissions) {
        						rainbowCancel(target, "silent");
        						if(target.getInventory().getHelmet() != null || target.getInventory().getChestplate() != null || target.getInventory().getLeggings() != null || target.getInventory().getBoots() != null) {
        							sender.sendMessage(pref + targetName + " needs to empty his/her " + cYellow + "armour slots" + cReset + " to become a " + rainbowWarrior + ".");
        							return true;
        						}
	        					if(args.length == 5) {
	        						Color color1 = getColor(args[3]);
	        						Color color2 = getColor(args[4]);
	        						if(color1 != null) {
	        							if(color2 != null) {
	        								rainbowWarriorActive.add(targetName);
	        								rainbowBlink.put(targetName, args[3] + "," + args[3] + "," + args[4]);
	        								target.sendMessage(pref + "You are now a " + rainbowWarrior + cReset + "! " + cGray + "(Blink)");
	        								if(target != player) {
	        									sender.sendMessage(pref + cAqua + targetName + cReset + " is now a " + rainbowWarrior + cReset + "! " + cGray + "(Blink)");
	        								} else if(player != null) {
	        									player.sendMessage(pref + "Next time you can just use " + cAqua + "/rainbow " + cYellow + "me" + cReset + ".");
	        								}
	            						} else {
	            							sender.sendMessage(pref + cAqua + "Color #2 " + cGray + args[4] + cReset + " is " + cRed + "not" + cReset + " supported.");
	            							sender.sendMessage(pref + "Supported colors:" + supportedColors + ".");
	            						}
	        						} else {
	        							sender.sendMessage(pref + cAqua + "Color #1 " + cGray + args[3] + cReset + " is " + cRed + "not" + cReset + " supported.");
	        							sender.sendMessage(pref + "Supported colors:" + supportedColors + ".");
	        						}
	        					} else {
	        						sender.sendMessage(pref + "You need to specify 2 colors: " + cAqua + "/rainbow me blink " + cYellow + "Color#1" + cYellow + " Color#2" + cReset + ".");
	        						sender.sendMessage(pref + "Supported colors:" + supportedColors + ".");
	        					}
	        				} else {
	    	    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "other " + cRed + "..." + cAqua + " blink" + cReset + " command.");
	    	    			}
        				} else if(args[2].equalsIgnoreCase("stop")) {
        					if(player == null || player.hasPermission(permPref + ".other.stop") || !usePermissions) {
	        					rainbowCancel(target, "stopCommand");
	        					player.sendMessage(pref + cAqua + targetName + cReset + " is no longer a " + rainbowWarrior + cReset + ".");
        					} else {
	    	    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "other " + cRed + "..." + cAqua + " stop" + cReset + " command.");
	    	    			}
        				} else {
        					sender.sendMessage(pref + "Unrecognised command: " + cAqua + "/rainbow " + cYellow + "other " + args[1] + " " + cRed + args[2] + cReset + ".");
        					sender.sendMessage(pref + "Use " + cYellow + "/rainbow help" + cReset + " for the list of commands.");
        				}
        			} else {
        				sender.sendMessage(pref + cAqua + "/rainbow " + cYellow + "other " + targetName + cReset + " is not enough.");
        				sender.sendMessage(pref + "Use " + cYellow + "/rainbow help" + cReset + " for the list of commands.");
        			}
    			} else {
    				sender.sendMessage(pref + "Unregocnized player: " + cRed + args[1] + cReset + ". Watch out for typos.");
    			}
    		} else if(args[0].equalsIgnoreCase("reload")) {
    			if(player == null || player.hasPermission(permPref + ".reload")) {
    				sender.sendMessage(pref + cAqua + "Plugin " + cGray + "(config.yml)" + cReset + " has been reloaded.");
    				rainbowRestart();
    			} else {
    				player.sendMessage(pref + "You're " + cRed + "not" + cReset + " allowed to use " + cAqua + "/rainbow " + cYellow + "reload" + cReset + " command.");
    			}
    		} else {
    			sender.sendMessage(pref + "Unrecognised command: " + cAqua + "/rainbow " + cRed + args[0] + cReset + ".");
    			sender.sendMessage(pref + "Use " + cYellow + "/rainbow help" + cReset + " for the list of commands.");
    		}
    	}
    	return true;
    }
    public void rainbowCancel(Player player, String reason) {
    	String playerName = player.getName();
    	rainbowWarriorActive.remove(playerName);
		rainbowRainbow.remove(playerName);
		rainbowRandom.remove(playerName);
		rainbowBlink.remove(playerName);
		player.getInventory().setArmorContents(null);
		if(reason.equalsIgnoreCase("stopCommand")) {
			player.sendMessage(pref + "You're no longer a " + rainbowWarrior + cReset + ".");
		} else if(reason.equalsIgnoreCase("disabled")) {
			player.sendMessage(pref + "Plugin was disabled, you're no longer a " + rainbowWarrior + cReset + ".");
		} else if(reason.equalsIgnoreCase("death")) {
			player.sendMessage(pref + "You died, so you are no longer a " + rainbowWarrior + cReset + ".");
		}
    }
    public void rainbowRestart() {
    	if(rainbowTaskID != null) {
    		Bukkit.getScheduler().cancelTask(rainbowTaskID);
    	}
    	this.reloadConfig();
    	pluginEnabled = this.getConfig().getBoolean("enablePlugin");
    	if(!pluginEnabled) {
    		for(String playerName : rainbowWarriorActive) {
        		Player player = Bukkit.getPlayer(playerName);
        		if(player != null) {
        			rainbowCancel(player, "disabled");
        		}
        	}
        	rainbowWarriorActive.clear();
        	rainbowRainbow.clear();
        	rainbowBlink.clear();
        	rainbowRandom.clear();
    	}
    	updateRate = this.getConfig().getInt("updateRate");
    	usePermissions = this.getConfig().getBoolean("usePermissions");
    	rainbowTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    public void run() {
		    	rainbowWarrior();
		    }
		}, 0, updateRate);
    }
    public void rainbowWarrior() {
    	for(String playerName : rainbowRandom) {
    		Player player = Bukkit.getPlayer(playerName);
    		if(player != null) {
    			setArmorColor(player,
					Color.fromRGB(randInt(0, 255), randInt(0, 255), randInt(0, 255)),
					Color.fromRGB(randInt(0, 255), randInt(0, 255), randInt(0, 255)),
					Color.fromRGB(randInt(0, 255), randInt(0, 255), randInt(0, 255)),
					Color.fromRGB(randInt(0, 255), randInt(0, 255), randInt(0, 255))
    			);
    		}
    	}
    	for(String playerName : rainbowBlink.keySet()) {
    		Player player = Bukkit.getPlayer(playerName);
    		if(player != null) {
    			String RGB = rainbowBlink.get(playerName);
    			String[] RGBArray = RGB.split(",");
    			String color;
    			if(RGBArray[0].equalsIgnoreCase(RGBArray[1])) {
    				color = RGBArray[2];
    			} else {
    				color = RGBArray[1];
    			}
    			setArmorColor(player,
    				getColor(color),
    				getColor(color),
    				getColor(color),
    				getColor(color)
    			);
    			rainbowBlink.put(playerName, color + "," + RGBArray[1] + "," + RGBArray[2]);
    		}
    	}
    	for(String playerName : rainbowRainbow.keySet()) {
    		Player player = Bukkit.getPlayer(playerName);
    		if(player != null) {
    			String color = rainbowRainbow.get(playerName);
    			color = nextColor(color);
    			setArmorColor(player,
    				getColor(color),
    				getColor(color),
    				getColor(color),
    				getColor(color)
    			);
    			rainbowRainbow.put(playerName, color);
    		}
    	}
    }
    public void setArmorColor(Player player, Color helmetColor, Color chestplateColor, Color leggingsColor, Color bootsColor) {
    	PlayerInventory playerInventory = player.getInventory();
    	
    	List<String> lore = new ArrayList<String>();
    	lore.add(rainbowWarrior + cReset + " Leather Armor");
    	
    	ItemStack lhelmet = new ItemStack(Material.LEATHER_HELMET, 1);
    	LeatherArmorMeta lam = (LeatherArmorMeta)lhelmet.getItemMeta();
    	lam.setColor(helmetColor);
    	lam.setLore(lore);
    	lhelmet.setItemMeta(lam);
    	playerInventory.setHelmet(lhelmet);
    	
    	ItemStack lchestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
    	lam.setColor(chestplateColor);
    	lchestplate.setItemMeta(lam);
    	playerInventory.setChestplate(lchestplate);
    	
    	ItemStack lleggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
    	lam.setColor(leggingsColor);
    	lleggings.setItemMeta(lam);
    	playerInventory.setLeggings(lleggings);
    	
    	ItemStack lboots = new ItemStack(Material.LEATHER_BOOTS, 1);
    	lam.setColor(bootsColor);
    	lboots.setItemMeta(lam);
    	playerInventory.setBoots(lboots);
    }
    public int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }
    public Color getColor(String color) {
    	if(colors.containsKey(color)) {
    		String RGB = colors.get(color);
    		String[] RGBArray = RGB.split(",");
    		return Color.fromRGB(Integer.parseInt(RGBArray[0]), Integer.parseInt(RGBArray[1]), Integer.parseInt(RGBArray[2]));
    	}
    	return null;
    }
    public String nextColor(String color) {
    	if(color.equalsIgnoreCase("red")) return "orange";
    	else if(color.equalsIgnoreCase("orange")) return "yellow";
    	else if(color.equalsIgnoreCase("yellow")) return "green";
    	else if(color.equalsIgnoreCase("green")) return "blue";
    	else if(color.equalsIgnoreCase("blue")) return "indigo";
    	else if(color.equalsIgnoreCase("indigo")) return "violet";
    	else if(color.equalsIgnoreCase("violet")) return "red";
    	else return "red";
    }
}