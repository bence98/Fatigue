package csokicraft.bukkit.fatigue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FatigueEffect implements ConfigurationSerializable{
	private final @Nonnull PotionEffect effect;
	private final int delay;
	
	public FatigueEffect(int after, String potionId, int strength, int duration){
		effect=new PotionEffect(PotionEffectType.getByName(potionId), duration, strength);
		delay=after;
	}
	
	@SuppressWarnings("unchecked")
	public FatigueEffect(Map<String, Object> conf){
		delay=(Integer)conf.get("after");
		effect=new PotionEffect((Map<String, Object>) conf.get("effect"));
	}
	
	public static FatigueEffect deserialize(@Nonnull Map<String, Object> m) {
		return new FatigueEffect(m);
	}
	
	public static FatigueEffect valueOf(Map<String, Object> m){
		return new FatigueEffect(m);
	}
	
	public void applyIfTired(Player p){
		if(!p.hasPermission(Fatigue.PERM_EXEMPT)&&p.getStatistic(Statistic.TIME_SINCE_REST)>delay)
			p.addPotionEffect(effect);
	}

	@Override
	public Map<String, Object> serialize(){
		Map<String, Object> m=new HashMap<>();
		m.put("after", delay);
		m.put("effect", effect.serialize());
		return m;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof FatigueEffect){
			FatigueEffect eff=(FatigueEffect) obj;
			return eff.delay==delay && eff.effect.equals(effect);
		}
		return false;
	}
}
