package io.comeandcommue.scraping.common.enums;

import lombok.Getter;

@Getter
public enum CommunityType {
    DCINSIDE("디시인사이드", "https://www.dcinside.com"),
    FMKOREA("에펨코리아", "https://www.fmkorea.com"),
    THEQOO("더쿠", "https://theqoo.net"),
    PPOMPPU("뽐뿌", "https://www.ppomppu.co.kr"),
    RULIWEB("루리웹", "https://www.ruliweb.com"),
    MLBPARK("엠엘비파크", "https://mlbpark.donga.com"),
    INVEN("인벤", "https://www.inven.co.kr"),
    ARCALIVE("아카라이브", "https://arca.live/"),
    NATEPANN("네이트판", "https://pann.nate.com"),
    CLIEN("클리앙", "https://www.clien.net"),
    BOBAEDREAM("보배드림", "https://www.bobaedream.co.kr"),
    INSTIZ("인스티즈", "https://www.instiz.net"),
    HUMORUNIV("유머유니버스", "https://www.humoruniv.com"),
    ETOLAND("이토랜드", "https://www.etoland.co.kr"),
    COOK82("82쿡", "https://www.82cook.com")
    ;

    private final String label;
    private final String baseUrl;

    CommunityType(String label, String baseUrl) {
        this.label = label;
        this.baseUrl = baseUrl;
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
