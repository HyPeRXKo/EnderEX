package fr.infinitystudios.enderex.Commands;

import fr.infinitystudios.enderex.Utils.Platform;

import java.util.UUID;

public record ResolvedUser(
        int id,
        String name,
        UUID uuid,
        Platform platform,
        boolean online
) {}