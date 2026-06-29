package WebBookStore.toss.repository;


import WebBookStore.toss.model.TossVo;
import org.springframework.stereotype.Repository;

import java.sql.*;
import javax.sql.DataSource;

@Repository
public class TossDAOH2 implements TossDAO {

    private final DataSource dataSource;

    public TossDAOH2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // =========================
    // 1. 주문 생성 (READY)
    // =========================
    @Override
    public void insertOrder(TossVo order) {

        String sql = "INSERT INTO ORDERS " +
                "(ORDER_ID, PURCHASE_ID, MEMBER_ID, TOTAL_PRICE, ORDER_STATUS, ORDER_DATE) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getOrderId());
            ps.setInt(2, order.getPurchaseId());
            ps.setString(3, order.getMember_id());
            ps.setInt(4, order.getTotalPrice());
            ps.setString(5, "READY");

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("insertOrder 실패", e);
        }
    }
    
    @Override
	public void insertOrder2(TossVo order) {
    	 String sql = "INSERT INTO ORDERS " +
                 "(ORDER_ID, GUESTPURCHASE_ID, MEMBER_ID, TOTAL_PRICE, ORDER_STATUS, ORDER_DATE) " +
                 "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

         try (Connection conn = dataSource.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {

             ps.setString(1, order.getOrderId());
             ps.setString(2, order.getGuestPurchaseId());
             ps.setString(3, order.getMember_id());
             ps.setInt(4, order.getTotalPrice());
             ps.setString(5, "READY");

             ps.executeUpdate();

         } catch (Exception e) {
             throw new RuntimeException("insertOrder 실패", e);
         }
		
	}

    // =========================
    // 2. 주문 조회
    // =========================
    @Override
    public TossVo findOrderById(String orderId) {

    	String sql =
    		    "SELECT ORDER_ID, PURCHASE_ID, MEMBER_ID, TOTAL_PRICE, " +
    		    "ORDER_STATUS, PAYMENT_KEY, ORDER_DATE, PAID_AT " +
    		    "FROM ORDERS " +
    		    "WHERE ORDER_ID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, orderId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            	TossVo order = new TossVo();

                order.setOrderId(rs.getString("ORDER_ID"));
                order.setPurchaseId(rs.getInt("PURCHASE_ID"));
                order.setMember_id(rs.getString("MEMBER_ID"));;
                order.setTotalPrice(rs.getInt("TOTAL_PRICE"));
                order.setOrderStatus(rs.getString("ORDER_STATUS"));
                order.setPaymentKey(rs.getString("PAYMENT_KEY"));
                order.setOrderDate(rs.getTimestamp("ORDER_DATE"));
                order.setPaidAt(rs.getTimestamp("PAID_AT"));

                return order;
            }

        } catch (Exception e) {
            throw new RuntimeException("findOrderById 실패", e);
        }

        throw new RuntimeException("주문 데이터 없음: " + orderId);
    }

    // =========================
    // 3. 결제 성공 처리
    // =========================
    @Override
    public void updatePaymentSuccess(String orderId, String paymentKey) {

        String sql = "UPDATE ORDERS SET " +
                "PAYMENT_KEY = ?, " +
                "ORDER_STATUS = 'PAID', " +
                "PAID_AT = CURRENT_TIMESTAMP " +
                "WHERE ORDER_ID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, paymentKey);
            ps.setString(2, orderId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("updatePaymentSuccess 실패", e);
        }
    }

    // =========================
    // 4. 상태 변경
    // =========================
    @Override
    public void updateOrderStatus(String orderId, String status) {

        String sql = "UPDATE ORDERS SET ORDER_STATUS = ? WHERE ORDER_ID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, orderId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("updateOrderStatus 실패", e);
        }
        
        
    }
    
    
    @Override
    public TossVo findPurchaseById(int purchaseId) {

        String sql =
            "SELECT PURCHASE_ID, TOTAL_PRICE " +
            "FROM PURCHASE " +
            "WHERE PURCHASE_ID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, purchaseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                TossVo vo = new TossVo();
                vo.setPurchaseId(rs.getInt("PURCHASE_ID"));
                vo.setTotalPrice(rs.getInt("TOTAL_PRICE"));
                return vo;
            }

        } catch (Exception e) {
            throw new RuntimeException("purchase 조회 실패", e);
        }

        throw new RuntimeException("purchase 없음: " + purchaseId);
    }

    @Override
    public TossVo findGuestPurchaseById(String guestPurchaseId) {

        String sql =
            "SELECT PURCHASE_ID, TOTAL_PRICE " +
            "FROM GUEST_PURCHASE " +
            "WHERE PURCHASE_ID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, guestPurchaseId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                TossVo vo = new TossVo();

                vo.setGuestPurchaseId(rs.getString("PURCHASE_ID")); // 또는 rename 권장
                vo.setTotalPrice(rs.getInt("TOTAL_PRICE"));

                return vo;
            }

        } catch (Exception e) {
            throw new RuntimeException("purchase 조회 실패", e);
        }

        throw new RuntimeException("purchase 없음: " + guestPurchaseId);
    }

	
}