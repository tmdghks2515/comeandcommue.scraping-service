package io.comeandcommue.scraping.common;

import lombok.Getter;

@Getter
public enum CommunityType {
    DCINSIDE,
    FMKOREA,
    THEQOO,
    PPOMPPU,
    RULIWEB,
    MLBPARK,
    INVEN,
    ARCALIVE,
    NATEPANN,
    CLIEN,
    BOBAEDREAM,
    INSTIZ,
    HUMORUNIV,
    ETOLAND,
    COOK82,
    ;

    public static CommunityType fromName(String name) {
        for (CommunityType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No CommunityType found for name: " + name);
    }
}
