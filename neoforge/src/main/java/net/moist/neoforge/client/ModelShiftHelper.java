package net.moist.neoforge.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ModelShiftHelper {
	public static final ModelProperty<Boolean> NEEDS_SHIFT = new ModelProperty<>();
	public static final ModelProperty<Float> SHIFT_AMOUNT = new ModelProperty<>();

	public static List<BakedQuad> shiftQuads(List<BakedQuad> originalQuads, float shiftY) {
		List<BakedQuad> newQuads = new ArrayList<>();
		Vector3f shiftVec = new Vector3f(0, shiftY, 0);

		for (BakedQuad quad : originalQuads) {newQuads.add(transformQuad(quad, shiftVec));}
		return newQuads;
	}

	private static BakedQuad transformQuad(BakedQuad original, Vector3f translation) {
		int[] vertexData = original.getVertices();
		int[] newVertexData = new int[vertexData.length];
		System.arraycopy(vertexData, 0, newVertexData, 0, vertexData.length);

		int vertexSize = newVertexData.length / 4; // Find the Number of ints per vertex
		for (int i = 0; i < 4; i++) {
			int offset = i * vertexSize;
			float x = Float.intBitsToFloat(newVertexData[offset]);float y = Float.intBitsToFloat(newVertexData[offset + 1]);float z = Float.intBitsToFloat(newVertexData[offset + 2]);
			x += translation.x();y += translation.y();z += translation.z();
			newVertexData[offset] = Float.floatToRawIntBits(x);newVertexData[offset + 1] = Float.floatToRawIntBits(y);newVertexData[offset + 2] = Float.floatToRawIntBits(z);
		}
		return new BakedQuad(newVertexData, original.getTintIndex(), original.getDirection(), original.getSprite(), original.isShade());
	}
}
