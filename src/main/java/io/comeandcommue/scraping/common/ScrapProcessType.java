package io.comeandcommue.scraping.common;

import lombok.Getter;

@Getter
public enum ScrapProcessType {
    REPLACE,
    REPLACE_ALL,
    MATCHER,
    SPLIT,
}
