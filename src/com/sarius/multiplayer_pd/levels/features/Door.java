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
package com.sarius.multiplayer_pd.levels.features;

import com.watabou.noosa.audio.Sample;
import com.sarius.multiplayer_pd.Assets;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.levels.Level;
import com.sarius.multiplayer_pd.levels.Terrain;
import com.sarius.multiplayer_pd.scenes.GameScene;

public class Door {

	public static void enter( int pos ) {
		Level.set( pos, Terrain.OPEN_DOOR );
		GameScene.updateMap( pos );
		Dungeon.observe();
		
		if (Dungeon.visible[pos]) {
			Sample.INSTANCE.play( Assets.SND_OPEN );
		}
	}
	
	public static void leave( int pos ) {
		if (Dungeon.level.heaps.get( pos ) == null) {
			Level.set( pos, Terrain.DOOR );
			GameScene.updateMap( pos );
			Dungeon.observe();
		}
	}
}
