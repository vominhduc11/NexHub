package com.devwonder.blog_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(nullable = false, unique = true, length = 500)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String excerpt;
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "featured_image", length = 500)
    private String featuredImage;
    
    @Column(name = "meta_title", length = 500)
    private String metaTitle;
    
    @Column(name = "meta_description", length = 1000)
    private String metaDescription;
    
    @Column(name = "meta_keywords", length = 1000)
    private String metaKeywords;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.DRAFT;
    
    @Column(name = "is_featured", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isFeatured = false;
    
    @Column(name = "views_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer viewsCount = 0;
    
    @Column(name = "likes_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer likesCount = 0;
    
    @Column(name = "comments_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer commentsCount = 0;
    
    @Column(name = "reading_time", columnDefinition = "INTEGER DEFAULT 0")
    private Integer readingTime = 0;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private BlogAuthor author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private BlogCategory category;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "blog_post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<BlogTag> tags;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BlogComment> comments;
    
    public enum PostStatus {
        DRAFT, PUBLISHED, SCHEDULED, ARCHIVED
    }
}