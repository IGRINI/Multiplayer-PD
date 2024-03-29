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
package com.sarius.multiplayer_pd.actors.mobs.npcs;

import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.Journal;
import com.sarius.multiplayer_pd.actors.Actor;
import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.actors.buffs.Buff;
import com.sarius.multiplayer_pd.actors.mobs.Golem;
import com.sarius.multiplayer_pd.actors.mobs.Mob;
import com.sarius.multiplayer_pd.actors.mobs.Monk;
import com.sarius.multiplayer_pd.items.Generator;
import com.sarius.multiplayer_pd.items.quest.DwarfToken;
import com.sarius.multiplayer_pd.items.rings.Ring;
import com.sarius.multiplayer_pd.levels.CityLevel;
import com.sarius.multiplayer_pd.levels.Room;
import com.sarius.multiplayer_pd.scenes.GameScene;
import com.sarius.multiplayer_pd.sprites.ImpSprite;
import com.sarius.multiplayer_pd.utils.Utils;
import com.sarius.multiplayer_pd.windows.WndImp;
import com.sarius.multiplayer_pd.windows.WndQuest;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Imp extends NPC {

	{
		name = "ambitious imp";
		spriteClass = ImpSprite.class;
	}
	
	private static final String TXT_GOLEMS1	=
		"Are you an adventurer? I love adventurers! You can always rely on them " +
		"if something needs to be killed. Am I right? For a bounty of course ;)\n" +
		"In my case this is _golems_ who need to be killed. You see, I'm going to start a " +
		"little business here, but these stupid golems are bad for business! " +
		"It's very hard to negotiate with wandering lumps of granite, damn them! " +
		"So please, kill... let's say _6 of them_ and a reward is yours.";
	
	private static final String TXT_MONKS1	=
		"Are you an adventurer? I love adventurers! You can always rely on them " +
		"if something needs to be killed. Am I right? For a bounty of course ;)\n" +
		"In my case this is _monks_ who need to be killed. You see, I'm going to start a " +
		"little business here, but these lunatics don't buy anything themselves and " +
		"will scare away other customers. " +
		"So please, kill... let's say _8 of them_ and a reward is yours.";
	
	private static final String TXT_GOLEMS2	=
		"How is your golem safari going?";
	
	private static final String TXT_MONKS2	=
		"Oh, you are still alive! I knew that your kung-fu is stronger ;) " +
		"Just don't forget to grab these monks' tokens.";
	
	private static final String TXT_CYA	= "See you, %s!";
	private static final String TXT_HEY	= "Psst, %s!";
	
	private boolean seenBefore = false;
	
	@Override
	protected boolean act() {
		
		if (!Quest.given && Dungeon.visible[pos]) {
			if (!seenBefore) {
				yell( Utils.format( TXT_HEY, Dungeon.hero.givenName() ) );
			}
			seenBefore = true;
		} else {
			seenBefore = false;
		}
		
		throwItem();
		
		return super.act();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return 1000;
	}
	
	@Override
	public String defenseVerb() {
		return "evaded";
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public void interact() {
		
		sprite.turnTo( pos, Dungeon.hero.pos );
		if (Quest.given) {
			
			DwarfToken tokens = Dungeon.hero.belongings.getItem( DwarfToken.class );
			if (tokens != null && (tokens.quantity() >= 8 || (!Quest.alternative && tokens.quantity() >= 6))) {
				GameScene.show( new WndImp( this, tokens ) );
			} else {
				tell( Quest.alternative ? TXT_MONKS2 : TXT_GOLEMS2, Dungeon.hero.givenName() );
			}
			
		} else {
			tell( Quest.alternative ? TXT_MONKS1 : TXT_GOLEMS1 );
			Quest.given = true;
			Quest.completed = false;
			
			Journal.add( Journal.Feature.IMP );
		}
	}
	
	private void tell( String format, Object...args ) {
		GameScene.show(
			new WndQuest( this, Utils.format( format, args ) ) );
	}
	
	public void flee() {
		
		yell( Utils.format( TXT_CYA, Dungeon.hero.givenName() ) );
		
		destroy();
		sprite.die();
	}
	
	@Override
	public String description() {
		return
			"Imps are lesser demons. They are notable for neither their strength nor their magic talent, " +
			"but they are quite smart and sociable. Many imps prefer to live among non-demons.";
	}
	
	public static class Quest {
		
		private static boolean alternative;
		
		private static boolean spawned;
		private static boolean given;
		private static boolean completed;
		
		public static Ring reward;
		
		public static void reset() {
			spawned = false;

			reward = null;
		}
		
		private static final String NODE		= "demon";
		
		private static final String ALTERNATIVE	= "alternative";
		private static final String SPAWNED		= "spawned";
		private static final String GIVEN		= "given";
		private static final String COMPLETED	= "completed";
		private static final String REWARD		= "reward";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				node.put( ALTERNATIVE, alternative );
				
				node.put( GIVEN, given );
				node.put( COMPLETED, completed );
				node.put( REWARD, reward );
			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
				alternative	= node.getBoolean( ALTERNATIVE );
				
				given = node.getBoolean( GIVEN );
				completed = node.getBoolean( COMPLETED );
				reward = (Ring)node.get( REWARD );
			}
		}
		
		public static void spawn( CityLevel level ) {
			if (!spawned && Dungeon.depth > 16 && Random.Int( 20 - Dungeon.depth ) == 0) {
				
				Imp npc = new Imp();
				do {
					npc.pos = level.randomRespawnCell();
				} while (npc.pos == -1 || level.heaps.get( npc.pos ) != null);
				level.mobs.add( npc );
				
				spawned = true;
				alternative = Random.Int( 2 ) == 0;
				
				given = false;
				
				do {
					reward = (Ring)Generator.random( Generator.Category.RING );
				} while (reward.cursed);
				reward.upgrade( 2 );
				reward.cursed = true;
			}
		}
		
		public static void process( Mob mob ) {
			if (spawned && given && !completed) {
				if ((alternative && mob instanceof Monk) ||
					(!alternative && mob instanceof Golem)) {
					
					Dungeon.level.drop( new DwarfToken(), mob.pos ).sprite.drop();
				}
			}
		}
		
		public static void complete() {
			reward = null;
			completed = true;
			
			Journal.remove( Journal.Feature.IMP );
		}
		
		public static boolean isCompleted() {
			return completed;
		}
	}
}
