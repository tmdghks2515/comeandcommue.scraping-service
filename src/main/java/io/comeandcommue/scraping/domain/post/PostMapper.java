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
}
