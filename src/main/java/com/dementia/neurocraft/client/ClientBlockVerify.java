package com.dementia.neurocraft.client;

import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SRefreshClientBlockList;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static com.dementia.neurocraft.NeuroCraft.MODID;
import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;
import static com.dementia.neurocraft.network.SRefreshClientBlockList.toIntArray;

@Mod.EventBusSubscriber(modid = MODID)
public class ClientBlockVerify {
    public static List<BlockPos> hallucinationBlocks = new ArrayList<>();
    private static int tickC = 1;

    @SubscribeEvent
    public static void onClientBreakBlockEvent(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.STOP) {
            System.out.println(hallucinationBlocks);
            var pos = event.getPos();
            if (hallucinationBlocks.contains(pos)) {
                removeHallucinationBlocks(pos);
            }
        }
    }

    public static void removeHallucinationBlocks(BlockPos pos) {
        hallucinationBlocks.remove(pos);
        PacketHandler.sendToServer(new SRefreshClientBlockList(toIntArray(pos)));
        HallucinationOccuredClient();
    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side != LogicalSide.CLIENT)
            return;

        var player = event.player;

        if (tickC++ == 5) {
            var onPos = player.getOnPos();
            if (hallucinationBlocks.contains(onPos)) {
                removeHallucinationBlocks(onPos);
            }
            tickC = 0;
        }
    }
//
//    @SubscribeEvent
//    public static void onEvent(Event e) {
//        try {
//            var x = Minecraft.getInstance();
//            if (x.player == null) {
//                return;
//            }
//            LOGGER.info("Event: " + e.toString());
//        } catch (Exception ignored) {
//        }
//    }

}
