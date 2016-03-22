package com.darkania.motd;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_9_R1.util.CraftIconCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_9_R1.ChatComponentText;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.NetworkManager;
import net.minecraft.server.v1_9_R1.PacketStatusOutServerInfo;
import net.minecraft.server.v1_9_R1.ServerPing;
import net.minecraft.server.v1_9_R1.ServerPing.ServerData;
import net.minecraft.server.v1_9_R1.ServerPing.ServerPingPlayerSample;

public class MOTD extends ChannelDuplexHandler implements Listener
{
public List<?> networkManagers;

public PacketStatusOutServerInfo motd(){
List<String> list = new ArrayList<>();
list.add(ChatColor.GREEN+"Darkania");
list.add(ChatColor.WHITE+"-------------------------");
list.add(ChatColor.RED+"Estamos testiando plugins");
list.add(ChatColor.RED+"Pronto estaremos online!");
GameProfile[] sample = new GameProfile[list.size()];
for (int i = 0; i < list.size(); i++) {
sample[i] = new GameProfile(UUID.randomUUID(), (String)list.get(i));
}
ServerPingPlayerSample playerSample = new ServerPingPlayerSample(33, 2);//max,online players
playerSample.a(sample);
ServerPing ping = new ServerPing();
ping.setMOTD(new ChatComponentText(ChatColor.BLUE+"Bienvenido a Darkania\n"+ChatColor.AQUA+"Online muy pronto..."));
ping.setPlayerSample(playerSample);
ping.setServerInfo(new ServerData("Bienvenido Jugador", 107));//max,online players a String, version (-1 mantenimiento, 107 mc1.9)
ping.setFavicon(((CraftIconCache)Bukkit.getServerIcon()).value);
return new PacketStatusOutServerInfo(ping);
}


public MOTD()
{try{Field console = Bukkit.getServer().getClass().getDeclaredField("console");
console.setAccessible(true);
Method[] arrayOfMethod;
int j = (arrayOfMethod = ((MinecraftServer)console.get(Bukkit.getServer())).getServerConnection().getClass().getDeclaredMethods()).length;
for (int i = 0; i < j; i++){
Method method = arrayOfMethod[i];
method.setAccessible(true);
if (method.getReturnType() == List.class) {
this.networkManagers = Collections.synchronizedList((List<?>)method.invoke(null, new Object[] { ((MinecraftServer)console.get(Bukkit.getServer())).getServerConnection() }));
}}}catch (Exception e){e.printStackTrace();}}
@EventHandler
public void serverListPing(ServerListPingEvent event){
try{Field field = null;
Field[] arrayOfField;
int j = (arrayOfField = NetworkManager.class.getDeclaredFields()).length;
for (int i = 0; i < j; i++){
Field field2 = arrayOfField[i];
field2.setAccessible(true);
if (field2.getType() == Channel.class) {field = field2;}}
field.setAccessible(true);
for (Object manager : this.networkManagers){
Channel channel = (Channel)field.get(manager);
if ((channel.pipeline().context("ping_handler") == null) && (channel.pipeline().context("packet_handler") != null)) {
channel.pipeline().addBefore("packet_handler", "ping_handler", new MOTD());}}}catch (Exception e){e.printStackTrace();}
}
@EventHandler
public void onJoin(PlayerJoinEvent event)
{try{Field field = null;
Field[] arrayOfField;
int j = (arrayOfField = NetworkManager.class.getDeclaredFields()).length;
for (int i = 0; i < j; i++){
Field field2 = arrayOfField[i];
field2.setAccessible(true);
if (field2.getType() == Channel.class) {field = field2;}}
field.setAccessible(true);
for (Object manager : this.networkManagers){
Channel channel = (Channel)field.get(manager);
if ((channel.pipeline().context("ping_handler") == null) && (channel.pipeline().context("packet_handler") != null)) {
channel.pipeline().addBefore("packet_handler", "ping_handler", new MOTD());}}}catch (Exception e){e.printStackTrace();}
}
public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
if ((msg instanceof PacketStatusOutServerInfo)){
super.write(ctx, motd(), promise);return;}super.write(ctx, msg, promise);
}
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
{super.channelRead(ctx, msg);}
}