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

public class Levitation extends FlavourBuff {

	public static final float DURATION	= 20f;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			target.flying = true;
			Roots.detach( target, Roots.class );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		target.flying = false;
		Dungeon.level.press( target.pos, target );
		super.detach();
	}
	
	@Override
	public int icon() {
		return BuffIndicator.LEVITATION;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.LEVITATING);
		else target.sprite.remove(CharSprite.State.LEVITATING);
	}

	@Override
	public String toString() {
		return "Levitating";
	}

	@Override
	public String desc() {
		return "A magical force is levitating you over the ground, making you feel weightless.\n" +
				"\n " +
				"While levitating you ignore all ground-based effects. Traps won't trigger, water won't put out fire, " +
				"plants won't be trampled, roots will miss you, and you will hover right over pits. " +
				"Be careful, as all these things can come into effect the second the levitation ends!\n" +
				"\n" +
				"You are levitating for " + dispTurns() + ".";
	}
}
