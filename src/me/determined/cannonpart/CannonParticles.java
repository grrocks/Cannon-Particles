package me.determined.cannonpart;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CannonParticles extends JavaPlugin implements Listener {

	ArrayList<Location> locs = new ArrayList<Location>();
	ArrayList<Location> locsTNT = new ArrayList<Location>();
	int running = 0;
	int i = 0;

	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Runnable() {
					public void run() {
						tick();
					}
				}, 0L, 1L);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("cannonparts")) {
			if (args.length == 1) {
				try {
					long time = Integer.parseInt(args[0]);
					playParts(p, time);
				} catch (NumberFormatException e) {
					if (args[0].equalsIgnoreCase("reset")) {
						locs.clear();
						locsTNT.clear();
						p.sendMessage(ChatColor.AQUA
								+ "Reset all saved particles");
					} else if (args[0].equalsIgnoreCase("cancel")) {
						Bukkit.getScheduler().cancelTask(running);
					} else if (args[0].equalsIgnoreCase("view")) {
						cannonView(p);
					} else if (args[0].equalsIgnoreCase("normal")) {
						normalView(p);
					} else
						p.sendMessage("/cannonparts <time/reset>");
				}
			} else
				p.sendMessage("/cannonparts <time/reset>");
		}
		return false;
	}

	private void normalView(Player p) {
		int radius = 35;
		for (double x = p.getLocation().getX() - (radius); x <= p.getLocation()
				.getX() + radius; x++) {
			for (double y = p.getLocation().getY() - (radius); y <= p
					.getLocation().getY() + radius; y++) {
				for (double z = p.getLocation().getZ() - (radius); z <= p
						.getLocation().getZ() + radius; z++) {
					Location loc = new Location(p.getWorld(), x, y, z);
					Block block = loc.getBlock();
					block.getState().update();
				}
			}
		}
	}

	public void tick() {
		for (World world : getServer().getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity.getType().equals(EntityType.FALLING_BLOCK)) {
					Location loc = entity.getLocation();
					locs.add(loc);
				} else if (entity.getType().equals(EntityType.PRIMED_TNT)) {
					Location loc = entity.getLocation();
					locsTNT.add(loc);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void playParts(final Player p, final long time) {
		running = getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new BukkitRunnable() {
					public void run() {
						for (Location loc : locs) {
							ParticleEffect.CRIT_MAGIC.display(0, 0, 0, 0, 5,
									loc, p);
						}
						for (Location loc : locsTNT) {
							ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0,
									5, loc, p);
						}
						i++;
						if (i > time) {
							Bukkit.getScheduler().cancelTask(running);
							i = 0;
						}
					}
				}, 0L, 4L);
	}

	@SuppressWarnings("deprecation")
	public void cannonView(Player p) {
		int radius = 30;
		for (double x = p.getLocation().getX() - (radius); x <= p.getLocation()
				.getX() + radius; x++) {
			for (double y = p.getLocation().getY() - (radius); y <= p
					.getLocation().getY() + radius; y++) {
				for (double z = p.getLocation().getZ() - (radius); z <= p
						.getLocation().getZ() + radius; z++) {
					Location loc = new Location(p.getWorld(), x, y, z);
					Block block = loc.getBlock();
					if (block.getType() != Material.DISPENSER
							&& block.getType() != Material.PISTON_BASE
							&& block.getType() != Material.PISTON_STICKY_BASE
							&& block.getType() != Material.SAND
							&& block.getType() != Material.AIR
							&& block.getType() != Material.AIR
							&& block.getType() != Material.REDSTONE
							&& block.getType() != Material.REDSTONE_COMPARATOR
							&& block.getType() != Material.REDSTONE_COMPARATOR_OFF
							&& block.getType() != Material.REDSTONE_COMPARATOR_ON
							&& block.getType() != Material.REDSTONE_TORCH_OFF
							&& block.getType() != Material.REDSTONE_TORCH_ON
							&& block.getType() != Material.REDSTONE_WIRE
							&& block.getType() != Material.DIODE
							&& block.getType() != Material.DIODE_BLOCK_OFF
							&& block.getType() != Material.DIODE_BLOCK_ON)
						p.sendBlockChange(loc, Material.BARRIER, (byte) 0);
				}
			}
		}
	}
}
