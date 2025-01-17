package net.spell_power.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.world.World;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;
import net.spell_power.config.AttributesConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    LivingEntityMixin(final EntityType<?> type, final World world) {
        super(type, world);
    }

    @Inject(
            method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;",
            require = 1, allow = 1, at = @At("RETURN")
    )
    private static void addAttributes(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        if (SpellPowerMod.attributeScope() == AttributesConfig.AttributeScope.LIVING_ENTITY) {
            for (var entry : SpellPowerMechanics.all.entrySet()) {
                var secondary = entry.getValue();
                info.getReturnValue().add(secondary.attribute);
            }
            for (var school: SpellSchools.all()) {
                if (school.attributeManagement.isInternal()) {
                    var attribute = school.attribute;
                    info.getReturnValue().add(attribute);
                }
            }
        }
    }
}