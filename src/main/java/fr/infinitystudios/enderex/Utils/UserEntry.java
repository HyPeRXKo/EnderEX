package fr.infinitystudios.enderex.Utils;

import java.util.UUID;

public record UserEntry(
        int id,
        String name,
        UUID uuid,
        Platform platform
) {}
