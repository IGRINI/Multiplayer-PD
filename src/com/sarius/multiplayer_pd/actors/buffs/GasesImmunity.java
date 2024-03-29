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

import com.sarius.multiplayer_pd.actors.blobs.ConfusionGas;
import com.sarius.multiplayer_pd.actors.blobs.ParalyticGas;
import com.sarius.multiplayer_pd.actors.blobs.StenchGas;
import com.sarius.multiplayer_pd.actors.blobs.ToxicGas;
import com.sarius.multiplayer_pd.actors.blobs.VenomGas;
import com.sarius.multiplayer_pd.ui.BuffIndicator;

public class GasesImmunity extends FlavourBuff {
	
	public static final float DURATION	= 15f;
	
	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}
	
	@Override
	public String toString() {
		return "Immune to gases";
	}

	{
		immunities.add( ParalyticGas.class );
		immunities.add( ToxicGas.class );
		immunities.add( ConfusionGas.class );
		immunities.add( StenchGas.class );
		immunities.add( VenomGas.class );
	}

	@Override
	public String desc() {
		return "some strange force is filtering out the air around you, it's not causing you any harm, but it blocks " +
				"out everything but air so effectively you can't even smell anything!\n" +
				"\n" +
				"You are immune to the effects of all gasses while this buff lasts.\n" +
				"\n" +
				"You will be immune for " + dispTurns() + ".";
	}
}
