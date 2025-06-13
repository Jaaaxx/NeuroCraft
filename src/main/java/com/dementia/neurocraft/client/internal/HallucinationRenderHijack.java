package com.dementia.neurocraft.client.internal;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.dementia.neurocraft.Neurocraft.LOGGER;
import static com.dementia.neurocraft.client.internal.PlayerHallucinations.getRandomName;

public final class HallucinationRenderHijack {

    public static boolean done;
    public static PlayerRenderer PLAYER_RENDERER;

    public static void apply() {
        if (done) return;
        done = true;

        ByteBuddyAgent.install();

        new ByteBuddy()
                .redefine(LivingEntityRenderer.class)
                .visit(Advice.to(RenderAdvice.class).on(
                        ElementMatchers.isPublic()
                                .and(ElementMatchers.returns(void.class))
                                .and(ElementMatchers.takesArguments(6))
                                .and(ElementMatchers.takesArgument(0, LivingEntity.class))
                                .and(ElementMatchers.takesArgument(3, PoseStack.class))
                ))
                .make()
                .load(LivingEntityRenderer.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());

        LOGGER.info("Hallucination render hijack installed.");
    }

    public static final class DummyPlayer extends AbstractClientPlayer {

        public DummyPlayer(ClientLevel lvl, GameProfile gp) {
            super(lvl, gp);
        }

        @Override
        public PlayerSkin getSkin() {
            return PlayerHallucinations.getSkin(getName().getString());
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative()  { return false; }
    }

    public static final class WalkAnimMirror {
        public static final Field POS, SPD, SPD_OLD;
        static {
            try {
                POS = WalkAnimationState.class.getDeclaredField("position");
                SPD = WalkAnimationState.class.getDeclaredField("speed");
                SPD_OLD = WalkAnimationState.class.getDeclaredField("speedOld");
                AccessibleObject.setAccessible(new AccessibleObject[]{POS, SPD, SPD_OLD}, true);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        public static void copy(WalkAnimationState from, WalkAnimationState to) {
            try {
                POS.setFloat(to, POS.getFloat(from));
                SPD.setFloat(to, SPD.getFloat(from));
                SPD_OLD.setFloat(to, SPD_OLD.getFloat(from));
            } catch (IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        }
    }

    // Obfuscation-safe, lazy reflection for previous attack animation
    public static final class LivingAnimMirror {
        private static Field ATTACK_OLD;
        private static boolean initialized = false;

        private static void init() {
            if (initialized) return;
            initialized = true;
            // Try Yarn and known obf names for oAttackAnim
            String[] names = {"oAttackAnim", "aN", "f_20920_"};
            for (String name : names) {
                try {
                    ATTACK_OLD = ObfuscationReflectionHelper.findField(LivingEntity.class, name);
                    ATTACK_OLD.setAccessible(true);
                    LOGGER.info("[RenderHijack] Found oAttackAnim field as '{}'", name);
                    return;
                } catch (Exception e) {
                    // continue
                }
            }
            LOGGER.warn("[RenderHijack] Could not find oAttackAnim field (tried {})", Arrays.toString(names));
        }

        public static float getAttackOld(LivingEntity e) {
            init();
            if (ATTACK_OLD == null) return 0f;
            try {
                return ATTACK_OLD.getFloat(e);
            } catch (IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        }

        public static void setAttackOld(LivingEntity e, float v) {
            init();
            if (ATTACK_OLD == null) return;
            try {
                ATTACK_OLD.setFloat(e, v);
            } catch (IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        }
    }

    public static final class RenderAdvice {
        public static final Map<LivingEntity, DummyPlayer> DUMMIES = new WeakHashMap<>();
        public static final Map<LivingEntity, GameProfile> PROFILES = new WeakHashMap<>();
        public static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);

        public static DummyPlayer getOrCreateDummy(LivingEntity mob) {
            return DUMMIES.computeIfAbsent(mob, m -> {
                GameProfile gp = PROFILES.computeIfAbsent(m, RenderAdvice::randomProfile);
                return new DummyPlayer((ClientLevel) m.level(), gp);
            });
        }

        public static GameProfile randomProfile(LivingEntity ignored) {
            String name = getRandomName();
            return new GameProfile(UUID.randomUUID(), name); // UUID is irrelevant; skin supplied manually
        }

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        public static boolean enter(@Advice.Argument(0) LivingEntity mob,
                                    @Advice.Argument(1) float yaw,
                                    @Advice.Argument(2) float pt,
                                    @Advice.Argument(3) PoseStack pose,
                                    @Advice.Argument(4) MultiBufferSource buf,
                                    @Advice.Argument(5) int light) {

            if (!EntityRandomizer.shouldRenderAsPlayer(mob) || REENTRY.get())
                return false;

            DummyPlayer dummy = EntityRandomizer.getOrCreateDummy(mob);
            mirrorState(mob, dummy, pt);

            REENTRY.set(true);
            try {
                EntityRandomizer.getRenderer().render(dummy, yaw, pt, pose, buf, light);
            } finally {
                REENTRY.set(false);
            }
            return true;
        }

        /* ---------- state mirroring ---------- */

        public static void mirrorState(LivingEntity src, DummyPlayer dst, float pt) {

            /* absolute position */
            dst.setPos(src.getX(), src.getY(), src.getZ());
            dst.xo = src.xo;
            dst.yo = src.yo;
            dst.zo = src.zo;

            boolean newTick = dst.tickCount != src.tickCount;
            if (newTick) {
                dst.xRotO = src.xRotO;
                dst.yRotO = src.yRotO;
                dst.yBodyRotO = src.yBodyRotO;
                dst.yHeadRotO = src.yHeadRotO;

                LivingAnimMirror.setAttackOld(dst, LivingAnimMirror.getAttackOld(src));

                double dx = src.getX() - src.xo;
                double dz = src.getZ() - src.zo;
                float speed = Math.min((float) Math.sqrt(dx * dx + dz * dz) * 4.0F, 1.0F);
                dst.walkAnimation.update(speed, 1.0F);

                dst.tickCount = src.tickCount;
            }

            dst.setYRot(src.getYRot());
            dst.setXRot(src.getXRot());
            dst.yBodyRot = src.yBodyRot;
            dst.yHeadRot = src.yHeadRot;
            dst.attackAnim = src.attackAnim;

            dst.hurtTime = src.hurtTime;
            dst.setPose(src.getPose());
        }
    }
}
