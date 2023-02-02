package com.umc.i.src.review;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.review.model.post.PostReviewReq;
import com.umc.i.utils.S3Storage.Image;

import javax.sql.DataSource;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 장터 후기 작성
    public int createReviews(PostReviewReq postReviewReq) throws BaseException {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = time.format(timeFormatter);

        try {
            String createReviewsQuery = "insert into Market_review (board_idx, sell_mem_idx, buy_mem_idx, review_goods, review_content, review_image, review_hit, review_blame, review_created_at) ";
            createReviewsQuery += "values (3, ?, ?, ?, ?, ?, 0, 0, ?)";

            Object[] createReviewsParams = new Object[] {postReviewReq.getSellerIdx(), postReviewReq.getBuyerIdx(), postReviewReq.getGoods(), postReviewReq.getContent(), postReviewReq.getImgCnt(), currentTime};
            this.jdbcTemplate.update(createReviewsQuery, createReviewsParams);  // 장터 후기 저장

            String laseInsertQuery = "select last_insert_id()"; // 가장 마지막에 삽입된 id 값 가져온다
            int reviewIdx = this.jdbcTemplate.queryForObject(laseInsertQuery, int.class);

            return reviewIdx;        // 생성된 장터 후기 인덱스
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.POST_FEEDS_UPLOAD_FAIL);
        }
    }

    // 장터 후기 이미지 정보 저장
    public void createReviewsImage(List<Image> img, int reviewIdx) {
        String createReviewsImageQuery = "insert into Image_url (content_category, content_idx, image_url, image_order)";
        createReviewsImageQuery += " values (?, ?, ?, ?)";
        for (int i = 0; i < img.size(); i++) {
            Object[] createReviewsImageParams = new Object[] {img.get(i).getCategory(), reviewIdx, img.get(i).getUploadFilePath(), i};
            this.jdbcTemplate.update(createReviewsImageQuery, createReviewsImageParams);
        }
    }

    
}
