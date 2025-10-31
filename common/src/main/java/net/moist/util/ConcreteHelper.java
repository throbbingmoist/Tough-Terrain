package net.moist.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public record ConcreteHelper(Block powder, Block concrete, int powder_color, int concrete_color) {
	public ConcreteHelper(Block powder, Block concrete) {this(powder, concrete, powder.defaultMapColor(), concrete.defaultMapColor());}
	public ConcreteHelper(Block powder, Block concrete, MapColor powder_color, MapColor concrete_color) {this(powder, concrete, getDefaultColor(powder_color), getDefaultColor(concrete_color));}

	public static int getDefaultColor(MapColor color) {
		int rgb = color.calculateRGBColor(MapColor.Brightness.HIGH);
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return ((b << 16) | (g << 8) | r);
	}
	public static Vector3f getVectorColor(MapColor color) {
		return Vec3.fromRGB24(getDefaultColor(color)).toVector3f();
	}


	public Block getPowder() {return powder;}
	public BlockState getPowderState() {return powder.defaultBlockState();}
	public int getPowderColor() {return powder_color;}

	public Block getConcrete() {return concrete;}
	public BlockState getConcreteState() {return concrete.defaultBlockState();}
	public int getConcreteColor() {return concrete_color;}

	public int getColor(Block block	) {
		if (block.equals(powder)) {
			return getPowderColor();
		} else if (block.equals(concrete)) {
			return getConcreteColor();
		} return -1;
	}
}
