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

import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.sprites.CharSprite;
import com.sarius.multiplayer_pd.ui.BuffIndicator;

public class Light extends FlavourBuff {

	public static final float DURATION	= 250f;
	public static final int DISTANCE	= 4;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			if (Dungeon.level != null) {
				target.viewDistance = Math.max( Dungeon.level.viewDistance, DISTANCE );
				Dungeon.observe();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		target.viewDistance = Dungeon.level.viewDistance;
		Dungeon.observe();
		super.detach();
	}
	
	@Override
	public int icon() {
		return BuffIndicator.LIGHT;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.ILLUMINATED);
		else target.sprite.remove(CharSprite.State.ILLUMINATED);
	}

	@Override
	public String toString() {
		return "Illuminated";
	}

	@Override
	public String desc() {
		return "Even in the Darkest Dungeon, a steady light at your side is always comforting.\n" +
				"\n" +
				"Light helps keep darkness at bay, allowing you to see a reasonable distance despite the environment.\n" +
				"\n" +
				"The light will last for " + dispTurns() + ".";
	}
}
