package com.devwonder.blog_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogPostCacheService {

    @CacheEvict(value = {"blog-posts", "blog-posts-by-category", "blog-posts-featured", "blog-posts-popular", "blog-posts-search"}, allEntries = true)
    public void evictPostListCaches() {
        log.debug("Evicting blog post list caches");
    }

    @CacheEvict(value = {"blog-posts", "blog-posts-by-category", "blog-posts-featured", "blog-posts-popular", "blog-posts-search", "blog-posts-by-slug"}, allEntries = true)
    public void evictAllPostCaches() {
        log.debug("Evicting all blog post caches");
    }

    @CacheEvict(value = "blog-posts-by-slug", key = "'slug:' + #slug")
    public void evictPostBySlugCache(String slug) {
        log.debug("Evicting blog post cache for slug: {}", slug);
    }
}