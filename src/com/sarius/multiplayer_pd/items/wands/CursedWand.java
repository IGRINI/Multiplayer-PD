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
package com.sarius.multiplayer_pd.items.wands;

import com.sarius.multiplayer_pd.Assets;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.ResultDescriptions;
import com.sarius.multiplayer_pd.actors.Actor;
import com.sarius.multiplayer_pd.actors.Char;
import com.sarius.multiplayer_pd.actors.blobs.Blob;
import com.sarius.multiplayer_pd.actors.blobs.ConfusionGas;
import com.sarius.multiplayer_pd.actors.blobs.Fire;
import com.sarius.multiplayer_pd.actors.blobs.ParalyticGas;
import com.sarius.multiplayer_pd.actors.blobs.Regrowth;
import com.sarius.multiplayer_pd.actors.blobs.ToxicGas;
import com.sarius.multiplayer_pd.actors.buffs.Buff;
import com.sarius.multiplayer_pd.actors.buffs.Burning;
import com.sarius.multiplayer_pd.actors.buffs.Frost;
import com.sarius.multiplayer_pd.actors.hero.Hero;
import com.sarius.multiplayer_pd.actors.mobs.Mimic;
import com.sarius.multiplayer_pd.actors.mobs.Mob;
import com.sarius.multiplayer_pd.actors.mobs.npcs.Sheep;
import com.sarius.multiplayer_pd.effects.CellEmitter;
import com.sarius.multiplayer_pd.effects.Flare;
import com.sarius.multiplayer_pd.effects.MagicMissile;
import com.sarius.multiplayer_pd.effects.Speck;
import com.sarius.multiplayer_pd.effects.SpellSprite;
import com.sarius.multiplayer_pd.effects.particles.ShadowParticle;
import com.sarius.multiplayer_pd.items.Bomb;
import com.sarius.multiplayer_pd.items.EquipableItem;
import com.sarius.multiplayer_pd.items.Generator;
import com.sarius.multiplayer_pd.items.Item;
import com.sarius.multiplayer_pd.items.KindOfWeapon;
import com.sarius.multiplayer_pd.items.KindofMisc;
import com.sarius.multiplayer_pd.items.armor.Armor;
import com.sarius.multiplayer_pd.items.artifacts.DriedRose;
import com.sarius.multiplayer_pd.items.artifacts.TimekeepersHourglass;
import com.sarius.multiplayer_pd.items.scrolls.ScrollOfRecharging;
import com.sarius.multiplayer_pd.items.scrolls.ScrollOfTeleportation;
import com.sarius.multiplayer_pd.items.weapon.missiles.MissileWeapon;
import com.sarius.multiplayer_pd.levels.Level;
import com.sarius.multiplayer_pd.levels.Terrain;
import com.sarius.multiplayer_pd.levels.traps.LightningTrap;
import com.sarius.multiplayer_pd.levels.traps.SummoningTrap;
import com.sarius.multiplayer_pd.mechanics.Ballistica;
import com.sarius.multiplayer_pd.plants.Plant;
import com.sarius.multiplayer_pd.scenes.GameScene;
import com.sarius.multiplayer_pd.scenes.InterlevelScene;
import com.sarius.multiplayer_pd.ui.HealthIndicator;
import com.sarius.multiplayer_pd.utils.GLog;
import com.sarius.multiplayer_pd.utils.Utils;
import com.sarius.multiplayer_pd.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;

//helper class to contain all the cursed wand zapping logic, so the main wand class doesn't get huge.
public class CursedWand {

	private static float COMMON_CHANCE = 0.6f;
	private static float UNCOMMON_CHANCE = 0.3f;
	private static float RARE_CHANCE = 0.09f;
	private static float VERY_RARE_CHANCE = 0.01f;

	public static void cursedZap(final Wand wand, final Hero user, final Ballistica bolt){
		switch (Random.chances(new float[]{COMMON_CHANCE, UNCOMMON_CHANCE, RARE_CHANCE, VERY_RARE_CHANCE})){
			case 0:
			default:
				commonEffect(wand, user, bolt);
				break;
			case 1:
				uncommonEffect(wand, user, bolt);
				break;
			case 2:
				rareEffect(wand, user, bolt);
				break;
			case 3:
				veryRareEffect(wand, user, bolt);
				break;
		}
	}

