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
package com.sarius.multiplayer_pd.actors.mobs;

import com.sarius.multiplayer_pd.Badges;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.actors.buffs.Bleeding;
import com.sarius.multiplayer_pd.actors.buffs.Blindness;
import com.sarius.multiplayer_pd.actors.buffs.Buff;
import com.sarius.multiplayer_pd.actors.buffs.Cripple;
import com.sarius.multiplayer_pd.actors.buffs.Poison;
import com.sarius.multiplayer_pd.actors.hero.Hero;
import com.sarius.multiplayer_pd.items.Item;
import com.sarius.multiplayer_pd.sprites.BanditSprite;
import com.watabou.utils.Random;

public class Bandit extends Thief {
	
	public Item item;
	
	{
		name = "crazy bandit";
		spriteClass = BanditSprite.class;

		//1 in 30 chance to be a crazy bandit, equates to overall 1/90 chance.
		lootChance = 0.333f;
	}
	
	@Override
	protected boolean steal( Hero hero ) {
		if (super.steal( hero )) {
			
			Buff.prolong( hero, Blindness.class, Random.Int( 5, 12 ) );
			Buff.affect( hero, Poison.class ).set(Random.Int(5, 7) * Poison.durationFactor(enemy));
			Buff.prolong( hero, Cripple.class, Cripple.DURATION );
			Dungeon.observe();
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void die( Object cause ) {
		super.die( cause );
		Badges.validateRare( this );
	}
}
