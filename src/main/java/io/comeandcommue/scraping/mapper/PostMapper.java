package io.comeandcommue.scraping.mapper;

import io.comeandcommue.scraping.dto.PostDto;
import io.comeandcommue.scraping.entity.PostEntity;

public class PostMapper {

    public static PostDto toDto(PostEntity entity) {
        if (entity == null) return null;

        return PostDto.of()
                .id(entity.getId())
                .communityPostId(entity.getCommunityPostId())
                .title(entity.getTitle())
                .linkHref(entity.getLinkHref())
                .thumbnailSrc(entity.getThumbnailSrc())
                .authorName(entity.getAuthorName())
                .likeCount(entity.getLikeCount())
                .viewCount(entity.getViewCount())
                .commentCount(entity.getCommentCount())
                .communityType(entity.getCommunityType())
                .postedAt(entity.getPostedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static PostEntity toEntity(PostDto dto) {
        if (dto == null) return null;

        return PostEntity.of() // Builder 패턴 사용 중
                .id(dto.getId())
                .communityPostId(dto.getCommunityPostId())
                .title(dto.getTitle())
                .linkHref(dto.getLinkHref())
                .thumbnailSrc(dto.getThumbnailSrc())
                .authorName(dto.getAuthorName())
                .likeCount(dto.getLikeCount())
                .viewCount(dto.getViewCount())
                .commentCount(dto.getCommentCount())
                .communityType(dto.getCommunityType())
                .postedAt(dto.getPostedAt())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
