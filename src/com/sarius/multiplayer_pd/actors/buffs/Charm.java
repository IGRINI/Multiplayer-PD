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

import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.items.rings.RingOfElements.Resistance;
import com.sarius.multiplayer_pd.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Charm extends FlavourBuff {

	public int object = 0;

	private static final String OBJECT    = "object";

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( OBJECT, object );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
	}

	@Override
	public int icon() {
		return BuffIndicator.HEART;
	}
	
	@Override
	public String toString() {
		return "Charmed";
	}
	
	public static float durationFactor( Char ch ) {
		Resistance r = ch.buff( Resistance.class );
		return r != null ? r.durationFactor() : 1;
	}

	@Override
	public String desc() {
		return "A charm is manipulative magic that can make enemies temporarily adore eachother.\n" +
				"\n" +
				"Characters affected by charm are unable to directly attack the enemy they are charmed by. " +
				"Attacking other targets is still possible however.\n" +
				"\n" +
				"The charm will last for " + dispTurns() + ".";
	}
}
