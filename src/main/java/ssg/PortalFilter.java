package ssg;

import java.util.List;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.LootContext;
import com.seedfinding.mcfeature.loot.LootTable;
import com.seedfinding.mcfeature.loot.MCLootTables;
import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.mcfeature.loot.item.Items;

public class PortalFilter {
	private final ChunkRand rand = new ChunkRand();
	private final LootContext ctx = new LootContext(0L, MCVersion.v1_16_1);
	private final LootTable RPLOOT = MCLootTables.RUINED_PORTAL_CHEST.get();
	
	public boolean portalCanBeGood(long structseed, CPos rp) {
		
		// only considering mountains & "other" biomes:
		// swamp & ocean is unenterable, desert is buried, jungle is rare & overgrown
		rand.setCarverSeed(structseed, rp.getX(), rp.getZ(), MCVersion.v1_16_1);
		boolean isSurface = rand.nextFloat() >= 0.5F;
		if (!isSurface)
			return false;
		
		rand.advance(5); // airpocket, portal type, rot, mirror
		CPos chestChunk = rp; // warn: approximation, ~80% accuracy
		
		rand.setDecoratorSeed(structseed, chestChunk.getX()<<4, chestChunk.getZ()<<4, 40005, MCVersion.v1_16_1);
		ctx.setSeed(rand.nextLong());
		
		//return true;
		List<ItemStack> loot = RPLOOT.generate(ctx);
		return lootSatisfiesConditions(loot);
	}
	
	private boolean lootSatisfiesConditions(List<ItemStack> loot) {
		int conditions = 0;
		
		for (ItemStack is : loot) {
			String itemName = is.getItem().getName();
			if (itemName.equals(Items.FLINT_AND_STEEL.getName()) || is.getItem().getName().equals(Items.FIRE_CHARGE.getName())) {
				conditions |= 1;
			}
			// only filtering for 1 obby here, checking if portal is completable will be done for worldseeds
			else if (itemName.equals(Items.OBSIDIAN.getName())) {
				conditions |= 2;
			}
			else if (itemName.equals(Items.GOLDEN_AXE.getName())) {
				conditions |= 4;
			}
			else if (itemName.equals(Items.GOLDEN_SWORD.getName())) {
				if (!is.getItem().getEnchantments().isEmpty()) {
					Pair<String, Integer> ench = is.getItem().getEnchantments().get(0);
					if (ench.getFirst().equals("looting") && ench.getSecond() == 3) {
						conditions |= 8;
					}
				}
			}
		}
		
		return conditions == 15; // all bits were flagged -> true, else false
	}
}
