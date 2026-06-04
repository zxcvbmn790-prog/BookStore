<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
		<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
		<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

			<style>
				/* [1] 슬라이드 배너 */
				.banner-container {
					max-width: 1200px;
					position: relative;
					margin: 20px auto;
					overflow: hidden;
					border-radius: 10px;
					box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
				}

				.banner-slide {
					display: none;
				}

				.banner-slide img {
					height: 400px;
					width: 100%;
					object-fit: cover;
				}



				.prev,
				.next {
					cursor: pointer;
					position: absolute;
					top: 50%;
					padding: 16px;
					color: white;
					font-weight: bold;
					background: rgba(0, 0, 0, 0.2);
					border-radius: 50%;
					user-select: none;
					margin-top: -22px;
				}

				.next {
					right: 20px;
				}

				.prev {
					left: 20px;
				}

				/* [2] 카테고리 버튼 스타일 */
				.category-menu {
					display: flex;
					justify-content: center;
					gap: 12px;
					flex-wrap: wrap;
					margin: 30px 0 50px 0;
				}

				.category-menu button {
					background-color: #f8f9fa;
					border: 1px solid #e9ecef;
					border-radius: 25px;
					padding: 10px 22px;
					font-size: 14px;
					color: #495057;
					cursor: pointer;
					transition: all 0.2s;
				}

				.category-menu button:hover,
				.category-menu button.active {
					background-color: #333;
					color: white;
				}

				/* [3] 메인 화면용 그리드 스타일 (왼쪽 정렬) */
				.book-list-container {
					display: flex;
					flex-wrap: wrap;
					justify-content: flex-start;
					gap: 25px;
					padding: 0;
					max-width: 1200px;
					margin: 0 auto;
				}

				.book-card {
					border: 1px solid #eee;
					padding: 15px;
					width: 210px;
					text-align: center;
					background: #fff;
					border-radius: 8px;
					transition: transform 0.2s;
					box-sizing: border-box;
				}

				.book-card:hover {
					transform: translateY(-5px);
					box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
				}

				.book-card img {
					width: 100%;
					height: 280px;
					object-fit: cover;
					border-radius: 4px;
				}

				.book-title {
					font-size: 14px;
					font-weight: bold;
					margin: 12px 0;
					height: 2.8em;
					overflow: hidden;
					line-height: 1.4;
					color: #333;
				}

				.book-price {
					color: #d9534f;
					font-size: 17px;
					font-weight: bold;
				}

				.section-header {
					display: flex;
					justify-content: space-between;
					align-items: center;
					border-bottom: 2px solid #333;
					padding-bottom: 10px;
					margin: 50px auto 25px auto;
					max-width: 1150px;
				}

				.section-header h2 {
					margin: 0;
					font-size: 22px;
					color: #222;
				}

				.more-link {
					text-decoration: none;
					color: #888;
					font-size: 14px;
				}

				/* [4] 전체보기/카테고리 선택용 가로 리스트 스타일 */
				.list-view-container {
					max-width: 1150px;
					margin: 0 auto;
				}

				.list-item {
					display: flex;
					align-items: center;
					padding: 25px 0;
					border-bottom: 1px solid #eee;
					gap: 30px;
				}

				.list-img-box {
					width: 150px;
					flex-shrink: 0;
				}

				.list-img-box img {
					width: 100%;
					height: auto;
					border-radius: 4px;
					box-shadow: 2px 2px 8px rgba(0, 0, 0, 0.1);
				}

				.list-info-box {
					flex-grow: 1;
					text-align: left;
				}

				.list-item-title {
					font-size: 20px;
					font-weight: bold;
					margin-bottom: 10px;
					color: #333;
					text-decoration: none;
				}

				.list-meta {
					font-size: 14px;
					color: #666;
					margin-bottom: 15px;
				}

				.list-item-price {
					font-size: 19px;
					color: #d9534f;
					font-weight: bold;
				}


				/* 우측 버튼 영역 컨테이너 */
				.list-btn-box {
					width: 140px;
					flex-shrink: 0;
					display: flex;
					flex-direction: column;
					gap: 8px;
				}

				/* 버튼 공통 스타일 (크기 고정 핵심) */
				.list-btn {
					width: 100%;
					/* 부모 너비에 맞춤 */
					height: 40px;
					/* 높이 통일 */
					display: flex;
					align-items: center;
					justify-content: center;
					border: 1px solid #ddd;
					background: #fff;
					cursor: pointer;
					border-radius: 4px;
					font-size: 13px;
					transition: 0.2s;
				}

				/* 장바구니 버튼 (강조) */
				.list-btn.cart-btn {
					background: #fff;
					color: #333;
					border: 1px solid #333;
				}

				.list-btn.cart-btn:hover {
					background: #f8f8f8;
				}

				/* 바로구매 버튼 (어두운 배경) */
				.list-btn.buy-btn {
					background: #333;
					color: #fff;
					border-color: #333;
				}

				.list-btn.buy-btn:hover {
					background: #000;
				}

				/* 하단 컨트롤러 영역 정렬 */
				.control-box {
					display: flex;
					justify-content: center;
					align-items: center;
					gap: 10px;
					margin: 60px 0;
				}

				/* 페이지 번호 및 버튼 공통 스타일 */
				.page-link {
					display: inline-flex;
					align-items: center;
					justify-content: center;
					min-width: 35px;
					height: 35px;
					padding: 0 5px;
					border: 1px solid #ddd;
					background-color: #fff;
					color: #555;
					text-decoration: none;
					font-size: 14px;
					border-radius: 4px;
					transition: 0.2s;
				}

				.page-link:hover {
					background-color: #f5f5f5;
					border-color: #999;
				}

				/* 현재 선택된 페이지 강조 */
				.page-link.active {
					background-color: #333;
					color: #fff;
					border-color: #333;
					font-weight: bold;
				}

				/* 전체보기 버튼 (우측에 배치) */
				.btn-all-view {
					margin-left: 20px;
					padding: 0 20px;
					background-color: #555;
					color: #fff;
					border-color: #444;
				}
			</style>

			<div class="banner-container">
				<div class="banner-slide fade"><img
						src="https://contents.kyobobook.co.kr/display/i_890_380_6974e9e91dd84287880c462e691aa1d7.jpg">
				</div>
				<div class="banner-slide fade"><img
						src="https://contents.kyobobook.co.kr/pmtn/2026/event/i_890_380_7581f67c386d4b6596163bf563cbc289.jpg">
				</div>
				<div class="banner-slide fade"><img
						src="https://contents.kyobobook.co.kr/advrcntr/IMAC/creatives/2026/03/20/70805/89038003201.png">
				</div>
				<a class="prev" onclick="plusSlides(-1)">&#10094;</a>
				<a class="next" onclick="plusSlides(1)">&#10095;</a>
			</div>

			<div class="category-section" style="text-align:center;">
			    <div class="category-menu">
			        <c:forEach var="cat"
			            items="${fn:split('전체,인공지능,초보자를 위한 컴퓨터 책,경영전략/혁신,인공지능/빅데이터,컴퓨터공학/전산학 개론', ',')}">
			            <button class="${category == cat ? 'active' : ''}"
			                onclick="location.href='${pageContext.request.contextPath}/book/list?category=${cat}'">
			                <c:choose>
			                    <c:when test="${cat == '초보자를 위한 컴퓨터 책'}">입문서</c:when>
			                    <c:when test="${cat == '컴퓨터공학/전산학 개론'}">컴퓨터공학</c:when>
			                    <c:otherwise>${cat}</c:otherwise>
			                </c:choose>
			            </button>
			        </c:forEach>
			    </div>
			</div>

			<c:choose>
				<%-- 1. 메인 화면 모드 (그리드형 섹션 출력) --%>
					<c:when test="${isMain}">
						<div style="max-width: 1200px; margin: 0 auto;">
							<div class="section-header">
								<h2>인공지능 추천 도서</h2><a
									href="${pageContext.request.contextPath}/book/list?category=인공지능&viewAll=true"
									class="more-link">더보기 ></a>
							</div>
							<div class="book-list-container">
								<c:forEach var="book" items="${itBooks}">
									<div class="book-card"><a href="view?isbn=${book.isbn}"><img
												src="${book.image}"></a>
										<div class="book-title">${book.bookname}</div>
										<div class="book-price">
											<fmt:parseNumber var="p" value="${book.price}" type="number" />
											<fmt:formatNumber value="${p}" type="number" />원
										</div>
									</div>
								</c:forEach>
							</div>
							<div class="section-header">
								<h2>초보자를 위한 컴퓨터 책</h2><a
									href="${pageContext.request.contextPath}/book/list?category=초보자를 위한 컴퓨터 책&viewAll=true"
									class="more-link">더보기 ></a>
							</div>
							<div class="book-list-container">
								<c:forEach var="book" items="${novelBooks}">
									<div class="book-card"><a href="view?isbn=${book.isbn}"><img
												src="${book.image}"></a>
										<div class="book-title">${book.bookname}</div>
										<div class="book-price">
											<fmt:parseNumber var="p" value="${book.price}" type="number" />
											<fmt:formatNumber value="${p}" type="number" />원
										</div>
									</div>
								</c:forEach>
							</div>
							<div class="section-header">
								<h2>경영전략/혁신 트렌드</h2><a
									href="${pageContext.request.contextPath}/book/list?category=경영전략/혁신&viewAll=true"
									class="more-link">더보기 ></a>
							</div>
							<div class="book-list-container">
								<c:forEach var="book" items="${economyBooks}">
									<div class="book-card"><a href="view?isbn=${book.isbn}"><img
												src="${book.image}"></a>
										<div class="book-title">${book.bookname}</div>
										<div class="book-price">
											<fmt:parseNumber var="p" value="${book.price}" type="number" />
											<fmt:formatNumber value="${p}" type="number" />원
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</c:when>

					<%-- 2. 전체보기/카테고리 선택 모드 (가로 리스트형 출력) --%>
						<c:otherwise>
							<div class="list-view-container">
								<c:forEach var="book" items="${list}">
									<div class="list-item">
										<div class="list-img-box">
											<a href="view?isbn=${book.isbn}"><img src="${book.image}"></a>
										</div>

										<div class="list-info-box">
											<a href="view?isbn=${book.isbn}"
												class="list-item-title">${book.bookname}</a>
											<div class="list-meta">${book.author} | ${book.publisher} | ${book.category}
											</div>
											<div class="list-item-price">
												<fmt:parseNumber var="p" value="${book.price}" type="number" />
												<fmt:formatNumber value="${p}" type="number" />원
											</div>
										</div>

										<div class="list-btn-box">
											<button type="button" class="list-btn cart-btn"
												onclick="addCartAsync('${book.isbn}', '${loginUser}')">장바구니</button>
											<button class="list-btn buy-btn"
												onclick="directBuy('${book.isbn}')">바로구매</button>
										</div>
									</div>
								</c:forEach>
							</div>
							<div class="control-box">
								<c:choose>
									<c:when test="${viewAll}">
										<a href="${pageContext.request.contextPath}/book/list?category=${category}&viewAll=false"
											class="page-link btn-all-view">접기</a>
									</c:when>

									<c:otherwise>
										<%-- [이전] 버튼: 현재 시작페이지가 1보다 크면 노출 --%>
											<c:if test="${startPage > 1}">
												<a href="${pageContext.request.contextPath}/book/list?category=${category}&page=${startPage - 1}"
													class="page-link">&lt; 이전</a>
											</c:if>

											<%-- ★ 수정된 부분: startPage부터 endPage까지만 반복 --%>
												<c:forEach var="i" begin="${startPage}" end="${endPage}">
													<a href="${pageContext.request.contextPath}/book/list?category=${category}&page=${i}"
														class="page-link ${currentPage == i ? 'active' : ''}">${i}</a>
												</c:forEach>

												<%-- [다음] 버튼: 현재 끝페이지가 전체페이지보다 작으면 노출 --%>
													<c:if test="${endPage < totalPages}">
														<a href="${pageContext.request.contextPath}/book/list?category=${category}&page=${endPage + 1}"
															class="page-link">다음 &gt;</a>
													</c:if>

													<a href="${pageContext.request.contextPath}/book/list?category=${category}&viewAll=true"
														class="page-link btn-all-view">전체보기</a>
									</c:otherwise>
								</c:choose>
							</div>
						</c:otherwise>
			</c:choose>

			<script>
				let slideIndex = 0;
				function showSlides() {
					let slides = document.getElementsByClassName("banner-slide");
					for (let i = 0; i < slides.length; i++) slides[i].style.display = "none";
					slideIndex++;
					if (slideIndex > slides.length) slideIndex = 1;
					if (slides.length > 0) slides[slideIndex - 1].style.display = "block";
					setTimeout(showSlides, 4500);
				}
				function plusSlides(n) {slideIndex += (n - 1); showSlides();}
				showSlides();
				function addCartAsync(isbn, loginUser) {
					if (!loginUser || loginUser === "") {
						alert("로그인이 필요한 서비스입니다.");
						location.href = "${pageContext.request.contextPath}/member/login";
						return;
					}

					fetch('${pageContext.request.contextPath}/cart/insert', {
						method: 'POST',
						headers: {'Content-Type': 'application/x-www-form-urlencoded'},
						body: new URLSearchParams({'isbn': isbn, 'amount': '1', 'userid': loginUser})
					})
						.then(() => {
							alert("장바구니에 담았습니다.");
							// 페이지 이동 안 함
						})
						.catch(err => alert("오류가 발생했습니다."));
				}
			</script>