package net.moist.block.content;

import net.minecraft.world.level.block.Block;
import net.moist.util.ConcreteHelper;

public class ConcretePowderLayer extends FallingLayer{
	private final ConcreteHelper concretePowderHelper;
	public ConcretePowderLayer(Properties properties, ConcreteHelper concretePowderHelper) {
		super(properties, false, concretePowderHelper.concrete(), true);
		this.concretePowderHelper = concretePowderHelper;
	}
	public ConcretePowderLayer(Properties properties, ConcreteHelper concretePowderHelper, boolean overgrowable) {
		super(properties, overgrowable, concretePowderHelper.concrete(), true);
		this.concretePowderHelper = concretePowderHelper;
	}
	public ConcretePowderLayer(Properties properties, ConcreteHelper concretePowderHelper, boolean overgrowable, Block packingBlock) {
		super(properties, overgrowable, concretePowderHelper.concrete(), true);
		this.concretePowderHelper = concretePowderHelper;
	}

	public ConcreteHelper getConcretePowderHelper() {
		return this.concretePowderHelper;
	}
}
