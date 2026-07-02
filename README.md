# Jasione

## Overview

Jasione is an optimization mod that reduces memory allocation (GC pressure).

The Java compiler generates, for each enum class, a `values()` method that can be used to retrieve all enum values as an array. This method clones the array before returning it, ensuring that callers of this method can safely mutate the array without affecting other callers. This, however, incurs a performance cost. When invoked in hot paths, these small memory allocations accumulate and increase GC pressure.

Jasione addresses this issue by redirecting the `values()` calls that do not mutate the returned array to use a cache, eliminating the cloning overhead and the memory allocation. For details on how this is implemented safely, see to the "Technical Details" section below.

## Configuration

Jasione currently offers several debug options, which can be used to inspect the optimization result. The configuration resides in `config/jasione.toml`.

```toml
#Specifies whether to enable the mod
enabled = true

[debug]
	#When enabled, logs statistics when optimizations are applied
	printOptimization = false
	#When enabled, dumps transformed classes to the ".jasione.out" folder
	dumpClasses = false
```

## FAQs

**Doesn't the JIT optimize the Enum#values() calls when they're safe?**

No, the JIT compiler doesn't currently recognize even the most trivial use patterns like iterating through the values array. The cloning and memory allocation overhead persists, per [JMH tests](https://github.com/decce6/Jasione_JMH).

**NeoForge displays a warning about this mod. Why?**

When running this mod on the latest NeoForge (1.21.11+), you may encounter this warning:

```
[Render thread/ERROR] [ne.ne.fm.cl.tr.ClassTransformStatistics/]: Class processor jasione:main transformed 100.00% of loaded class which is suspiciously high; it may be attempting mass-ASM. Please report this to the mod author.
```

According to members of the NeoForge team, this message essentially acts as a nudge for mod authors to identify whether the use case is necessary. For Jasione, this transformation is necessary. (See the previous question)

For reference, see [discussion on "The NeoForged Project" discord server](https://discord.com/channels/313125603924639766/1516470112658657443/1521489234379411497) (note that the discussion involved different implementations which didn't become the final version due to various issues. See the "Technical Details" section for more detail.)

## Technical Details

The contract of `Enum#values` (where `Enum` refers to any enum class) is that the returned array is fresh and can be freely mutated. Therefore, we must identify which callers are safe to optimize by analyzing whether they mutate the array or the array escapes. This is made possible by utilizing the asm-analysis library. If array mutation or escapes are detected via bytecode analysis, the call remains unchanged to preserve correctness.

We need to find a place to store the cached values array, because the synthetic `$VALUES` field for enum classes is private. The current implementation performs runtime class generation. For each enum class, a corresponding cache holder class is generated to store the values array as a `static final` field. For example, when the transformer first detects use of `net.minecraft.core.Direction#values`, it generates the class `me.decce.jasione.cached.net.minecraft.core.Direction`, which holds a cached values array and various other metadata to ensure correctness. With this, it can transform all callers of the original `values()` method to use the cached class.
