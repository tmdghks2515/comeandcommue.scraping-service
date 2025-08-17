package io.comeandcommue.scraping.domain.post;

public class PostMapper {

    public static PostDto toDto(PostEntity entity) {
        if (entity == null) return null;

        return PostDto.of()
                .id(entity.getId())
                .postNo(entity.getPostNo())
                .title(entity.getTitle())
                .categoryName(entity.getCategoryName())
                .linkHref(entity.getLinkHref())
                .thumbnailSrc(entity.getThumbnailSrc())
                .authorName(entity.getAuthorName())
                .likeCount(entity.getLikeCount())
                .hitCount(entity.getHitCount())
                .communityType(entity.getCommunityType())
                .postedAt(entity.getPostedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static PostEntity toEntity(PostDto dto) {
        if (dto == null) return null;

        return PostEntity.of()
                .id(dto.getId())
                .postNo(dto.getPostNo())
                .title(dto.getTitle())
                .categoryName(dto.getCategoryName())
                .linkHref(dto.getLinkHref())
                .thumbnailSrc(dto.getThumbnailSrc())
                .authorName(dto.getAuthorName())
                .likeCount(dto.getLikeCount())
                .hitCount(dto.getHitCount())
                .communityType(dto.getCommunityType())
                .postedAt(dto.getPostedAt())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
