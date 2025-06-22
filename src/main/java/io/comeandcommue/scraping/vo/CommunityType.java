package io.comeandcommue.scraping.vo;

import lombok.Getter;

@Getter
public enum CommunityType {
    DCINSIDE(1),
    FMKOREA(2),
    THEQOO(3),
    ;

    private final int order;

    CommunityType(int order) {
        this.order = order;
    }

    public static CommunityType fromOrder(int order) {
        for (CommunityType type : values()) {
            if (type.getOrder() == order) {
                return type;
            }
        }
        return DCINSIDE; // Default to DCINSIDE if no match found
    }

    public static CommunityType fromName(String name) {
        for (CommunityType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No CommunityType found for name: " + name);
    }
}
