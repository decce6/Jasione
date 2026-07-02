package me.decce.transformingbase.service.transform;

import org.objectweb.asm.tree.analysis.SourceValue;
import org.objectweb.asm.tree.analysis.Value;

public class SourceValueEx implements Value {
    public final SourceValue sourceValue;
    public boolean tracking; // true = from Enum#values()

    public SourceValueEx(SourceValue sourceValue) {
        this.sourceValue = sourceValue;
    }

    public static SourceValueEx of(SourceValue sourceValue) {
        return sourceValue == null ? null : new SourceValueEx(sourceValue);
    }

    public SourceValueEx trackIf(boolean bl) {
        if (bl) {
            this.tracking = true;
        }
        return this;
    }

    @Override
    public int getSize() {
        return sourceValue.getSize();
    }
}
