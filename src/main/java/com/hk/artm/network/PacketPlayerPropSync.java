package com.hk.artm.network;

import com.hk.artm.ARTM;
import com.hk.artm.player.ARTMPlayerProperties;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;

public class PacketPlayerPropSync extends ARTMNetwork.ARTMPacket
{
	private NBTTagCompound tag;

	public PacketPlayerPropSync()
	{
	}

	public PacketPlayerPropSync(ARTMPlayerProperties properties)
	{
		this.tag = properties.serializeNBT();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.merge(this.tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		this.tag = tag;
	}

	@Override
	public void onClientReceive(EntityPlayerSP player)
	{
		player.getCapability(ARTM.ARTM_PLAYER_PROPERTIES, null).deserializeNBT(tag);
	}
}
