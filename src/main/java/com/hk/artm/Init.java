package com.hk.artm;

import com.hk.artm.block.BlockLibrary;
import com.hk.artm.block.tile.TileLibrary;
import com.hk.artm.item.ItemARTMGuide;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public class Init
{
	public static final CreativeTabs artmTab = new CreativeARTMTabs();

	// ITEMS
	public static final Item ITEM_ARTM_GUIDE = new ItemARTMGuide();

	// BLOCKS
	public static final Block BLOCK_LIBRARY = new BlockLibrary();

	public static void register()
	{
		register(ITEM_ARTM_GUIDE);

		register(BLOCK_LIBRARY);

		registerTile(TileLibrary.class);
	}

	@SideOnly(Side.CLIENT)
	public static void registerRenders()
	{
		registerRender(ITEM_ARTM_GUIDE);

		registerRender(BLOCK_LIBRARY);
	}

	public static void addRecipes()
	{

	}

	private static void registerTile(Class<? extends TileEntity> cls)
	{
		GameRegistry.registerTileEntity(cls, cls.getName());
	}

	private static void register(Object obj)
	{
		if (obj instanceof Block)
		{
			Block block = (Block) obj;
			GameRegistry.register(block);
			GameRegistry.register(new ItemBlock(block), block.getRegistryName());
		}
		else if (obj instanceof Item)
		{
			GameRegistry.register((Item) obj);
		}
		else
		{
			throw new IllegalArgumentException(obj + " isn't a block or item");
		}
	}

	private static void register(Block block, ItemBlock itemBlock)
	{
		GameRegistry.register(block);
		GameRegistry.register(itemBlock, block.getRegistryName());
	}

	private static void registerRender(Object obj)
	{
		registerRender(obj, 1);
	}

	private static void registerRender(Object obj, int subtypes)
	{
		if (obj instanceof Block)
		{
			Block block = (Block) obj;
			final Item item = Item.getItemFromBlock(block);
			for (int i = 0; i < subtypes; i++)
			{
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, i, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			}
		}
		else if (obj instanceof Item)
		{
			final Item item = (Item) obj;
			for (int i = 0; i < subtypes; i++)
			{
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, i, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			}
		}
		else
		{
			throw new IllegalArgumentException(obj + " isn't a block or item");
		}
	}
}