	private static void commonEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//anti-entropy
			case 0:
				cursedFX(user, bolt, new Callback() {
						public void call() {
							Char target = Actor.findChar(bolt.collisionPos);
							switch (Random.Int(2)){
								case 0:
									if (target != null)
										Buff.affect(target, Burning.class).reignite(target);
									Buff.affect(user, Frost.class, Frost.duration(user) * Random.Float(3f, 5f));
									break;
								case 1:
									Buff.affect(user, Burning.class).reignite(user);
									if (target != null)
										Buff.affect(target, Frost.class, Frost.duration(target) * Random.Float(3f, 5f));
									break;
							}
							wand.wandUsed();
						}
					});
				break;

			//spawns some regrowth
			case 1:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						int c = Dungeon.level.map[bolt.collisionPos];
						if (c == Terrain.EMPTY ||
								c == Terrain.EMBERS ||
								c == Terrain.EMPTY_DECO ||
								c == Terrain.GRASS ||
								c == Terrain.HIGH_GRASS) {
							GameScene.add( Blob.seed(bolt.collisionPos, 30, Regrowth.class));
						}
						wand.wandUsed();
					}
				});
				break;

			//random teleportation
			case 2:
				switch(Random.Int(2)){
					case 0:
						ScrollOfTeleportation.teleportHero(user);
						wand.wandUsed();
						break;
					case 1:
						cursedFX(user, bolt, new Callback() {
							public void call() {
								Char ch = Actor.findChar( bolt.collisionPos );
								if (ch != null) {
									int count = 10;
									int pos;
									do {
										pos = Dungeon.level.randomRespawnCell();
										if (count-- <= 0) {
											break;
										}
									} while (pos == -1);
									if (pos == -1) {
										GLog.w(ScrollOfTeleportation.TXT_NO_TELEPORT);
									} else {
										ch.pos = pos;
										ch.sprite.place(ch.pos);
										ch.sprite.visible = Dungeon.visible[pos];
									}
								}
								wand.wandUsed();
							}
						});
						break;
				}
				break;

			//random gas at location
			case 3:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						switch (Random.Int(3)) {
							case 0:
								GameScene.add( Blob.seed( bolt.collisionPos, 800, ConfusionGas.class ) );
								break;
							case 1:
								GameScene.add( Blob.seed( bolt.collisionPos, 500, ToxicGas.class ) );
								break;
							case 2:
								GameScene.add( Blob.seed( bolt.collisionPos, 200, ParalyticGas.class ) );
								break;
						}
						wand.wandUsed();
					}
				});
				break;
		}

	}

	private static void uncommonEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//Random plant
			case 0:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						int pos = bolt.collisionPos;
						//place the plant infront of an enemy so they walk into it.
						if (Actor.findChar(pos) != null && bolt.dist > 1) {
							pos = bolt.path.get(bolt.dist - 1);
						}
						Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), pos);
						wand.wandUsed();
					}
				});
				break;

			//Health transfer
			case 1:
				final Char target = Actor.findChar( bolt.collisionPos );
				if (target != null) {
					cursedFX(user, bolt, new Callback() {
						public void call() {
							int damage = user.lvl * 2;
							switch (Random.Int(2)) {
								case 0:
									user.HP = Math.min(user.HT, user.HP + damage);
									user.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
									target.damage(damage, wand);
									target.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
									break;
								case 1:
									user.damage( damage, this );
									user.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
									target.HP = Math.min(target.HT, target.HP + damage);
									target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
									Sample.INSTANCE.play(Assets.SND_CURSED);
									if (!user.isAlive()) {
										Dungeon.fail(Utils.format(ResultDescriptions.ITEM, wand.name()));
										GLog.n("You were killed by your own " + wand.name());
									}
									break;
							}
							wand.wandUsed();
						}
					});
				} else {
					GLog.i("nothing happens");
					wand.wandUsed();
				}
				break;

			//Bomb explosion
			case 2:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						new Bomb().explode(bolt.collisionPos);
						wand.wandUsed();
					}
				});
				break;

			//shock and recharge
			case 3:
				new LightningTrap().set( user.pos ).activate();
				Buff.prolong(user, ScrollOfRecharging.Recharging.class, 20f);
				ScrollOfRecharging.charge(user);
				SpellSprite.show(user, SpellSprite.CHARGE);
				wand.wandUsed();
				break;
		}

	}

	private static void rareEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//sheep transformation
			case 0:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						Char ch = Actor.findChar( bolt.collisionPos );
						if (ch != null && ch != user){
							Sheep sheep = new Sheep();
							sheep.lifespan = 10;
							sheep.pos = ch.pos;
							ch.sprite.killAndErase();
							Actor.remove(ch);
							Dungeon.level.mobs.remove(ch);
							HealthIndicator.instance.target(null);
							GameScene.add(sheep);
							CellEmitter.get(sheep.pos).burst(Speck.factory(Speck.WOOL), 4);
						} else {
							GLog.i("nothing happens");
						}
						wand.wandUsed();
					}
				});
				break;

			//curses!
			case 1:
				KindOfWeapon weapon = user.belongings.weapon;
				Armor armor = user.belongings.armor;
				KindofMisc misc1 = user.belongings.misc1;
				KindofMisc misc2 = user.belongings.misc2;
				if (weapon != null) weapon.cursed = weapon.cursedKnown = true;
				if (armor != null)  armor.cursed = armor.cursedKnown = true;
				if (misc1 != null)  misc1.cursed = misc1.cursedKnown = true;
				if (misc2 != null)  misc2.cursed = misc2.cursedKnown = true;
				EquipableItem.equipCursed(user);
				GLog.n("Your worn equipment becomes cursed!");
				wand.wandUsed();
				break;

			//inter-level teleportation
			case 2:
				if (Dungeon.depth > 1 && !Dungeon.bossLevel()) {

					Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
					if (buff != null) buff.detach();

					for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] ))
						if (mob instanceof DriedRose.GhostHero) mob.destroy();

					InterlevelScene.mode = InterlevelScene.Mode.RETURN;
					InterlevelScene.returnDepth = Random.Int(Dungeon.depth-1)+1;
					InterlevelScene.returnPos = -1;
					Game.switchScene(InterlevelScene.class);

				} else {
					ScrollOfTeleportation.teleportHero(user);
					wand.wandUsed();
				}
				break;

			//summon monsters
			case 3:
				new SummoningTrap().set( user.pos ).activate();
				wand.wandUsed();
				break;
		}
	}

	private static void veryRareEffect(final Wand wand, final Hero user, final Ballistica bolt){
		switch(Random.Int(4)){

			//great forest fire!
			case 0:
				for (int i = 0; i < Level.LENGTH; i++){
					int c = Dungeon.level.map[i];
					if (c == Terrain.EMPTY ||
							c == Terrain.EMBERS ||
							c == Terrain.EMPTY_DECO ||
							c == Terrain.GRASS ||
							c == Terrain.HIGH_GRASS) {
						GameScene.add( Blob.seed(i, 15, Regrowth.class));
					}
				}
				do {
					GameScene.add(Blob.seed(Dungeon.level.randomDestination(), 10, Fire.class));
				} while (Random.Int(5) != 0);
				new Flare(8, 32).color(0xFFFF66, true).show(user.sprite, 2f);
				Sample.INSTANCE.play(Assets.SND_TELEPORT);
				GLog.p("grass explodes around you!");
				GLog.w("you smell burning...");
				wand.wandUsed();
				break;

			//superpowered mimic
			case 1:
				cursedFX(user, bolt, new Callback() {
					public void call() {
						Mimic mimic = Mimic.spawnAt(bolt.collisionPos, new ArrayList<Item>());
						mimic.adjustStats(Dungeon.depth + 10);
						mimic.HP = mimic.HT;
						Item reward;
						do {
							reward = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
									Generator.Category.RING, Generator.Category.WAND));
						} while (reward.level < 2 && !(reward instanceof MissileWeapon));
						Sample.INSTANCE.play(Assets.SND_MIMIC, 1, 1, 0.5f);
						mimic.items.clear();
						mimic.items.add(reward);

						wand.wandUsed();
					}
				});
				break;

			//crashes the game, yes, really.
			case 2:
				try {
					Dungeon.saveAll();
					GameScene.show(
							new WndOptions("CURSED WAND ERROR", "this application will now self-destruct", "abort", "retry", "fail") {
								@Override
								public void hide() {
									throw new RuntimeException("critical wand exception");
								}
							}
					);
				} catch(IOException e){
					//oookay maybe don't kill the game if the save failed.
					GLog.i("nothing happens");
					wand.wandUsed();
				}
				break;

			//random transmogrification
			case 3:
				wand.wandUsed();
				wand.detach(user.belongings.backpack);
				Item result;
				do {
					result = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
							Generator.Category.RING, Generator.Category.ARTIFACT));
				} while (result.level < 0 && !(result instanceof MissileWeapon));
				if (result.isUpgradable()) result.upgrade();
				result.cursed = result.cursedKnown = true;
				GLog.w("your wand transmogrifies into a different item!");
				Dungeon.level.drop(result, user.pos).sprite.drop();
				wand.wandUsed();
				break;
		}
	}

	private static void cursedFX(final Hero user, final Ballistica bolt, final Callback callback){
		MagicMissile.rainbow(user.sprite.parent, bolt.sourcePos, bolt.collisionPos, callback);
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}

}
