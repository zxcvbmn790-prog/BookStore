package WebBookStore.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFeedbackVO {
    private int likeCount;
    private double averageRating;
    private int ratingCount;
    private Integer myRating;
    private boolean liked;
}
