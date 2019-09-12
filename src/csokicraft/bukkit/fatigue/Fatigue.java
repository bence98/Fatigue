package csokicraft.bukkit.fatigue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

public class Fatigue extends JavaPlugin{
	public static final String PERM_EXEMPT = "csokicraft.fatigue.exempt";
	public static Logger l;
	Collection<FatigueEffect> effects;
	
	@Override
	public void onLoad(){
		super.onLoad();
		saveDefaultConfig();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		super.onEnable();
		ConfigurationSerialization.registerClass(FatigueEffect.class);
		FileConfiguration conf=getConfig();
		effects=(Collection<FatigueEffect>) conf.getList("effects", new LinkedList<FatigueEffect>());
		getServer().getScheduler().scheduleSyncRepeatingTask(this, taskFatigue, 0, (int)(20*conf.getDouble("frequency")));
		l=getLogger();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length==4||args.length==5){
			int amp=0;
			if(args.length==5)
				amp=Integer.parseInt(args[4]);
			FatigueEffect fe=new FatigueEffect(Integer.parseInt(args[1]), args[2], amp, Integer.parseInt(args[3]));
			synchronized(effects){
				switch(args[0]){
				case "add":
					effects.add(fe);
					getConfig().set("effects", effects);
					saveConfig();
					l.info(sender.getName()+" added effect "+args[2]);
					sender.sendMessage(ChatColor.YELLOW+"Effect added");
					return true;
				case "remove":
					if(!effects.remove(fe))
						sender.sendMessage(ChatColor.RED+"Effect not found!");
					else{
						getConfig().set("effects", effects);
						saveConfig();
						l.info(sender.getName()+" removed effect "+args[2]);
						sender.sendMessage(ChatColor.YELLOW+"Effect removed");
					}
					return true;
				}
			}
		}
		return super.onCommand(sender, command, label, args);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
		switch(args.length){
		case 1:
			List<String> tmp=new LinkedList<>();
			StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "remove"), tmp);
			return tmp;
		case 2:
			//<after>
		case 4:
		case 5:
			//<duration> [<amplifier>]
			return Collections.singletonList("0");
		case 3:
			return Arrays.stream(PotionEffectType.values()).map((t)->{return t.getName();}).filter((s)->{return StringUtil.startsWithIgnoreCase(s, args[2]);}).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	Runnable taskFatigue=()->{
		var players=getServer().getOnlinePlayers();
		for(Player p:players)
			synchronized(effects){
				for(FatigueEffect fe:effects)
					fe.applyIfTired(p);
			}
	};
}
