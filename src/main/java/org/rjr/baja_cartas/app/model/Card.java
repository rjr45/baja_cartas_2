package org.rjr.baja_cartas.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.rjr.baja_cartas.app.enums.CardType;
import org.rjr.baja_cartas.app.enums.Race;
import org.rjr.baja_cartas.app.enums.Rarity;

@Getter
@Setter
@ToString
public class Card {

    private final int id;
    private final String edid;
    private final String slug;
    private final String name;
    private final Rarity rarity;
    private final Race race;
    private final CardType type;
    private final String keywords;
    private final int cost;
    private final int damage;
    private final String ability;
    private final String flavour;
    private final String editionId;
    private final String editionSlug;

    public Card(
            String id,
            String edid,
            String slug,
            String name,
            String rarity,
            String race,
            String type,
            String keywords,
            String cost,
            String damage,
            String ability,
            String flavour,
            String ed_edid,
            String ed_slug
    ) {
        this.id = Integer.parseInt(id);
        this.edid = edid;
        this.slug = slug;
        this.name = name;
        this.rarity = Rarity.fromId(rarity);
        this.race = Race.fromId(race);
        this.type = CardType.fromId(type);
        this.keywords = keywords;
        this.cost = Integer.parseInt(cost);
        this.damage = Integer.parseInt(damage);
        this.ability = ability;
        this.flavour = flavour;
        this.editionId = ed_edid;
        this.editionSlug = ed_slug;
    }
}
