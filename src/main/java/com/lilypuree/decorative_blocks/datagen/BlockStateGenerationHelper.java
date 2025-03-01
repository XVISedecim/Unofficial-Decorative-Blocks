package com.lilypuree.decorative_blocks.datagen;

import com.lilypuree.decorative_blocks.blocks.IWoodenBlock;
import com.lilypuree.decorative_blocks.state.ModBlockProperties;
import com.lilypuree.decorative_blocks.blocks.PalisadeBlock;
import com.lilypuree.decorative_blocks.datagen.types.IWoodType;
import com.lilypuree.decorative_blocks.datagen.types.WoodDecorativeBlockTypes;
import com.lilypuree.decorative_blocks.state.SupportFaceShape;
import net.minecraft.block.Block;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

import java.util.*;

import static com.lilypuree.decorative_blocks.datagen.types.WoodDecorativeBlockTypes.*;

public class BlockStateGenerationHelper {

    private String modid;
    private BlockStateProvider provider;

    public BlockStateGenerationHelper(String modid, BlockStateProvider provider) {
        this.modid = modid;
        this.provider = provider;
    }

    public void supportBlock(IWoodenBlock block) {
        IWoodType woodType = block.getWoodType();
        String texture = woodType + "_support";

        MultiPartBlockStateBuilder builder = getMultipartBuilder((Block) block);
        BlockStateProperties.UP.getAllValues().forEach(up -> {
            String upsideDown = up.value() ? "" : "upside_down_";
            ModBlockProperties.HORIZONTAL_SHAPE.getAllValues().forEach(shape -> {
                if (!shape.value().isHidden()){
                    addFourDirections(builder, sideEndModel(woodType, SUPPORT, upsideDown, "_horizontal_" + shape.value().getSerializedName(), texture), up, shape);
                }
            });
            ModBlockProperties.VERTICAL_SHAPE.getAllValues().forEach(shape -> {
                if (!shape.value().isHidden()){
                    addFourDirections(builder, sideEndModel(woodType, SUPPORT, upsideDown, "_vertical_" + shape.value().getSerializedName(), texture), up, shape);
                }
            });
            addFourDirections(builder, sideEndModel(woodType, SUPPORT, upsideDown, "_post", texture),
                    ModBlockProperties.HORIZONTAL_SHAPE.value(SupportFaceShape.BIG), ModBlockProperties.HORIZONTAL_SHAPE.value(SupportFaceShape.SMALL),
                    ModBlockProperties.VERTICAL_SHAPE.value(SupportFaceShape.BIG), ModBlockProperties.VERTICAL_SHAPE.value(SupportFaceShape.SMALL),
                    up);
        });
        sideEndModel(woodType, SUPPORT, "_inventory", texture);
        sideEndModel(woodType, SUPPORT, "upside_down_", "_inventory", texture);

//        getVariantBuilder((Block) block).forAllStatesExcept(state -> {
////            String horizontal = state.get(ModBlockProperties.HORIZONTAL_BIG) ? "hb" : "hs";
////            String vertical = state.get(ModBlockProperties.VERTICAL_BIG) ? "vb" : "vs";
//            boolean up = state.get(BlockStateProperties.UP);
//            int rotation = (int) state.get(BlockStateProperties.HORIZONTAL_FACING).getOpposite().getHorizontalAngle();
//            return ConfiguredModel.builder().modelFile(
//                    withSideEndTextures(createModel(woodType, WoodDecorativeBlockTypes.SUPPORT, up ? "" : "upside_down_", "_" + horizontal + "_" + vertical), woodType + "_support")
//            ).rotationY(rotation).build();
//        }, BlockStateProperties.WATERLOGGED);
    }

