package me.decce.transformingbase.service.fabric;

//? fabric {
//? >=26 {
/*import dev.jfronny.libjf.unsafe.asm.AsmConfig;
import dev.jfronny.libjf.unsafe.asm.patch.Patch;
*///? } else {
import io.gitlab.jfronny.libjf.unsafe.asm.AsmConfig;
import io.gitlab.jfronny.libjf.unsafe.asm.patch.Patch;
//? }
import me.decce.transformingbase.service.transform.CommonTransformer;

import java.util.Set;

public class LibJFEntrypoint implements AsmConfig {
    @Override
    public Set<String> skipClasses() {
        return null;
    }

    @Override
    public Set<Patch> getPatches() {
        return Set.of(CommonTransformer::process);
    }
}
//? }