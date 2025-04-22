package org.rjr.baja_cartas.app.enums;

public enum CardType {
    ALIADO("1", "aliado", "Aliado"),
    TALISMAN("2", "talisman", "Talismán"),
    ARMA("3", "arma", "Arma"),
    TOTEM("4", "totem", "Tótem"),
    ORO("5", "oro", "Oro"),
    MONUMENTO("6", "monumento", "Monumento");

    private final String id;
    private final String slug;
    private final String name;

    CardType(String id, String slug, String name) {
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

    // Buscar por slug
    public static CardType fromSlug(String slug) {
        for (CardType t : values()) {
            if (t.slug.equalsIgnoreCase(slug)) {
                return t;
            }
        }
        return null;
    }

    // Buscar por id
    public static CardType fromId(String id) {
        for (CardType t : values()) {
            if (t.id.equals(id)) {
                return t;
            }
        }
        return null;
    }
}
