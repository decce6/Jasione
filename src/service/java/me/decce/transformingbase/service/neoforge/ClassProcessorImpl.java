//? neoforge && >=1.21.10 {
/*package me.decce.transformingbase.service.neoforge;

import me.decce.transformingbase.constants.Constants;
import me.decce.transformingbase.core.Jasione;
import me.decce.transformingbase.service.transform.CommonTransformer;
import net.neoforged.neoforgespi.transformation.ClassProcessor;
import net.neoforged.neoforgespi.transformation.ClassProcessorIds;
import net.neoforged.neoforgespi.transformation.ProcessorName;

import java.util.Set;

public class ClassProcessorImpl implements ClassProcessor {
    public static final ProcessorName NAME = new ProcessorName(Constants.MOD_ID, "main");
    public static final boolean ENABLED = Jasione.getConfig().enabled;
    @Override
    public ProcessorName name() {
        return NAME;
    }

    @Override
    public boolean handlesClass(SelectionContext context) {
        return ENABLED;
    }

    @Override
    public ComputeFlags processClass(TransformationContext context) {
        boolean transformed = CommonTransformer.process(context.node());
        // We have already modified maxStack appropriately - SIMPLE_REWRITE should suffice here
        return transformed ? ComputeFlags.SIMPLE_REWRITE : ComputeFlags.NO_REWRITE;
    }

    @Override
    public OrderingHint orderingHint() {
        return OrderingHint.LATE;
    }

    @Override
    public Set<ProcessorName> runsAfter() {
        return Set.of(ClassProcessorIds.MIXIN, ClassProcessorIds.SIMPLE_PROCESSORS_GROUP);
    }
}
*///? }
