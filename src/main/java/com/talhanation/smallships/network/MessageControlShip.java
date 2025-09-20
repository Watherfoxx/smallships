package com.talhanation.smallships.network;

import com.talhanation.smallships.entities.AbstractSailShip;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessageControlShip implements Message<MessageControlShip> {

    private boolean forward, backward, left, right;
    private UUID uuid;

    public MessageControlShip() {

    }

    public MessageControlShip(boolean forward, boolean backward, boolean left, boolean right, PlayerEntity player) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.uuid = player.getUUID();
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        if (!context.getSender().getUUID().equals(uuid)) {
            //Main.LOGGER.error("The UUID of the sender was not equal to the packet UUID");
            return;
        }

        Entity e = context.getSender().getVehicle();

        if (!(e instanceof AbstractSailShip)) {
            return;
        }

        AbstractSailShip ship = (AbstractSailShip) e;

        ship.updateControls(forward, backward, left, right, context.getSender());
        ship.sendMessage(new StringTextComponent("Forward: " + forward), context.getSender().getUUID());
    }

    @Override
    public MessageControlShip fromBytes(PacketBuffer buf) {
        this.forward = buf.readBoolean();
        this.backward = buf.readBoolean();
        this.left = buf.readBoolean();
        this.right = buf.readBoolean();
        this.uuid = buf.readUUID();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(forward);
        buf.writeBoolean(backward);
        buf.writeBoolean(left);
        buf.writeBoolean(right);
        buf.writeUUID(uuid);
    }

}
