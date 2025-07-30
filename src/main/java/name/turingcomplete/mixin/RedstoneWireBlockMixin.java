package name.turingcomplete.mixin;

import name.turingcomplete.blocks.ConnectsToRedstone;
import name.turingcomplete.blocks.logicwire.OnePassWireUpdateStrategy;
import net.minecraft.block.RedstoneWireBlock;

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
    private void update_neighborUpdate_tcd_mixin(RedstoneWireBlock instance, World world, BlockPos pos, BlockState state, Operation<Void> original, BlockState stateAgain, World worldAgain, BlockPos posAgain, Block sourceBlock, BlockPos sourcePos, boolean notify){
        new OnePassWireUpdateStrategy().onNeighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }
}
