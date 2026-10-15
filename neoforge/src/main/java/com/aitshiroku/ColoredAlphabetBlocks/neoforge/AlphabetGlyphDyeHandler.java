package com.aitshiroku.ColoredAlphabetBlocks.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Handles Shift + Right-click with Dye to change the glyph (letter) color.
 *
 * This MUST be handled via a NeoForge event because vanilla Minecraft skips
 * {@code Block.useItemOn()} when the player is sneaking while holding an item.
 * {@code PlayerInteractEvent.RightClickBlock} fires BEFORE that sneak-bypass
 * check, so we can intercept the interaction here.
 *
 * Registered programmatically on {@code NeoForge.EVENT_BUS} in
 * {@link AlphabetBlockNeoForge} constructor.
 */
public final class AlphabetGlyphDyeHandler {

    private AlphabetGlyphDyeHandler() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown())
            return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof DyeItem dyeItem))
            return;

        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof LetterBlock)) {
            return;
        }
        if (net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()
                .equals("empty_block")) {
            return;
        }
        if (!state.hasProperty(AlphabetColorProperties.GLYPH_COLOR))
            return;

        DyeColor newColor = stack.get(net.minecraft.core.component.DataComponents.DYE);

        // Same color already applied — consume the interaction but do nothing
        if (state.getValue(AlphabetColorProperties.GLYPH_COLOR) == newColor) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
            return;
        }

        // Apply glyph color change on the server side
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(AlphabetColorProperties.GLYPH_COLOR, newColor), 3);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 0.8F);
        }

        event.setCanceled(true);
        event.setCancellationResult(level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
    }
}