    @SafeVarargs
    protected final MultiPartBlockStateBuilder addFourDirections(MultiPartBlockStateBuilder builder, ModelFile modelFile, Property.ValuePair... conditions) {
        addConditions(builder.part().modelFile(modelFile).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), conditions).end();
        addConditions(builder.part().modelFile(modelFile).rotationY(90).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), conditions).end();
        addConditions(builder.part().modelFile(modelFile).rotationY(180).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), conditions).end();
        addConditions(builder.part().modelFile(modelFile).rotationY(270).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), conditions).end();
        return builder;
    }

    @SafeVarargs
    private final MultiPartBlockStateBuilder.PartBuilder addConditions(MultiPartBlockStateBuilder.PartBuilder builder, Property.ValuePair... conditions) {
        Map<Property, List<Comparable<?>>> allConditions = new HashMap<>();
        for (Property.ValuePair condition : conditions) {
            List<Comparable<?>> sameConditions = new ArrayList<>();
            for (Property.ValuePair valuePair : conditions) {
                if (condition.getProperty() == valuePair.getProperty()) {
                    sameConditions.add(valuePair.value());
                }
            }
            allConditions.put(condition.getProperty(), sameConditions);
        }
        for (Map.Entry<Property, List<Comparable<?>>> entry : allConditions.entrySet()) {
            builder.conditions.putAll(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    public void seatBlock(IWoodenBlock block) {
        IWoodType woodType = block.getWoodType();
        String texture = woodType + "_seat";
        MultiPartBlockStateBuilder builder = getMultipartBuilder((Block) block);
        addFourDirections(builder, simpleModel(woodType, SEAT, "", texture));
        builder.part().modelFile(simpleModel(woodType, SEAT, "_post", texture)).addModel().condition(BlockStateProperties.ATTACHED, true);
        builder.part().modelFile(simpleModel(woodType, SEAT, "_top_post", texture)).addModel().condition(ModBlockProperties.POST, true);
        simpleModel(woodType, SEAT, "_inventory", texture);
        simpleModel(woodType, SEAT, "_post_inventory", texture);
        //        getVariantBuilder((Block) block)
//                .forAllStatesExcept(state -> {
//                    String suffix = state.get(ModBlockProperties.POST) ? "_tall" : "";
//                    suffix += state.get(BlockStateProperties.ATTACHED) ? "_with_post" : "";
//                    suffix += state.get(BlockStateProperties.ATTACHED) ? "_with_post" : "";
//                    int rotation = (int) state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle();
//                    return ConfiguredModel.builder().modelFile(simpleModel(woodType, SEAT, suffix, texture)).rotationY(rotation).build();
//                }, BlockStateProperties.WATERLOGGED, BlockStateProperties.OCCUPIED);
    }

    public void beamBlock(IWoodenBlock block) {
        IWoodType woodType = block.getWoodType();
        ModelFile beamXModel = beamModel(woodType, Direction.Axis.X);
        ModelFile beamYModel = beamModel(woodType, Direction.Axis.Y);
        ModelFile beamZModel = beamModel(woodType, Direction.Axis.Z);
        getVariantBuilder((Block) block)
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.X).modelForState().modelFile(beamXModel).addModel()
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y).modelForState().modelFile(beamYModel).addModel()
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z).modelForState().modelFile(beamZModel).addModel();
    }

    public ModelFile beamModel(IWoodType wood, Direction.Axis axis) {
        ModelBuilder<?> builder = createModel(wood, BEAM, "_" + axis.getName());
        return withSideEndTextures(builder, wood + "_beam");
    }

    public void palisadeBlock(IWoodenBlock block) {
        IWoodType woodType = block.getWoodType();
        String texture = woodType + "_palisade";

        ModelFile postModel = sideEndModel(woodType, PALISADE, "_post", texture);
        ModelFile sideModel = sideEndModel(woodType, PALISADE, "_side", texture);
        ModelFile invModel = sideEndModel(woodType, PALISADE, "_inventory", texture);

        getMultipartBuilder(((PalisadeBlock) block)).part().modelFile(postModel).addModel().end()
                .part().modelFile(sideModel).uvLock(true).addModel().condition(BlockStateProperties.NORTH, Boolean.TRUE).end()
                .part().modelFile(sideModel).uvLock(true).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, Boolean.TRUE).end()
                .part().modelFile(sideModel).uvLock(true).rotationY(90).addModel().condition(BlockStateProperties.EAST, Boolean.TRUE).end()
                .part().modelFile(sideModel).uvLock(true).rotationY(270).addModel().condition(BlockStateProperties.WEST, Boolean.TRUE).end();
    }

    private ModelFile sideEndModel(IWoodType wood, WoodDecorativeBlockTypes blockType, String modelSuffix, String texture) {
        return withSideEndTextures(createModel(wood, blockType, modelSuffix), texture);
    }

    private ModelFile sideEndModel(IWoodType wood, WoodDecorativeBlockTypes blockType, String modelPrefix, String modelSuffix, String texture) {
        return withSideEndTextures(createModel(wood, blockType, modelPrefix, modelSuffix), texture);
    }

    private ModelFile simpleModel(IWoodType wood, WoodDecorativeBlockTypes blockType, String modelSuffix, String texture) {
        return withParticleTexture(createModel(wood, blockType, modelSuffix), texture);
    }

    private ModelBuilder<?> createModel(IWoodType wood, WoodDecorativeBlockTypes type) {
        return createModel(wood, type, "");
    }

    private ModelBuilder<?> createModel(IWoodType wood, WoodDecorativeBlockTypes type, String suffix) {
        return createModel(wood, type, "", suffix);
    }

    private ModelBuilder<?> createModel(IWoodType wood, WoodDecorativeBlockTypes type, String prefix, String suffix) {
        String name = prefix + type + suffix;
        return models().getBuilder(wood + "_" + name)
                .parent(modelFile(modLoc("custom/" + name)));
    }

    private ModelBuilder<?> withParticleTexture(ModelBuilder<?> model, String name) {
        ResourceLocation texture = modBlockLoc(name);
        return model.texture("particle", texture).texture("texture", texture);
    }

    private ModelBuilder<?> withSideEndTextures(ModelBuilder<?> model, String name) {
        ResourceLocation side = modBlockLoc(name + "_side");
        ResourceLocation end = modBlockLoc(name + "_end");
        return model.texture("particle", side).texture("side", side).texture("end", end);
    }

    public BlockModelProvider models() {
        return provider.models();
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
        return provider.getMultipartBuilder(b);
    }

    public VariantBlockStateBuilder getVariantBuilder(Block b) {
        return provider.getVariantBuilder(b);
    }

    protected ModelFile modelFile(ResourceLocation loc) {
        return new ModelFile.UncheckedModelFile(loc);
    }

    protected ResourceLocation modBlockLoc(String name) {
        return modLoc("block/" + name);
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(modid, name);
    }
}
