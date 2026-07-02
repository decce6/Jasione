package me.decce.transformingbase.service.transform;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InterpreterEx extends Interpreter<SourceValueEx> {
    // Since our escape analysis is rather pessimistic, as long as the returned array gets passed into another method we consider it dangerous for cache
    // This set is an extra optimization that allows Arrays.stream calls to use cached array
    private static final Set<MethodHolder> SAFE_ARRAY_CONSUMERS = Set.of(
        new MethodHolder("java/util/Arrays", "stream", "([Ljava/lang/Object;)Ljava/util/stream/Stream;"),
        new MethodHolder("java/util/Arrays", "stream", "([Ljava/lang/Object;II)Ljava/util/stream/Stream;")
    );

    private final SourceInterpreter sourceInterpreter;
    private final AbstractInsnNode valuesInsn;
    private boolean violation;

    public InterpreterEx(AbstractInsnNode valuesInsn) {
        super(Opcodes.ASM9);
        this.sourceInterpreter = new SourceInterpreter();
        this.valuesInsn = valuesInsn;
    }

    protected void violate() {
        this.violation = true;
    }

    protected void violateIf(boolean bl) {
        if (bl) {
            violate();
        }
    }

    public boolean hasViolation() {
        return this.violation;
    }

    @Override
    public SourceValueEx newValue(Type type) {
        return SourceValueEx.of(sourceInterpreter.newValue(type));
    }

    @Override
    public SourceValueEx newOperation(AbstractInsnNode insn) {
        return SourceValueEx.of(sourceInterpreter.newOperation(insn));
    }

    @Override
    public SourceValueEx copyOperation(AbstractInsnNode insn, SourceValueEx value) {
        return SourceValueEx.of(sourceInterpreter.copyOperation(insn, value.sourceValue)).trackIf(value.tracking);
    }

    @Override
    public SourceValueEx unaryOperation(AbstractInsnNode insn, SourceValueEx value) {
        var opcode = insn.getOpcode();
        violateIf(value.tracking && opcode == Opcodes.PUTSTATIC);
        return SourceValueEx.of(sourceInterpreter.unaryOperation(insn, value.sourceValue)).trackIf(value.tracking && opcode == Opcodes.CHECKCAST);
    }

    @Override
    public SourceValueEx binaryOperation(AbstractInsnNode insn, SourceValueEx value1, SourceValueEx value2) {
        var opcode = insn.getOpcode();
        violateIf(value2.tracking && opcode == Opcodes.PUTFIELD);
        return SourceValueEx.of(sourceInterpreter.binaryOperation(insn, value1.sourceValue, value2.sourceValue));
    }

    @Override
    public SourceValueEx ternaryOperation(AbstractInsnNode insn, SourceValueEx value1, SourceValueEx value2, SourceValueEx value3) {
        violateIf(value1.tracking); // Returned array will be mutated
        return SourceValueEx.of(sourceInterpreter.ternaryOperation(insn, value1.sourceValue, value2.sourceValue, value3.sourceValue));
    }

    @Override
    public SourceValueEx naryOperation(AbstractInsnNode insn, List<? extends SourceValueEx> values) {
        boolean isValuesInsn = insn == valuesInsn;

        if (!isValuesInsn) { // skip the Enum#values() call itself
            boolean isSafeConsumer = insn instanceof MethodInsnNode methodInsnNode &&
                    SAFE_ARRAY_CONSUMERS.contains(MethodHolder.of(methodInsnNode));
            if (!isSafeConsumer){
                for (var value : values) {
                    // result of values() escaped into another method
                    if (value.tracking) {
                        violate();
                        break;
                    }
                }
            }
        }

        List<SourceValue> list = new ArrayList<>(values.size());
        for (var value : values) {
            list.add(value.sourceValue);
        }
        return SourceValueEx.of(sourceInterpreter.naryOperation(insn, list)).trackIf(isValuesInsn);
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, SourceValueEx value, SourceValueEx expected) {
        violateIf(value.tracking);
        sourceInterpreter.returnOperation(insn, value.sourceValue, expected.sourceValue);
    }

    @Override
    public SourceValueEx merge(SourceValueEx value1, SourceValueEx value2) {
        var merged = sourceInterpreter.merge(value1.sourceValue, value2.sourceValue);
        if (merged == value1.sourceValue) {
            return value1.trackIf(value2.tracking);
        } else if (merged == value2.sourceValue) {
            return value2.trackIf(value1.tracking);
        }
        return SourceValueEx.of(merged).trackIf(value1.tracking || value2.tracking);
    }
}
