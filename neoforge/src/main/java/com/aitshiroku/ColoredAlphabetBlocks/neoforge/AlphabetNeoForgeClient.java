package com.aitshiroku.ColoredAlphabetBlocks.neoforge;

import java.util.List;

import com.aitshiroku.ColoredAlphabetBlocks.AlphabetCommon;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public final class AlphabetNeoForgeClient {

    private AlphabetNeoForgeClient() {
    }

    public static void registerListeners(IEventBus modEventBus) {
        modEventBus.addListener(AlphabetNeoForgeClient::onClientSetup);
        modEventBus.addListener(AlphabetNeoForgeClient::registerItemTintSources);
        modEventBus.addListener(AlphabetNeoForgeClient::registerBlockColors);
    }

    @SuppressWarnings("deprecation")
    public static void onClientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Render type setting is now handled automatically
        });
    }

    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                Identifier.fromNamespaceAndPath(AlphabetCommon.MOD_ID, "background_tint"),
                MapCodec.unit(new ItemTintSource() {
                    @Override
                    public int calculate(ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level,
                            net.minecraft.world.entity.LivingEntity entity) {
                        if (stack.getItem() instanceof BlockItem blockItem) {
                            Block block = blockItem.getBlock();
                            BlockState state = AlphabetBlockStateUtil.stateFromItemStack(stack, block);
                            if (state.hasProperty(LetterBlock.COLOR)) {
                                AlphabetColorProperties.BlockColor color = state
                                        .getValue(LetterBlock.COLOR);
                                return AlphabetColorUtil.backgroundArgbFromColor(color);
                            }
                        }
                        return AlphabetColorUtil
                                .backgroundArgbFromColor(AlphabetColorProperties.BlockColor.NONE);
                    }

                    @Override
                    public MapCodec<? extends ItemTintSource> type() {
                        return MapCodec.unit(this);
                    }
                }));

        event.register(
                Identifier.fromNamespaceAndPath(AlphabetCommon.MOD_ID, "glyph_tint"),
                MapCodec.unit(new ItemTintSource() {
                    @Override
                    public int calculate(ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level,
                            net.minecraft.world.entity.LivingEntity entity) {
                        if (stack.getItem() instanceof BlockItem blockItem) {
                            Block block = blockItem.getBlock();
                            BlockState state = AlphabetBlockStateUtil.stateFromItemStack(stack, block);
                            if (state.hasProperty(LetterBlock.GLYPH_COLOR)) {
                                DyeColor glyphDye = state.getValue(LetterBlock.GLYPH_COLOR);
                                return AlphabetColorUtil.glyphArgbFromDye(glyphDye);
                            }
                        }
                        return AlphabetColorUtil.glyphArgbFromDye(DyeColor.BLACK);
                    }

                    @Override
                    public MapCodec<? extends ItemTintSource> type() {
                        return MapCodec.unit(this);
                    }
                }));
    }

    private static final BlockTintSource BACKGROUND_TINT_SOURCE = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            AlphabetColorProperties.BlockColor color = state.hasProperty(LetterBlock.COLOR)
                    ? state.getValue(LetterBlock.COLOR)
                    : AlphabetColorProperties.BlockColor.NONE;
            return AlphabetColorUtil.backgroundArgbFromColor(color);
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            AlphabetColorProperties.BlockColor color = state.hasProperty(LetterBlock.COLOR)
                    ? state.getValue(LetterBlock.COLOR)
                    : AlphabetColorProperties.BlockColor.NONE;
            return AlphabetColorUtil.backgroundArgbFromColor(color);
        }
    };

    private static final BlockTintSource GLYPH_TINT_SOURCE = new BlockTintSource() {
        @Override
        public int color(BlockState state) {
            DyeColor glyphDye = state.hasProperty(LetterBlock.GLYPH_COLOR)
                    ? state.getValue(LetterBlock.GLYPH_COLOR)
                    : DyeColor.BLACK;
            return AlphabetColorUtil.glyphArgbFromDye(glyphDye);
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            DyeColor glyphDye = state.hasProperty(LetterBlock.GLYPH_COLOR)
                    ? state.getValue(LetterBlock.GLYPH_COLOR)
                    : DyeColor.BLACK;
            return AlphabetColorUtil.glyphArgbFromDye(glyphDye);
        }
    };

    private static final List<BlockTintSource> ALPHABET_BLOCK_TINT_SOURCES = List.of(
        BACKGROUND_TINT_SOURCE,
        GLYPH_TINT_SOURCE
    );

    public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        for (net.neoforged.neoforge.registries.DeferredHolder<Block, ? extends Block> ro : AlphabetBlockNeoForge.BLOCKS
                .getEntries()) {
            Block block = ro.get();
            if (!(block instanceof LetterBlock)) {
                continue;
            }
            event.register(ALPHABET_BLOCK_TINT_SOURCES, block);
        }
    }
}
