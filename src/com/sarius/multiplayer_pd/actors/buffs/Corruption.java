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
package com.sarius.multiplayer_pd.actors.buffs;

import com.sarius.multiplayer_pd.sprites.CharSprite;
import com.sarius.multiplayer_pd.ui.BuffIndicator;

public class Corruption extends Buff {

	{
		type = buffType.NEGATIVE;
	}

	private float buildToDamage = 0f;

	@Override
	public boolean act() {
		buildToDamage += target.HT/100f;

		int damage = (int)buildToDamage;
		buildToDamage -= damage;

		if (damage > 0)
			target.damage(damage, this);

		spend(TICK);

		return true;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add( CharSprite.State.DARKENED );
		else if (target.invisible == 0) target.sprite.remove( CharSprite.State.DARKENED );
	}

	@Override
	public int icon() {
		return BuffIndicator.CORRUPT;
	}

	@Override
	public String toString() {
		return "Corrupted";
	}

	@Override
	public String desc() {
		return "Corruption seeps into the essence of a being, twisting them against their former nature.\n" +
				"\n" +
				"Corrupted creatures will attack and aggravate their allies, and ignore their former enemies. " +
				"Corruption is damaging as well, and will slowly cause its target to succumb.\n" +
				"\n" +
				"Corruption is permanent, its effects only end in death.";
	}
}
