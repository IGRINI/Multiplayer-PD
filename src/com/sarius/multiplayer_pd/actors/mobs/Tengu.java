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
package com.sarius.multiplayer_pd.actors.mobs;

import java.util.HashSet;

import com.sarius.multiplayer_pd.Assets;
import com.sarius.multiplayer_pd.Badges;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.Badges.Badge;
import com.sarius.multiplayer_pd.actors.Actor;
import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.actors.blobs.ToxicGas;
import com.sarius.multiplayer_pd.actors.buffs.Poison;
import com.sarius.multiplayer_pd.effects.CellEmitter;
import com.sarius.multiplayer_pd.effects.Speck;
import com.sarius.multiplayer_pd.items.TomeOfMastery;
import com.sarius.multiplayer_pd.items.artifacts.LloydsBeacon;
import com.sarius.multiplayer_pd.items.keys.SkeletonKey;
import com.sarius.multiplayer_pd.items.scrolls.ScrollOfMagicMapping;
import com.sarius.multiplayer_pd.items.scrolls.ScrollOfPsionicBlast;
import com.sarius.multiplayer_pd.items.weapon.enchantments.Death;
import com.sarius.multiplayer_pd.levels.Level;
import com.sarius.multiplayer_pd.levels.Terrain;
import com.sarius.multiplayer_pd.levels.traps.PoisonTrap;
import com.sarius.multiplayer_pd.mechanics.Ballistica;
import com.sarius.multiplayer_pd.scenes.GameScene;
import com.sarius.multiplayer_pd.sprites.TenguSprite;
import com.sarius.multiplayer_pd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Tengu extends Mob {

	private static final int JUMP_DELAY = 5;
	
	{
		name = "Tengu";
		spriteClass = TenguSprite.class;
		
		HP = HT = 120;
		EXP = 20;
		defenseSkill = 20;
	}
	
	private int timeToJump = JUMP_DELAY;
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 8, 15 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20;
	}
	
	@Override
	public int dr() {
		return 5;
	}
	
	@Override
	public void die( Object cause ) {
		
		Badges.Badge badgeToCheck = null;
		switch (Dungeon.hero.heroClass) {
		case WARRIOR:
			badgeToCheck = Badge.MASTERY_WARRIOR;
			break;
		case MAGE:
			badgeToCheck = Badge.MASTERY_MAGE;
			break;
		case ROGUE:
			badgeToCheck = Badge.MASTERY_ROGUE;
			break;
		case HUNTRESS:
			badgeToCheck = Badge.MASTERY_HUNTRESS;
			break;
		}
		if (!Badges.isUnlocked( badgeToCheck )) {
			Dungeon.level.drop( new TomeOfMastery(), pos ).sprite.drop();
		}
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop();
		super.die( cause );
		
		Badges.validateBossSlain();

		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
			GLog.p("Your beacon grows stronger!");
		}
		
		yell( "Free at last..." );
	}
	
	@Override
	protected boolean getCloser( int target ) {
		if (Level.fieldOfView[target]) {
			jump();
			return true;
		} else {
			return super.getCloser( target );
		}
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}
	
	@Override
	protected boolean doAttack( Char enemy ) {
		timeToJump--;
		if (timeToJump <= 0 && Level.adjacent( pos, enemy.pos )) {
			jump();
			return true;
		} else {
			return super.doAttack( enemy );
		}
	}
	
	private void jump() {
		timeToJump = JUMP_DELAY;
		
		for (int i=0; i < 4; i++) {
			int trapPos;
			do {
				trapPos = Random.Int( Level.LENGTH );
			} while (!Level.fieldOfView[trapPos] || !Level.passable[trapPos]);
			
			if (Dungeon.level.map[trapPos] == Terrain.INACTIVE_TRAP) {
				Dungeon.level.setTrap( new PoisonTrap().reveal(), trapPos );
				Level.set( trapPos, Terrain.TRAP );
				ScrollOfMagicMapping.discover( trapPos );
			}
		}
		
		int newPos;
		do {
			newPos = Random.Int( Level.LENGTH );
		} while (
			!Level.fieldOfView[newPos] ||
			!Level.passable[newPos] ||
			Level.adjacent( newPos, enemy.pos ) ||
			Actor.findChar( newPos ) != null);
		
		sprite.move( pos, newPos );
		move( newPos );
		
		if (Dungeon.visible[newPos]) {
			CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
			Sample.INSTANCE.play( Assets.SND_PUFF );
		}
		
		spend( 1 / speed() );
	}
	
	@Override
	public void notice() {
		super.notice();
		yell( "Gotcha, " + Dungeon.hero.givenName() + "!" );
	}
	
	@Override
	public String description() {
		return
			"Tengu are members of the ancient assassins clan, which is also called Tengu. " +
			"These assassins are noted for extensive use of shuriken and traps.";
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Poison.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
