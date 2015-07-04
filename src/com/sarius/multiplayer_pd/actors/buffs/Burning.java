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

import com.sarius.multiplayer_pd.Badges;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.ResultDescriptions;
import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.actors.blobs.Blob;
import com.sarius.multiplayer_pd.actors.blobs.Fire;
import com.sarius.multiplayer_pd.actors.hero.Hero;
import com.sarius.multiplayer_pd.actors.mobs.Thief;
import com.sarius.multiplayer_pd.effects.particles.ElmoParticle;
import com.sarius.multiplayer_pd.items.Heap;
import com.sarius.multiplayer_pd.items.Item;
import com.sarius.multiplayer_pd.items.food.ChargrilledMeat;
import com.sarius.multiplayer_pd.items.food.MysteryMeat;
import com.sarius.multiplayer_pd.items.rings.RingOfElements.Resistance;
import com.sarius.multiplayer_pd.items.scrolls.Scroll;
import com.sarius.multiplayer_pd.levels.Level;
import com.sarius.multiplayer_pd.scenes.GameScene;
import com.sarius.multiplayer_pd.sprites.CharSprite;
import com.sarius.multiplayer_pd.ui.BuffIndicator;
import com.sarius.multiplayer_pd.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Burning extends Buff implements Hero.Doom {

	private static final String TXT_BURNS_UP		= "%s burns up!";
	private static final String TXT_BURNED_TO_DEATH	= "You burned to death...";
	
	private static final float DURATION = 8f;
	
	private float left;
	
	private static final String LEFT	= "left";

	{
		type = buffType.NEGATIVE;
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEFT, left );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat( LEFT );
	}

	@Override
	public boolean act() {
		
		if (target.isAlive()) {
			
			target.damage( Random.Int( 1, 5 ), this );
			Buff.detach( target, Chill.class);

			if (target instanceof Hero) {

				Hero hero = (Hero)target;
				Item item = hero.belongings.randomUnequipped();
				if (item instanceof Scroll) {
					
					item = item.detach( hero.belongings.backpack );
					GLog.w( TXT_BURNS_UP, item.toString() );
					
					Heap.burnFX( hero.pos );
					
				} else if (item instanceof MysteryMeat) {
					
					item = item.detach( hero.belongings.backpack );
					ChargrilledMeat steak = new ChargrilledMeat();
					if (!steak.collect( hero.belongings.backpack )) {
						Dungeon.level.drop( steak, hero.pos ).sprite.drop();
					}
					GLog.w( TXT_BURNS_UP, item.toString() );
					
					Heap.burnFX( hero.pos );
					
				}
				
			} else if (target instanceof Thief && ((Thief)target).item instanceof Scroll) {
				
				((Thief)target).item = null;
				target.sprite.emitter().burst( ElmoParticle.FACTORY, 6 );
			}

		} else {
			detach();
		}
		
		if (Level.flamable[target.pos]) {
			GameScene.add( Blob.seed( target.pos, 4, Fire.class ) );
		}
		
		spend( TICK );
		left -= TICK;
		
		if (left <= 0 ||
			Random.Float() > (2 + (float)target.HP / target.HT) / 3 ||
			(Level.water[target.pos] && !target.flying)) {
			
			detach();
		}
		
		return true;
	}
	
	public void reignite( Char ch ) {
		left = duration( ch );
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FIRE;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.BURNING);
		else target.sprite.remove(CharSprite.State.BURNING);
	}

	@Override
	public String toString() {
		return "Burning";
	}

	public static float duration( Char ch ) {
		Resistance r = ch.buff( Resistance.class );
		return r != null ? r.durationFactor() * DURATION : DURATION;
	}

	@Override
	public String desc() {
		return "Few things are more distressing than being engulfed in flames.\n" +
				"\n" +
				"Fire will deal damage every turn until it is put out by water, expires, or it is resisted. " +
				"Fire can be extinquished by stepping into water, or from the splash of a shattering potion. \n" +
				"\n" +
				"Additionally, the fire may ignite flammable terrain or items that it comes into contact with.\n" +
				"\n" +
				"The burning will last for " + dispTurns(left) + ", or until it is resisted or extinquished.";
	}

	@Override
	public void onDeath() {
		
		Badges.validateDeathFromFire();
		
		Dungeon.fail( ResultDescriptions.BURNING );
		GLog.n( TXT_BURNED_TO_DEATH );
	}
}
