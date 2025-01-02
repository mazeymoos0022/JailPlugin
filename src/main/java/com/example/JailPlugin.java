package com.example.jailplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.ChatColor;

public class JailPlugin extends JavaPlugin {
    private Location jailLocation;
    private Location unjailLocation;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadLocations();
        getLogger().info("JailPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        saveLocations();
        getLogger().info("JailPlugin has been disabled!");
    }

    private void loadLocations() {
        if (getConfig().contains("jail")) {
            World world = getServer().getWorld(getConfig().getString("jail.world"));
            double x = getConfig().getDouble("jail.x");
            double y = getConfig().getDouble("jail.y");
            double z = getConfig().getDouble("jail.z");
            float yaw = (float) getConfig().getDouble("jail.yaw");
            float pitch = (float) getConfig().getDouble("jail.pitch");
            jailLocation = new Location(world, x, y, z, yaw, pitch);
        }

        if (getConfig().contains("unjail")) {
            World world = getServer().getWorld(getConfig().getString("unjail.world"));
            double x = getConfig().getDouble("unjail.x");
            double y = getConfig().getDouble("unjail.y");
            double z = getConfig().getDouble("unjail.z");
            float yaw = (float) getConfig().getDouble("unjail.yaw");
            float pitch = (float) getConfig().getDouble("unjail.pitch");
            unjailLocation = new Location(world, x, y, z, yaw, pitch);
        }
    }

    private void saveLocations() {
        if (jailLocation != null) {
            getConfig().set("jail.world", jailLocation.getWorld().getName());
            getConfig().set("jail.x", jailLocation.getX());
            getConfig().set("jail.y", jailLocation.getY());
            getConfig().set("jail.z", jailLocation.getZ());
            getConfig().set("jail.yaw", jailLocation.getYaw());
            getConfig().set("jail.pitch", jailLocation.getPitch());
        }

        if (unjailLocation != null) {
            getConfig().set("unjail.world", unjailLocation.getWorld().getName());
            getConfig().set("unjail.x", unjailLocation.getX());
            getConfig().set("unjail.y", unjailLocation.getY());
            getConfig().set("unjail.z", unjailLocation.getZ());
            getConfig().set("unjail.yaw", unjailLocation.getYaw());
            getConfig().set("unjail.pitch", unjailLocation.getPitch());
        }

        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "setjail":
                return handleSetJail(sender, args);
            case "setunjail":
                return handleSetUnjail(sender, args);
            case "jail":
                return handleJail(sender, args);
            case "unjail":
                return handleUnjail(sender, args);
            case "jailreload":
                return handleReload(sender);
            case "jailhelp":
                return handleHelp(sender);
            default:
                return false;
        }
    }

    private boolean handleSetJail(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("jailplugin.setjail")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 3) {
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                jailLocation = new Location(player.getWorld(), x, y, z);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid coordinates! Use: /setjail <x> <y> <z>");
                return true;
            }
        } else if (args.length == 0) {
            jailLocation = player.getLocation();
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /setjail or /setjail <x> <y> <z>");
            return true;
        }

        saveLocations();
        player.sendMessage(ChatColor.GREEN + "Jail location has been set!");
        return true;
    }

    private boolean handleSetUnjail(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("jailplugin.setunjail")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (args.length == 3) {
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                unjailLocation = new Location(player.getWorld(), x, y, z);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid coordinates! Use: /setunjail <x> <y> <z>");
                return true;
            }
        } else if (args.length == 0) {
            unjailLocation = player.getLocation();
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /setunjail or /setunjail <x> <y> <z>");
            return true;
        }

        saveLocations();
        player.sendMessage(ChatColor.GREEN + "Unjail location has been set!");
        return true;
    }

    private boolean handleJail(CommandSender sender, String[] args) {
        if (!sender.hasPermission("jailplugin.jail")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (jailLocation == null) {
            sender.sendMessage(ChatColor.RED + "Jail location has not been set! Use /setjail first.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /jail <player>");
            return true;
        }

        Player target = getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        target.teleport(jailLocation);
        target.sendMessage(ChatColor.RED + "You have been jailed!");
        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been jailed!");
        return true;
    }

    private boolean handleUnjail(CommandSender sender, String[] args) {
        if (!sender.hasPermission("jailplugin.unjail")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (unjailLocation == null) {
            sender.sendMessage(ChatColor.RED + "Unjail location has not been set! Use /setunjail first.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unjail <player>");
            return true;
        }

        Player target = getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        target.teleport(unjailLocation);
        target.sendMessage(ChatColor.GREEN + "You have been released from jail!");
        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been released from jail!");
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("jailplugin.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration!");
            return true;
        }

        reloadConfig();
        loadLocations();
        sender.sendMessage(ChatColor.GREEN + "JailPlugin configuration reloaded!");
        return true;
    }

    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "JailPlugin Commands:");
        sender.sendMessage(ChatColor.GREEN + "/setjail [x y z]" + ChatColor.WHITE + " - Set the jail location.");
        sender.sendMessage(ChatColor.GREEN + "/setunjail [x y z]" + ChatColor.WHITE + " - Set the unjail location.");
        sender.sendMessage(ChatColor.GREEN + "/jail <player>" + ChatColor.WHITE + " - Jail a player.");
        sender.sendMessage(ChatColor.GREEN + "/unjail <player>" + ChatColor.WHITE + " - Unjail a player.");
        sender.sendMessage(ChatColor.GREEN + "/jailreload" + ChatColor.WHITE + " - Reload the plugin configuration.");
        sender.sendMessage(ChatColor.GREEN + "/jailhelp" + ChatColor.WHITE + " - Show this help message.");
        return true;
    }
}