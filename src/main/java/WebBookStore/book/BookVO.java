package WebBookStore.book;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookVO {
 private long isbn;
 private String bookname;
 private String author;
 private String publisher;
 private String image;
 private String price;
 private String category;
 private int likeCount;
 private double averageRating;
 private int ratingCount;

 public BookVO(long isbn, String bookname, String author, String publisher, String image, String price, String category) {
  this.isbn = isbn;
  this.bookname = bookname;
  this.author = author;
  this.publisher = publisher;
  this.image = image;
  this.price = price;
  this.category = category;
 }
}
