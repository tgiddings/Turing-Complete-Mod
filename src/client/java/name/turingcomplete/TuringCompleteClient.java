package name.turingcomplete;

import name.turingcomplete.color.BlockTint;
import name.turingcomplete.init.blockInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class TuringCompleteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// To make some parts of the block transparent (like glass, saplings and doors):
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
			blockInit.NOT_GATE,
			blockInit.NAND_GATE,
			blockInit.NOR_GATE,
			blockInit.XNOR_GATE,
			blockInit.OMNI_DIRECTIONAL_REDSTONE_BRIDGE_BLOCK,
			blockInit.LOGIC_BASE_PLATE_BLOCK
		);

		BlockTint.create();
	}
}