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
package com.sarius.multiplayer_pd.levels.traps;

import com.watabou.noosa.audio.Sample;
import com.sarius.multiplayer_pd.Assets;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.actors.mobs.Mob;
import com.sarius.multiplayer_pd.effects.CellEmitter;
import com.sarius.multiplayer_pd.effects.Speck;
import com.sarius.multiplayer_pd.utils.GLog;

public class AlarmTrap extends Trap {

	// 0xDD3333
	{
		name = "Alarm trap";
		image = 4;
	}

	@Override
	public void activate() {

		for (Mob mob : Dungeon.level.mobs) {
				mob.beckon( pos );
		}

		if (Dungeon.visible[pos]) {
			GLog.w( "The trap emits a piercing sound that echoes throughout the dungeon!" );
			CellEmitter.center( pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
		}

		Sample.INSTANCE.play( Assets.SND_ALERT );
	}
}
