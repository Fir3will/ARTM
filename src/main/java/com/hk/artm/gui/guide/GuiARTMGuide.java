package com.hk.artm.gui.guide;

import com.hk.artm.ARTM;
import com.hk.artm.Init;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiARTMGuide extends GuiScreen
{
	private static final ResourceLocation bookBackground = new ResourceLocation(ARTM.MODID + ":textures/gui/artmguide.png");
	public final EntityPlayerSP player;
	private final GuidePage[] pages;
	private final int xSize, ySize;
	private int currPage, typedPage = -1;
	private long typeStart = -1;
	private final List<Integer> prevPages = new ArrayList<Integer>();

	public GuiARTMGuide(EntityPlayerSP player)
	{
		this.player = player;
		List<GuidePage> lst = new ArrayList<GuidePage>();

		addPage(lst, new TitlePage(this));
		addPage(lst, new InfoPage(this, "To begin with ARTM, you must collect three different materials. These materials are " + fmt(TextFormatting.GOLD, "Copper") + ", " + fmt(TextFormatting.GRAY, "Tin") + ", and finally " + fmt(TextFormatting.DARK_GREEN, "Nickel (or Ferrous)") + ". These are the main metals of the mod that'll be used in most recipes. Including alloys of these metals as well.",
				"After collecting these materials. you'll need to construct the " + fmt(TextFormatting.DARK_BLUE, "Library") + " block, (Recipe on next page). "));
		addPage(lst, new RecipePage(this, new ItemStack(Init.BLOCK_LIBRARY), null, "This is the " + fmt(TextFormatting.DARK_BLUE, "Library") + " block. This block allows you to create " + fmt(TextFormatting.BLUE, "Blueprints") + ". Blueprints allow you to craft what they are for in the constructing table. Most important to remember about this block is that it's a multi-block structure. You need to assemble a 2x2 grid of these blocks to create the multi-block structure."));

		pages = lst.toArray(new GuidePage[lst.size()]);
		currPage = 0;
		xSize = 230;
		ySize = 230;

		goToPage(0);
	}

	private void addPage(List<GuidePage> lst, GuidePage page)
	{
		lst.add(page.setPageNumber(lst.size()));
	}

	@Override
	public void initGui()
	{
		super.initGui();
		this.buttonList.clear();
		addButton(new GuiButton(0, this.width / 2 - 100, 196, 200, 20, I18n.format("gui.done")));
		addButton(new GuiARTMGuide.NextPageButton(1, width * 4 / 5, 206, true));
		addButton(new GuiARTMGuide.NextPageButton(2, width / 5, 206, false));

		for (GuidePage page : pages)
		{
			page.initPage();
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		long time = Minecraft.getSystemTime();
		if (typeStart != -1 && time - typeStart > 1000)
		{
			if (typedPage != -1)
			{
				goToPage(typedPage);
				typedPage = -1;
			}
			typeStart = -1;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		boolean unicode = fontRendererObj.getUnicodeFlag();
		fontRendererObj.setUnicodeFlag(true);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(bookBackground);
		int i = (this.width - xSize) / 2;
		int j = (this.height - ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		super.drawScreen(mouseX, mouseY, partialTicks);
		GL11.glPushMatrix();
		GL11.glTranslatef(i + 15, j + 10, 0.1F);
		getCurrPage().drawPage(mouseX - (i + 15), mouseY - (j + 10));
		GL11.glPopMatrix();
		fontRendererObj.setUnicodeFlag(unicode);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}

	public void goToPage(int page)
	{
		while (page >= pages.length)
		{
			page -= pages.length;
		}
		while (page < 0)
		{
			page += pages.length;
		}

		if (currPage != page)
		{
			pages[currPage].closedPage();
			prevPages.add(currPage);
			currPage = page;
			pages[currPage].openedPage();
		}
	}

	public GuidePage getCurrPage()
	{
		return pages[currPage];
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		getCurrPage().mousePressed(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton)
	{
		super.mouseReleased(mouseX, mouseY, mouseButton);
		getCurrPage().mouseReleased(mouseX, mouseY, mouseButton);
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);

		if (keyCode == Keyboard.KEY_LEFT)
		{
			goToPage(currPage - 1);
		}
		else if (keyCode == Keyboard.KEY_RIGHT)
		{
			goToPage(currPage + 1);
		}
		else if (keyCode == Keyboard.KEY_BACK)
		{
			int size = prevPages.size();
			if (size > 0)
			{
				goToPage(prevPages.remove(size - 1));
			}
		}
		else if (Character.isDigit(typedChar))
		{
			if (typeStart == -1)
			{
				typeStart = Minecraft.getSystemTime();
			}
			typedPage = typedPage * 10 + Integer.parseInt(String.valueOf(typedChar));
		}
		else
		{
			typeStart = -1;
			typedPage = -1;
			getCurrPage().keyTyped(typedChar, keyCode);
		}
	}

	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.enabled)
		{
			if (button.id == 0)
			{
				Minecraft.getMinecraft().displayGuiScreen(null);
			}
			else if (button.id == 1)
			{
				goToPage(currPage + 1);
			}
			else if (button.id == 2)
			{
				goToPage(currPage - 1);
			}
		}
	}

	@Override
	public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
	{
		super.drawGradientRect(left, top, right, bottom, startColor, endColor);
	}

	@Override
	public void drawHorizontalLine(int startX, int endX, int y, int color)
	{
		super.drawHorizontalLine(startX, endX, y, color);
	}

	@Override
	public void drawVerticalLine(int x, int startY, int endY, int color)
	{
		super.drawVerticalLine(x, startY, endY, color);
	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y)
	{
		super.drawHoveringText(textLines, x, y);
	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font)
	{
		super.drawHoveringText(textLines, x, y, font);
	}

	@Override
	public void renderToolTip(ItemStack stack, int x, int y)
	{
		super.renderToolTip(stack, x, y);
	}

	public FontRenderer getFROBJ()
	{
		return this.fontRendererObj;
	}

	public RenderItem getItemRender()
	{
		return this.itemRender;
	}

	public String fmt(TextFormatting format, String txt)
	{
		return format + txt + TextFormatting.RESET;
	}

	@SideOnly(Side.CLIENT)
	static class NextPageButton extends GuiButton
	{
		private final boolean isForward;

		private NextPageButton(int buttonId, int x, int y, boolean isForwardIn)
		{
			super(buttonId, x, y, 18, 10, "");
			this.isForward = isForwardIn;
			xPosition -= width / 2;
			yPosition -= height / 2;
		}

		/**
		 * Draws this button to the screen.
		 */
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(bookBackground);
				int i = 0;
				int j = 233;

				if (flag)
				{
					i += 23;
				}

				if (!this.isForward)
				{
					j += 13;
				}

				this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, width, height);
			}
		}
	}
}
