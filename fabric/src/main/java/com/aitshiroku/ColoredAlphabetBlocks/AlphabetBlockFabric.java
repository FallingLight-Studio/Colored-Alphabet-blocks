package com.aitshiroku.ColoredAlphabetBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class AlphabetBlockFabric implements ModInitializer {

    public static final List<Block> REGISTERED_BLOCKS = new ArrayList<>();

    private static final Map<String, Item> REGISTERED_ITEMS =
        new LinkedHashMap<>();

    @Override
    public void onInitialize() {
        // Register all blocks and items
        for (AlphabetDefinitions.CharacterDef def : AlphabetDefinitions.all()) {
            Identifier id = Identifier.fromNamespaceAndPath(
                AlphabetCommon.MOD_ID,
                def.id()
            );

            ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);

            Block block = new LetterBlock(
                BlockBehaviour.Properties.of()
                    .setId(blockKey)
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(1.5f, 6.0f)
            );
            Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
            REGISTERED_BLOCKS.add(block);

            Item item = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, item);
            REGISTERED_ITEMS.put(def.id(), item);
        }

        // Create tab ResourceKey
        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(AlphabetCommon.MOD_ID, "alphabet_tab")
        );

        // Register custom creative tab
        CreativeModeTab alphabetTab = FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + AlphabetCommon.MOD_ID))
            .icon(() -> {
                Item firstItem = REGISTERED_ITEMS.get("letter_a");
                return firstItem != null
                    ? new ItemStack(firstItem)
                    : ItemStack.EMPTY;
            })
            .displayItems((params, output) -> {
                for (Item item : REGISTERED_ITEMS.values()) {
                    output.accept(item);
                }
            })
            .build();

        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            tabKey,
            alphabetTab
        );

        // Register Shift + Right-click with Dye → glyph color change.
        // This MUST be handled via an event because vanilla Minecraft skips
        // Block.useItemOn() when the player is sneaking while holding an item.
        // UseBlockCallback fires BEFORE that sneak-bypass check.
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.isShiftKeyDown()) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof DyeItem dyeItem)) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (!(state.getBlock() instanceof LetterBlock)) {
                return InteractionResult.PASS;
            }
            if (BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath().equals("empty_block")) {
                return InteractionResult.PASS;
            }
            if (!state.hasProperty(AlphabetColorProperties.GLYPH_COLOR)) {
                return InteractionResult.PASS;
            }

            DyeColor newColor = stack.get(net.minecraft.core.component.DataComponents.DYE);

            // Same color already applied — consume the interaction but do nothing
            if (state.getValue(AlphabetColorProperties.GLYPH_COLOR) == newColor) {
                return InteractionResult.CONSUME;
            }

            // Apply glyph color change on the server side
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(AlphabetColorProperties.GLYPH_COLOR, newColor), 3);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                world.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 0.8F);
            }

            return world.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        });
    }

    public static List<Block> letterBlocksView() {
        return Collections.unmodifiableList(REGISTERED_BLOCKS);
    }
}
