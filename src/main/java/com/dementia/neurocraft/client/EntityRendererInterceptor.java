package com.dementia.neurocraft.client;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class EntityRendererInterceptor {

    private static volatile boolean injected;

    public static void injectTextureAdvice() {
        if (injected) return;
        injected = true;

        ByteBuddyAgent.install();

        EntityRenderDispatcher disp = Minecraft.getInstance().getEntityRenderDispatcher();
        Map<?, EntityRenderer<?>> renderers = disp.renderers;

        for (EntityRenderer<?> r : renderers.values()) {
            Class<?> c = r.getClass();
            try {
                new ByteBuddy()
                        .redefine(c)
                        .visit(Advice.to(TextureAdvice.class)
                                .on(ElementMatchers.named("getTextureLocation")))
                        .make()
                        .load(c.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

                System.out.println("patched: " + c.getName());
            } catch (Throwable t) {
                System.err.println("failed:  " + c.getName());
                t.printStackTrace();
            }
        }
    }

    /** executes before each getTextureLocation(..) and rewrites the return value */
    static class TextureAdvice {

        @Advice.OnMethodExit                         // runs after original returns
        static void exit(@Advice.Return(readOnly = false) ResourceLocation ret) {
            if (!com.dementia.neurocraft.client.RandomizeTextures.crazyRenderingActive) return;

            AbstractClientPlayer p = (AbstractClientPlayer) Minecraft.getInstance().player;
            if (p == null) return;

            ResourceLocation skin = p.getSkin().texture();
            ret = skin;                              // replace vanilla texture
        }
    }
}
