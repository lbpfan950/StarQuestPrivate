package net.countercraft.movecraft.utils;

import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WarpStartTask extends BukkitRunnable{
	
	int iteration;
	Craft c;
	Player p;
	Sign s;
	
	public WarpStartTask(Craft c, Player p, Sign s){
		if(c == null){
			p.sendMessage("You aren't flying a ship.");
			return;
		}
		if(!p.getWorld().getName().equals(LocationUtils.getSystem())){
			p.sendMessage("Activating slipdrive while not in a vacuum will blow your starship to subatomic particles.");
			return;
		}
		boolean success = takeFuel(s);
		if(!success){ p.sendMessage("No catalyst found."); return;}
		this.c = c;
		this.p = p;
		this.s = s;
		iteration = 6;
		this.runTaskTimer(Movecraft.getInstance(), 0, 60);
	}
	
	public void run(){
		s.getWorld().playSound(s.getLocation(), Sound.FIZZ, 1.0F, 1.0F);
		switch(iteration){
		case 0:
			if(p != null){
				if(CraftManager.getInstance().getCraftByPlayer(p) != null){
					
					s.setLine(2, ChatColor.BLUE + "Stable Slip.");
					s.update();
					p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
					
					WarpUtils.enterWarp(p, CraftManager.getInstance().getCraftByPlayer(p));
					p.sendMessage(ChatColor.AQUA +"succesfully entered the slip and in stable travel. To navigate, fly just like your normally do.");
				}
			}
			this.cancel();
			return;
		case 1:
			if(p != null) p.sendMessage(ChatColor.AQUA + "Energizing..."); iteration--; return;
		case 2:
			if(p != null) p.sendMessage(ChatColor.AQUA +"Capacitors charged, calibrating energization ray..."); iteration--; return;
		case 3:
			if(p != null) p.sendMessage(ChatColor.AQUA +"Flux field generated. Charging capacitors for energization..."); iteration--; return;
		case 4:
			if(p != null) p.sendMessage(ChatColor.AQUA +"Catalyst liquified and stable, Spinning up flux generator..."); iteration--; return;
		case 5:
			if(p != null) p.sendMessage(ChatColor.AQUA +"Beginning catalyist liquification process..."); iteration--; return;
		case 6:
			if(p != null) p.sendMessage(ChatColor.AQUA +"Initializing slipdrive..."); iteration--;
			s.setLine(2, ChatColor.AQUA + "Starting up...");
			s.update();
			return;
		}
	}
	
	private boolean takeFuel(Sign s){
		BlockFace dir = DirectionUtils.getGateDirection(s.getBlock());
		Block dia = s.getBlock().getRelative(dir);
		if(dia.getType() != Material.DIAMOND_BLOCK) return false;
		Block hopperLeft = dia.getRelative(DirectionUtils.getBlockFaceLeft(dir));
		if(hopperLeft.getType() != Material.HOPPER) return false;
		Block hopperRight = dia.getRelative(DirectionUtils.getBlockFaceRight(dir));
		if(hopperRight.getType() != Material.HOPPER) return false;
		
		Hopper hpr1 = (Hopper) hopperLeft.getState();
		Hopper hpr2 = (Hopper) hopperRight.getState();
		
		Inventory i = hpr1.getInventory();
		Inventory i2 = hpr2.getInventory();
		
		if(i.contains(Material.EYE_OF_ENDER)) {
			ItemStack iStack=i.getItem(i.first(Material.EYE_OF_ENDER));
			int amount=iStack.getAmount();
			if(amount==1) {
				i.clear();
			} else {
				iStack.setAmount(amount-1);
			}
			return true;
			
		} else if(i2.contains(Material.EYE_OF_ENDER)){
			ItemStack iStack= i2.getItem(i2.first(Material.EYE_OF_ENDER));
			int amount=iStack.getAmount();
			if(amount==1) {
				i.clear();
			} else {
				iStack.setAmount(amount-1);
			}
			return true;
		} else {
			return false;
		}
	}
}
