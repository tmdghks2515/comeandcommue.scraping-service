CREATE TABLE community (
                           community_type VARCHAR(255) NOT NULL,   -- EnumType.STRING 매핑
                           base_url VARCHAR(255),
                           post_base_url VARCHAR(255),
                           has_img_permission BOOLEAN NOT NULL,
                           use_selenium BOOLEAN NOT NULL,

                           created_by VARCHAR(255),
                           updated_by VARCHAR(255),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

                           PRIMARY KEY (community_type)
);

CREATE TABLE scrap_info (
                            id                  VARCHAR(255) PRIMARY KEY,
                            community_type      VARCHAR(255) NOT NULL,     -- FK to community(community_type)
                            scrap_target_type   VARCHAR(255) NOT NULL,     -- EnumType.STRING
                            target_url          VARCHAR(255) NOT NULL,
                            target_row_selector VARCHAR(255) NOT NULL,

                            active              BOOLEAN NOT NULL DEFAULT TRUE,

                            created_by          VARCHAR(255),
                            updated_by          VARCHAR(255),
                            created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_scrap_info_community
                                FOREIGN KEY (community_type)
                                    REFERENCES community (community_type)
                                    ON UPDATE CASCADE
                                    ON DELETE RESTRICT
);


CREATE TABLE scrap_property (
                                id                           VARCHAR(255) PRIMARY KEY,
                                scrap_info_id                VARCHAR(255) NOT NULL,   -- FK to scrap_info(id)

                                post_property_type           VARCHAR(255) NOT NULL,   -- EnumType.STRING
                                selector                     VARCHAR(255) NOT NULL,
                                select_idx                   INTEGER NOT NULL DEFAULT 0,

                                extract_method               VARCHAR(255) NOT NULL DEFAULT 'TEXT', -- EnumType.STRING, 기본값 TEXT
                                attr_name                    VARCHAR(255),

                                ad_detection_regex           TEXT,      -- 정규식은 길 수 있어 TEXT 권장
                                property_skip_detection_regex TEXT,     -- 정규식은 길 수 있어 TEXT 권장

                                date_format                  VARCHAR(255),
                                date_format_2                VARCHAR(255),

    -- BaseEntity (Auditing + Time) 상속 컬럼
                                created_by                   VARCHAR(255),
                                updated_by                   VARCHAR(255),
                                created_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_scrap_property_info
                                    FOREIGN KEY (scrap_info_id)
                                        REFERENCES scrap_info (id)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE
);


CREATE TABLE scrap_process (
                               id                  VARCHAR(255) PRIMARY KEY,
                               scrap_property_id   VARCHAR(255) NOT NULL,   -- FK to scrap_property(id)

                               scrap_process_type  VARCHAR(255) NOT NULL,   -- EnumType.STRING
                               replace_from        VARCHAR(255),
                               replace_to          VARCHAR(255),
                               matcher_regex       TEXT,
                               matcher_group_no    INTEGER,
                               split_str           VARCHAR(255),
                               split_index         INTEGER,
                               process_order       INTEGER NOT NULL,        -- 정렬 순서

                               created_by          VARCHAR(255),
                               updated_by          VARCHAR(255),
                               created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_scrap_process_property
                                   FOREIGN KEY (scrap_property_id)
                                       REFERENCES scrap_property (id)
                                       ON UPDATE CASCADE
                                       ON DELETE CASCADE
);

CREATE TABLE post (
                      id               VARCHAR(255) PRIMARY KEY,
                      post_no          VARCHAR(255),
                      title            VARCHAR(255),
                      category_name    VARCHAR(255),

                      link_href        VARCHAR(1000) NOT NULL,
                      thumbnail_src    VARCHAR(1000),

                      author_name      VARCHAR(255),
                      like_count       INTEGER      NOT NULL DEFAULT 0,
                      hit_count        INTEGER      NOT NULL DEFAULT 0,
                      comment_count    INTEGER      NOT NULL DEFAULT 0,

                      community_type   VARCHAR(100),      -- EnumType.STRING
                      posted_at        TIMESTAMP,         -- Instant → TIMESTAMP(밀리초는 드라이버/JPA가 처리)

                      created_by       VARCHAR(255),
                      updated_by       VARCHAR(255),
                      created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE post_like (
                           post_id     VARCHAR(255) NOT NULL,
                           user_id     VARCHAR(255) NOT NULL,

                           created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           PRIMARY KEY (post_id, user_id)
);
