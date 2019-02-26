/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.server.mixin.core.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.entity.EntityUtil;
import org.spongepowered.common.interfaces.world.IMixinITeleporter;

import javax.annotation.Nullable;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends MixinEntityPlayer {

    private static final String PERSISTED_NBT_TAG = "PlayerPersisted";

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void onClonePlayerReturn(EntityPlayerMP oldPlayer, boolean respawnFromEnd, CallbackInfo ci) {
        this.spawnChunkMap = ((MixinEntityPlayer) (Object) oldPlayer).spawnChunkMap;
        this.spawnForcedSet = ((MixinEntityPlayer) (Object) oldPlayer).spawnForcedSet;

        final NBTTagCompound old = ((MixinEntityPlayer) (Object) oldPlayer).getEntityData();
        if (old.contains(PERSISTED_NBT_TAG)) {
            this.getEntityData().put(PERSISTED_NBT_TAG, old.getCompound(PERSISTED_NBT_TAG));
        }
    }

    /**
     * @author gabizou - April 7th, 2018
     * @author JBYoshi - July 19, 2018 - Copy to SpongeVanilla
     * @author Zidane - February 26th, 2019 - Version 1.13
     * @reason Re-route teleportation logic to common
     */
    @Overwrite
    @Nullable
    public Entity func_212321_a(DimensionType dimensionType) {
        if (!this.world.isRemote && !this.removed) {
            // Sponge Start - Handle teleportation solely in TrackingUtil where everything can be debugged.
            return EntityUtil.teleportPlayerToDimension((EntityPlayerMP) (Object) this, dimensionType,
                    (IMixinITeleporter) SpongeImpl.getServer().getWorld(dimensionType).getDefaultTeleporter());
            // Sponge End
        }
        return null;
    }
}
