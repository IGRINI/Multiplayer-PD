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
package com.sarius.multiplayer_pd.items.scrolls;

import com.sarius.multiplayer_pd.Badges;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.effects.Speck;
import com.sarius.multiplayer_pd.items.Item;
import com.sarius.multiplayer_pd.items.armor.Armor;
import com.sarius.multiplayer_pd.items.weapon.Weapon;
import com.sarius.multiplayer_pd.utils.GLog;
import com.sarius.multiplayer_pd.windows.WndBag;

public class ScrollOfMagicalInfusion extends InventoryScroll {

	private static final String TXT_INFUSE	= "your %s is infused with arcane energy!";
	
	{
		name = "Scroll of Magical Infusion";
		initials = "MaI";

		inventoryTitle = "Select an item to infuse";
		mode = WndBag.Mode.ENCHANTABLE;

		bones = true;
	}
	
	@Override
	protected void onItemSelected( Item item ) {

		ScrollOfRemoveCurse.uncurse( Dungeon.hero, item );
		if (item instanceof Weapon)
			((Weapon)item).upgrade(true);
		else
			((Armor)item).upgrade(true);
		
		GLog.p( TXT_INFUSE, item.name() );
		
		Badges.validateItemLevelAquired( item );
		
		curUser.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
	}
	
	@Override
	public String desc() {
		return
			"This scroll will infuse a weapon or armor with powerful magical energy.\n\n" +
			"In addition to being upgraded, A weapon will gain a magical enchantment, or armor will be imbued with a magical glyph.\n\n" +
			"If the item already has an enchantment or glyph, it will never be erased by this scroll.";
	}
}
