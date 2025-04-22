package org.rjr.baja_cartas.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Card {

    private int id;
    private String edid;
    private String slug;
    private String name;
    private int rarity;
    private int race;
    private int type;
    private int cost;
    private int damage;
    private String ability;
    private String flavour;
    private String ed_edid;
    private String ed_slug;

    
}
