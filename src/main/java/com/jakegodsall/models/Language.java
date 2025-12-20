package com.jakegodsall.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jakegodsall.models.enums.Gender;
import com.jakegodsall.models.enums.Tense;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Language {
    private String name;

    @JsonProperty("supports_stress")
    private boolean supportsStress;
    private List<Tense> tenses;
    private List<Gender> genders;

    @Override
    public String toString() {
        return this.name;
    }

    public String toStringVerbose() {
        return "(name=" + this.name
                + ", supportsStress=" + this.isSupportsStress()
                + ", tenses=" + this.getTenses()
                + ", genders=" + this.getGenders()
                + ")";
    }
}
