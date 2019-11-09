package de.JHammer.RDS.Static;

import org.bukkit.entity.Player;

import de.JHammer.RDS.Main;

/**
 * ######################################################
 * # @author JHammer17								    #
 * # Erstellt am 06.11.2019 20:25:56					#
 * #												   	#
 * # Alle Ihhalte dieser Klasse dürfen					#
 * # frei verwendet oder verbreitet werden.				#
 * # Es wird keine Zustimmung von JHammer17 benötigt.	#
 * #													#
 * ######################################################
*/

public class MoneyManager {

	
	public static void addMoney(Player p, int amount) {
		Main.ins.economy.depositPlayer(p, amount);
	}

	public static void removeMoney(Player p, int amount) {
		Main.ins.economy.withdrawPlayer(p, amount);
	}
	
	public static int getMoney(Player p) {
		return (int) Main.ins.economy.getBalance(p);
	}
	
}
