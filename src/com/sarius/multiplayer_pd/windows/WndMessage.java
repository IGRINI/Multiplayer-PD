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
package com.sarius.multiplayer_pd.windows;

import com.sarius.multiplayer_pd.ShatteredPixelDungeon;
import com.sarius.multiplayer_pd.scenes.PixelScene;
import com.sarius.multiplayer_pd.ui.Window;
import com.watabou.noosa.BitmapTextMultiline;

public class WndMessage extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 144;
	private static final int MARGIN = 4;
	
	public WndMessage( String text ) {
		
		super();
		
		BitmapTextMultiline info = PixelScene.createMultiline( text, 6 );
		info.maxWidth = (ShatteredPixelDungeon.landscape() ? WIDTH_L : WIDTH_P) - MARGIN * 2;
		info.measure();
		info.x = info.y = MARGIN;
		add( info );

		resize(
			(int)info.width() + MARGIN * 2,
			(int)info.height() + MARGIN * 2 );
	}
}
