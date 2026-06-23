package WebBookStore.order;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
	private int orderId;
	private String userid;
	private long isbn;
	private String bookname;
	private int price;
	private int amount;
	private int totalPrice;
	private String receiver;
	private String phone;
	private String address;
	private Timestamp orderDate;
	private String trakingstatus;
	private String status;
	private String image;

	// 상윤 - 마일리지 기능을 위한 컬럼 추가
	private int usedMileage;
	private int earnedMileage;
	private int finalPayment;
}