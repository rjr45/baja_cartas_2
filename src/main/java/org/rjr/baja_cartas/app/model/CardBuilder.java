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
public class CardBuilder {

    private String nombre;
    private String edicion;
    private String tipo;
    private String rareza;
    private String raza;
    private String danio;
    private String costo;
    private String habilidad;
    private String imgPath;
    private int copias;

}
