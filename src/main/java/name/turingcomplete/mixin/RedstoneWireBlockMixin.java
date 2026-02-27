package name.turingcomplete.mixin;

import name.turingcomplete.blocks.ConnectsToRedstone;
import name.turingcomplete.blocks.logicwire.AbstractLogicWire;
import name.turingcomplete.blocks.logicwire.VanillaWireUpdateStrategy;
import net.minecraft.block.RedstoneWireBlock;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
    // ==================================================
    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true, remap = true)
    private static void connectsTo_tcd_mixin(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> e)
    {
        //check for block type
        if (!(state.getBlock() instanceof ConnectsToRedstone logic_block))
            return;

        //check for null direction
        if(dir == null) {
            e.setReturnValue(false);
            e.cancel();
            return;
        }

        // use dustConnectsToThis(state, dir) to determine whether redstone dust should or should not
        // connect to that side of the Logic Gate.
        e.setReturnValue(logic_block.dustConnectsToThis(state, dir));
        e.cancel();
    }

    @WrapOperation(
        method = "neighborUpdate(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;Z)V",
        at = @At(value = "invoke", target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")
    )
    private void update_neighborUpdate_tcd_mixin(
        RedstoneWireBlock instance,
        World update_world,
        BlockPos update_pos,
        BlockState update_state,
        Operation<Void> original,
        BlockState neighborUpdate_state,
        World neighborUpdate_world,
        BlockPos neighborUpdate_pos,
        Block neighborUpdate_sourceBlock,
        BlockPos neighborUpdate_sourcePos, 
        boolean neighborUpdate_notify
    ){
        if(AbstractLogicWire.WireUpdateStrategy instanceof VanillaWireUpdateStrategy){
            original.call(
                instance,
                update_world,
                update_pos,
                update_state);
        }
        else {
            AbstractLogicWire.WireUpdateStrategy.onNeighborUpdate(
                update_state,
                update_world,
                update_pos,
                Optional.of(neighborUpdate_sourceBlock),
                Optional.of(neighborUpdate_sourcePos),
                neighborUpdate_notify);
        }
    }
}
