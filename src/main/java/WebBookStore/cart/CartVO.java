package WebBookStore.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CartVO {
	private int cart_id;
	private String userid;
	private int isbn;
	private int amount;

	private String bookname;
	private String price; // BookVO와 동일하게 String 유지
	private String image;

	private int totalPrice;
}