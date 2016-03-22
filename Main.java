package com.darkania.motd;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin{
public void onEnable(){getServer().getPluginManager().registerEvents(new MOTD(), this);}}