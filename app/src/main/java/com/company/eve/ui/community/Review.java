package com.company.eve.ui.community;

import java.util.ArrayList;
import java.util.List;

public class Review {
    private String reviewId; // 리뷰 ID
    private String stationName; // 충전소 이름
    private String title; // 제목
    private String content; // 내용
    private String userId; // 작성자 UID
    private String author; // 작성자 이름 추가
    private Long timestamp; // 작성 시간
    private int viewCount; // 조회수
    private List<String> comments; // 댓글 목록

    // 기본 생성자
    public Review() {
        comments = new ArrayList<>();
    }

    // 생성자
    public Review(String reviewId, String stationName, String title, String content, String userId, String author, Long timestamp, int viewCount) {
        this.reviewId = reviewId;
        this.stationName = stationName;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.author = author; // 추가된 부분
        this.timestamp = timestamp;
        this.viewCount = viewCount;
        this.comments = new ArrayList<>();
    }

    // Getter 및 Setter 메서드
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAuthor() { return author; } // 추가된 부분
    public void setAuthor(String author) { this.author = author; } // 추가된 부분
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public List<String> getComments() { return comments; }
    public void setComments(List<String> comments) { this.comments = comments; }
    public void addComment(String comment) { comments.add(comment); }
    public void incrementViewCount() { this.viewCount++; }
}
