/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.sarius.multiplayer_pd.items.weapon.enchantments;

import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.actors.buffs.Buff;
import com.sarius.multiplayer_pd.actors.buffs.Chill;
import com.sarius.multiplayer_pd.items.weapon.Weapon;
import com.sarius.multiplayer_pd.sprites.ItemSprite;
import com.sarius.multiplayer_pd.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Slow extends Weapon.Enchantment {

	private static final String TXT_CHILLING = "Chilling %s";
	
	private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x0044FF );
	
	@Override
	public boolean proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		// lvl 0 - 25%
		// lvl 1 - 40%
		// lvl 2 - 50%
		int level = Math.max( 0, weapon.level );
		
		if (Random.Int( level + 4 ) >= 3) {
			
			Buff.affect( defender, Chill.class,
				Random.Float( 1, 3f ) );
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Glowing glowing() {
		return BLUE;
	}
	
	@Override
	public String name( String weaponName) {
		return String.format( TXT_CHILLING, weaponName );
	}

}
