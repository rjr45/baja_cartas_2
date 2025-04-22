package org.rjr.baja_cartas.app.enums;

public enum Rarity {
    PROMO("0", "promo", "Promocional"),
    LEGENDARIA("1", "legendaria", "Legendaria"),
    ULTRA_REAL("2", "ultra-real", "Ultra Real"),
    MEGA_REAL("3", "mega-real", "Mega Real"),
    REAL("4", "real", "Real"),
    CORTESANO("5", "cortesano", "Cortesano"),
    VASALLO("6", "vasallo", "Vasallo"),
    ORO("7", "oro", "Oro"),
    MILENARIA("8", "milenaria", "Milenaria"),
    SECRETA("9", "secreta", "Secreta"),
    FICHA("10", "ficha", "Ficha"),
    SET_PARALELO("11", "set_paralelo", "Set Paralelo");

    private final String id;
    private final String slug;
    private final String name;

    Rarity(String id, String slug, String name) {
        this.id = id;
        this.slug = slug;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getDisplayName() {
        return name;
    }

    public static Rarity fromSlug(String slug) {
        for (Rarity r : values()) {
            if (r.slug.equalsIgnoreCase(slug)) {
                return r;
            }
        }
        return null;
    }

    public static Rarity fromId(String id) {
        for (Rarity r : values()) {
            if (r.id.equals(id)) {
                return r;
            }
        }
        return null;
    }
}
