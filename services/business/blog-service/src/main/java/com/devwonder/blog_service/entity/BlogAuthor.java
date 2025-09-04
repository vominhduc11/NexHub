package com.devwonder.blog_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blog_authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogAuthor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 255)
    private String title;
    
    @Column(length = 500)
    private String avatar;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(length = 255)
    private String twitter;
    
    @Column(length = 255)
    private String linkedin;
    
    @Column(length = 255)
    private String github;
    
    @Column(length = 255)
    private String facebook;
    
    @Column(name = "articles_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer articlesCount = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BlogPost> posts;
}