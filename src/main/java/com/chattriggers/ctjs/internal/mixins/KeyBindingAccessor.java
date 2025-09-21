package com.chattriggers.ctjs.internal.mixins;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC<=12108
//$$import java.util.Map;
//$$import java.util.Set;
//#else
import java.util.List;
//#endif

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    //#if MC<=12108
    //$$@Accessor("CATEGORY_ORDER_MAP")
    //$$static Map<String, Integer> getCategoryMap() {
    //$$    throw new IllegalStateException();
    //$$}
    //$$@Accessor("KEY_CATEGORIES")
    //$$static Set<String> getKeyCategories() {
    //$$    throw new IllegalStateException();
    //$$}
    //#else
    @Mixin(KeyBinding.Category.class)
    interface Category {
        @Accessor("CATEGORIES")
        static List<KeyBinding.Category> getCategoryList() { throw new IllegalStateException(); }
    }
    //#endif


    @Accessor
    InputUtil.Key getBoundKey();

    @Accessor
    int getTimesPressed();
}
