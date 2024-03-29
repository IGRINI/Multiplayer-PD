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
package com.sarius.multiplayer_pd.items.artifacts;

import com.sarius.multiplayer_pd.Assets;
import com.sarius.multiplayer_pd.Badges;
import com.sarius.multiplayer_pd.Dungeon;
import com.sarius.multiplayer_pd.Statistics;
import com.sarius.multiplayer_pd.actors.buffs.Buff;
import com.sarius.multiplayer_pd.actors.buffs.Hunger;
import com.sarius.multiplayer_pd.actors.hero.Hero;
import com.sarius.multiplayer_pd.effects.Speck;
import com.sarius.multiplayer_pd.effects.SpellSprite;
import com.sarius.multiplayer_pd.items.Item;
import com.sarius.multiplayer_pd.items.food.Blandfruit;
import com.sarius.multiplayer_pd.items.food.Food;
import com.sarius.multiplayer_pd.items.scrolls.ScrollOfRecharging;
import com.sarius.multiplayer_pd.scenes.GameScene;
import com.sarius.multiplayer_pd.sprites.ItemSpriteSheet;
import com.sarius.multiplayer_pd.utils.GLog;
import com.sarius.multiplayer_pd.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class HornOfPlenty extends Artifact {


	{
		name = "Horn of Plenty";
		image = ItemSpriteSheet.ARTIFACT_HORN1;

		level = 0;
		levelCap = 30;

		charge = 0;
		partialCharge = 0;
		chargeCap = 10;

		defaultAction = AC_EAT;
	}

	private static final float TIME_TO_EAT	= 3f;

	private float energy = 36f;

	public static final String AC_EAT = "EAT";
	public static final String AC_STORE = "STORE";

	protected String inventoryTitle = "Select a piece of food";
	protected WndBag.Mode mode = WndBag.Mode.FOOD;

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && charge > 0)
			actions.add(AC_EAT);
		if (isEquipped( hero ) && level < 30 && !cursed)
			actions.add(AC_STORE);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_EAT)){

			if (!isEquipped(hero)) GLog.i("You need to equip your horn to do that.");
			else if (charge == 0)  GLog.i("Your horn has no food in it to eat!");
			else {
				((Hunger) hero.buff(Hunger.class)).satisfy(energy * charge);

				//if you get at least 100 food energy from the horn
				if (charge >= 3) {
					switch (hero.heroClass) {
						case WARRIOR:
							if (hero.HP < hero.HT) {
								hero.HP = Math.min(hero.HP + 5, hero.HT);
								hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
							}
							break;
						case MAGE:
							//1 charge
							Buff.affect( hero, ScrollOfRecharging.Recharging.class, 4f );
							ScrollOfRecharging.charge(hero);
							break;
						case ROGUE:
						case HUNTRESS:
							break;
					}

					Statistics.foodEaten++;
				}
				charge = 0;

				hero.sprite.operate(hero.pos);
				hero.busy();
				SpellSprite.show(hero, SpellSprite.FOOD);
				Sample.INSTANCE.play(Assets.SND_EAT);
				GLog.i("You eat from the horn.");

				hero.spend(TIME_TO_EAT);

				Badges.validateFoodEaten();

				image = ItemSpriteSheet.ARTIFACT_HORN1;

				updateQuickslot();
			}

		} else if (action.equals(AC_STORE)){

			GameScene.selectItem(itemSelector, mode, inventoryTitle);
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new hornRecharge();
	}

	@Override
	public String desc() {
		String desc = "This horn can't be blown into, but instead seems to fill up with food over time.\n\n";

		if (charge == 0)
			desc += "The horn is completely empty.";
		else if (charge < 3)
			desc += "The horn is almost empty, a few small fruits and berries sit in the back.";
		else if (charge < 7)
			desc += "The horn is partially filled, you can see several fruits & vegetables inside.";
		else if (charge < 10)
			desc += "The horn is getting quite full, several pieces of fresh produce are poking up towards the front.";
		else
			desc += "The horn is overflowing! A delicious array of fruit and veg is filling the horn up to its brim.";

		if ( isEquipped( Dungeon.hero ) ){
			if (!cursed) {
				desc += "\n\nThe horn rests at your side and is surprisingly lightweight, even with food in it.";

				if (level < 15)
					desc += " Perhaps there is a way to increase the horn's power by giving it food energy.";
			} else {
				desc += "\n\nThe cursed horn has bound itself to your side, " +
						"it seems to be eager to take food rather than produce it.";
			}
		}

		return desc;
	}

	public class hornRecharge extends ArtifactBuff{

		@Override
		public boolean act() {
			if (charge < chargeCap && !cursed) {

				//generates 0.25 food value every round, +0.015 value per level
				//to a max of 0.70 food value per round (0.25+0.5, at level 30)
				partialCharge += 0.25f + (0.015f*level);

				//charge is in increments of 36 food value.
				if (partialCharge >= 36) {
					charge++;
					partialCharge -= 36;

					if (charge == chargeCap)
						image = ItemSpriteSheet.ARTIFACT_HORN4;
					else if (charge >= 7)
						image = ItemSpriteSheet.ARTIFACT_HORN3;
					else if (charge >= 3)
						image = ItemSpriteSheet.ARTIFACT_HORN2;
					else
						image = ItemSpriteSheet.ARTIFACT_HORN1;

					if (charge == chargeCap){
						GLog.p("Your horn is full of food!");
						partialCharge = 0;
					}

					updateQuickslot();
				}
			} else
				partialCharge = 0;

			spend( TICK );

			return true;
		}

	}

	protected static WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null && item instanceof Food) {
				if (item instanceof Blandfruit && ((Blandfruit) item).potionAttrib == null){
					GLog.w("your horn rejects the unprepared blandfruit.");
				} else {
					Hero hero = Dungeon.hero;
					hero.sprite.operate( hero.pos );
					hero.busy();
					hero.spend( TIME_TO_EAT );

					curItem.upgrade(((Food)item).hornValue);
					if (curItem.level >= 30){
						curItem.level = 30;
						GLog.p("your horn has consumed all the food it can!");
					} else
						GLog.p("the horn consumes your food offering and grows in strength!");
					item.detach(hero.belongings.backpack);
				}

			}
		}
	};

}
