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
package com.sarius.multiplayer_pd.items.bags;

import com.sarius.multiplayer_pd.items.Item;
import com.sarius.multiplayer_pd.plants.Plant;
import com.sarius.multiplayer_pd.sprites.ItemSpriteSheet;

public class SeedPouch extends Bag {

	{
		name = "seed pouch";
		image = ItemSpriteSheet.POUCH;
		
		size = 12;
	}
	
	@Override
	public boolean grab( Item item ) {
		return item instanceof Plant.Seed;
	}
	
	@Override
	public int price() {
		return 50;
	}
	
	@Override
	public String info() {
		return
			"This small velvet pouch allows you to store any number of seeds in it. Very convenient.";
	}
}
