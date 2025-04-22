package org.rjr.baja_cartas.app.enums;

public enum Race {
    NORAZA("0", "noraza", "Sin Raza"),
    CABALLERO("1", "caballero", "Caballero"),
    BESTIA("2", "bestia", "Bestia"),
    ETERNO("3", "eterno", "Eterno"),
    GUERRERO("4", "guerrero", "Guerrero"),
    BARBARO("5", "barbaro", "Bárbaro"),
    FAERIE("6", "faerie", "Faerie"),
    SAMURAI("7", "samurai", "Samurái"),
    SOMBRA("8", "sombra", "Sombra"),
    ANCESTRAL("9", "ancestral", "Ancestral"),
    SACERDOTE("10", "sacerdote", "Sacerdote"),
    DRAGON("11", "dragon", "Dragón"),
    HEROE("12", "heroe", "Héroe"),
    ONI("13", "oni", "Oni"),
    OLIMPICO("14", "olimpico", "Olímpico"),
    TITAN("15", "titan", "Titán"),
    FARAON("16", "faraon", "Faraón"),
    DESAFIANTE("17", "desafiante", "Desafiante"),
    DEFENSOR("18", "defensor", "Defensor"),
    LICANTROPO("19", "licantropo", "Licántropo"),
    VAMPIRO("20", "vampiro", "Vampiro"),
    CAZADOR("21", "cazador", "Cazador"),
    CHAMAN("22", "chaman", "Chamán"),
    DIOS("23", "dios", "Dios"),
    ABOMINACION("24", "abominacion", "Abominación"),
    KAMI("25", "kami", "Kami"),
    XIAN("26", "xian", "Xian"),
    CRIATURAS("27", "criaturas", "Criaturas"),
    CAMPEON_SHAOLIN("28", "campeon/shaolin", "Campeón / Shaolín"),
    CAMPEON_NINJA("29", "campeon/ninja", "Campeón / Ninja"),
    CAMPEON_SAMURAI("30", "campeon/samurai", "Campeón / Samurái"),
    CAMPEON("31", "campeon", "Campeón"),
    HEROE_SACERDOTE("32", "heroe/sacerdote", "Héroe/Sacerdote"),
    ETERNO_SOMBRA("33", "eterno/sombra", "Eterno/Sombra"),
    CABALLERO_GUERRERO("34", "caballero/guerrero", "Caballero/Guerrero"),
    BESTIA_GUERRERO("35", "bestia/guerrero", "Bestia/Guerrero"),
    CABALLERO_HEROE("36", "caballero/heroe", "Caballero/Héroe"),
    DRAGON_ETERNO("37", "dragon/eterno", "Dragón/Eterno"),
    ETERNO_FAERIE("38", "eterno/faerie", "Eterno/Faerie"),
    PALADIN("39", "paladin", "Paladín"),
    ASESINO("40", "asesino", "Asesino"),
    TENEBRIS("41", "tenebris", "Tenebris"),
    ETERNO_SACERDOTE("42", "eterno/sacerdote", "Eterno/Sacerdote"),
    CABALLERO_GUERRERO_HEROE("43", "caballero/guerrero/hero", "Caballero/Guerrero/Héroe"),
    BESTIA_DRAGON_SOMBRA("44", "bestia/dragon/sombra", "Bestia/Dragón/Sombra"),
    ETERNO_FAERIE_SACERDOTE("45", "eterno/faerie/sacerdote", "Eterno/Faerie/Sacerdote"),
    BESTIA_FAERIE("46", "bestia faerie", "Bestia Faerie"),
    BESTIA_SOMBRA("47", "bestia/Sombra", "Bestia/Sombra"),
    GUERRERO_HEROE("48", "guerrero/heroe", "Guerrero/Héroe"),
    BESTIA_DRAGON("49", "bestia/dragon", "Bestia/Dragón"),
    GUERRERO_SACERDOTE("50", "guerrero/sacerdote", "Guerrero/Sacerdote"),
    DRAGON_SOMBRA("51", "dragon sombra", "Dragon Sombra"),
    ETERNO_HEROE("52", "eterno heroe", "Eterno Heroe"),
    CABALLERO_SACERDOTE("53", "caballero sacerdote", "Caballero Sacerdote"),
    FAERIE_SACERDOTE("54", "faerie sacerdote", "Faerie Sacerdote"),
    CABALLERO_SOMBRA("55", "caballero sombra", "Caballero Sombra");

    private final String id;
    private final String slug;
    private final String name;

    Race(String id, String slug, String name) {
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

    public static Race fromSlug(String slug) {
        for (Race r : values()) {
            if (r.slug.equalsIgnoreCase(slug)) {
                return r;
            }
        }
        return null;
    }

    public static Race fromId(String id) {
        for (Race r : values()) {
            if (r.id.equals(id)) {
                return r;
            }
        }
        return null;
    }
}
