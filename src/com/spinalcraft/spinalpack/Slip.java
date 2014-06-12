package com.spinalcraft.spinalpack;

import org.bukkit.Location;

public class Slip {
	public Location sign1, slip1, sign2, slip2;
	public int timeCreated, cooldown;
	
	public Slip(){
		sign1 = null;
		slip1 = null;
		sign2 = null;
		slip2 = null;
	}
	
	public boolean wholeSlip(){
		return sign1Valid() && sign2Valid();
	}
	
	public boolean noSlip(){
		return !(sign1Valid() || sign2Valid());
	}
	
	public boolean sign1Valid(){
		return sign1 != null;
	}
	
	public boolean sign2Valid(){
		return sign2 != null;
	}
}
