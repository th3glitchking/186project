//Made with Blockbench
//Paste this code into your mod.

import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class custom_model extends ModelBase {
	private final ModelRenderer bb_main;
	private final ModelRenderer flying_1;
	private final ModelRenderer holdy_bits;
	private final ModelRenderer blades;
	private final ModelRenderer flying_2;
	private final ModelRenderer holdy_bits2;
	private final ModelRenderer blades2;
	private final ModelRenderer flying_3;
	private final ModelRenderer holdy_bits3;
	private final ModelRenderer blades3;
	private final ModelRenderer flying_4;
	private final ModelRenderer holdy_bits4;
	private final ModelRenderer blades4;

	public custom_model() {
		textureWidth = 32;
		textureHeight = 32;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(bb_main, 0.0F, 1.5708F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -4.9983F, -2.0122F, -3.0105F, 10, 2, 6, 0.0F, false));

		flying_1 = new ModelRenderer(this);
		flying_1.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(flying_1, 0.0F, 1.5708F, 0.0F);

		holdy_bits = new ModelRenderer(this);
		holdy_bits.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_1.addChild(holdy_bits);
		holdy_bits.cubeList.add(new ModelBox(holdy_bits, 0, 29, -8.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
		holdy_bits.cubeList.add(new ModelBox(holdy_bits, 0, 29, -7.0F, -3.0F, 3.0F, 2, 1, 1, 0.0F, false));

		blades = new ModelRenderer(this);
		blades.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_1.addChild(blades);
		blades.cubeList.add(new ModelBox(blades, 0, 8, -11.0F, -4.0F, 2.0F, 6, 0, 6, 0.0F, false));

		flying_2 = new ModelRenderer(this);
		flying_2.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(flying_2, 0.0F, -1.5708F, 0.0F);

		holdy_bits2 = new ModelRenderer(this);
		holdy_bits2.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_2.addChild(holdy_bits2);
		holdy_bits2.cubeList.add(new ModelBox(holdy_bits2, 0, 29, -8.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
		holdy_bits2.cubeList.add(new ModelBox(holdy_bits2, 0, 29, -7.0F, -3.0F, 3.0F, 2, 1, 1, 0.0F, false));

		blades2 = new ModelRenderer(this);
		blades2.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_2.addChild(blades2);
		blades2.cubeList.add(new ModelBox(blades2, 0, 8, -11.0F, -4.0F, 2.0F, 6, 0, 6, 0.0F, false));

		flying_3 = new ModelRenderer(this);
		flying_3.setRotationPoint(0.0F, 24.0F, 0.0F);

		holdy_bits3 = new ModelRenderer(this);
		holdy_bits3.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_3.addChild(holdy_bits3);
		holdy_bits3.cubeList.add(new ModelBox(holdy_bits3, 0, 29, -5.0F, -4.0F, 7.0F, 1, 1, 1, 0.0F, false));
		holdy_bits3.cubeList.add(new ModelBox(holdy_bits3, 0, 29, -4.0F, -3.0F, 5.0F, 1, 1, 2, 0.0F, false));

		blades3 = new ModelRenderer(this);
		blades3.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_3.addChild(blades3);
		blades3.cubeList.add(new ModelBox(blades3, 0, 8, -8.0F, -4.0F, 5.0F, 6, 0, 6, 0.0F, false));

		flying_4 = new ModelRenderer(this);
		flying_4.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(flying_4, 0.0F, 3.1416F, 0.0F);

		holdy_bits4 = new ModelRenderer(this);
		holdy_bits4.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_4.addChild(holdy_bits4);
		holdy_bits4.cubeList.add(new ModelBox(holdy_bits4, 0, 29, -5.0F, -4.0F, 7.0F, 1, 1, 1, 0.0F, false));
		holdy_bits4.cubeList.add(new ModelBox(holdy_bits4, 0, 29, -4.0F, -3.0F, 5.0F, 1, 1, 2, 0.0F, false));

		blades4 = new ModelRenderer(this);
		blades4.setRotationPoint(0.0F, 0.0F, 0.0F);
		flying_4.addChild(blades4);
		blades4.cubeList.add(new ModelBox(blades4, 0, 8, -8.0F, -4.0F, 5.0F, 6, 0, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bb_main.render(f5);
		flying_1.render(f5);
		flying_2.render(f5);
		flying_3.render(f5);
		flying_4.render(f5);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}