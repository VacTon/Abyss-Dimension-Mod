package io.github.vacton.abyss.client;

import io.github.vacton.abyss.Abyss;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;

/** Vanilla zombie model and animations, with the Abyss Zombie texture. */
public class AbyssZombieRenderer extends ZombieRenderer {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(Abyss.MOD_ID, "textures/entity/abyss_zombie.png");

    public AbyssZombieRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(ZombieRenderState state) {
        return TEXTURE;
    }
}