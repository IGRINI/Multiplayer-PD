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
package com.sarius.multiplayer_pd.plants;

import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.actors.Actor;
import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.actors.buffs.Bless;
import com.sarius.multiplayer_pd.actors.buffs.Buff;
import com.sarius.multiplayer_pd.items.potions.PotionOfExperience;
import com.sarius.multiplayer_pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Starflower extends Plant {

	private static final String TXT_DESC =
			"An extremely rare plant, " +
			"Starflower is said to grant holy power to whomever touches it.";

	{
		image = 11;
		plantName = "Starflower";
	}

	@Override
	public void activate() {
		Char ch = Actor.findChar(pos);

		if (ch != null) Buff.prolong(ch, Bless.class, 30f);

		if (Random.Int(5) == 0){
			Dungeon.level.drop(new Seed(), pos).sprite.drop();
		}
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed{

		{
			plantName = "Starflower";

			name = "Seed of " + plantName;
			image = ItemSpriteSheet.SEED_STARFLOWER;

			plantClass = Starflower.class;
			alchemyClass = PotionOfExperience.class;
		}

		@Override
		public String desc() {
			return TXT_DESC;
		}
	}
}
