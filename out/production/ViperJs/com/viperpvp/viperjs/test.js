/**
 * Created by Matt on 15/05/2016.
 */
var PlayerJoinEvent = Java.type("org.bukkit.event.player.PlayerJoinEvent");
var ChatColor = Java.type("org.bukkit.ChatColor");

viper.registerEvent(PlayerJoinEvent.class, function(event) {
   event.setJoinMessage(ChatColor.RED + "Welcome to ViperPvP, running on ViperJs!");
});

viper.registerCommand("hello", function(player){
    player.sendMessage(ChatColor.GREEN + "Hello");
});