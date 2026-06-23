<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>도서 관리</title>
<link
	href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500&display=swap"
	rel="stylesheet">
<style>
:root {
	--bg: #f5f0e8;
	--surface: #faf7f2;
	--border: #e2d9cc;
	--text: #2c2520;
	--text-sub: #8a7e74;
	--text-muted: #b5aba0;
	--accent: #5c4a3a;
	--accent-light: #e8ddd3;
	--red: #9e3d2b;
}

*, *::before, *::after {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	font-family: 'DM Sans', sans-serif;
	background: var(--bg);
	color: var(--text);
	min-height: 100vh;
}

header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	z-index: 100;
	height: 60px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 52px;
	background: var(--accent);
	border-bottom: 1px solid var(--border);
}

.logo {
	font-family: 'DM Serif Display', serif;
	font-size: 18px;
	color: #faf7f2;
	letter-spacing: 0.5px;
}

.admin-badge {
	font-size: 11px;
	font-weight: 500;
	letter-spacing: 1.5px;
	text-transform: uppercase;
	background: var(--accent-light);
	color: var(--accent);
	padding: 3px 10px;
	border-radius: 2px;
	margin-left: 12px;
}

nav {
	display: flex;
	gap: 28px;
	align-items: center;
}

nav a {
	font-size: 13px;
	color: var(--accent-light);
	text-decoration: none;
	transition: color 0.2s;
}

nav a:hover {
	color: #fff;
}

/* 등록 버튼 */
.btn-insert {
	padding: 8px 18px;
	background: #faf7f2;
	color: var(--accent);
	font-size: 13px;
	font-weight: 500;
	border-radius: 4px;
	text-decoration: none;
	transition: all 0.2s;
}

.btn-insert:hover {
	background: var(--accent-light);
}

/* 페이지 타이틀 */
.page-header {
	padding-top: 60px;
	text-align: center;
}

.page-header-inner {
	padding: 52px 0 40px;
	border-bottom: 1px solid var(--border);
}

.page-tag {
	display: inline-block;
	font-size: 11px;
	font-weight: 500;
	letter-spacing: 1.5px;
	text-transform: uppercase;
	color: var(--text-sub);
	background: var(--accent-light);
	padding: 4px 10px;
	border-radius: 2px;
	margin-bottom: 16px;
}

.page-title {
	font-family: 'DM Serif Display', serif;
	font-size: 36px;
	color: var(--text);
}

/* 그리드 */
.book-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
	gap: 28px;
	max-width: 1100px;
	margin: 0 auto;
	padding: 48px 52px 72px;
}

/* 카드 */
.book-card {
	background: var(--surface);
	border: 1px solid var(--border);
	border-radius: 4px;
	overflow: hidden;
	display: block;
	color: inherit;
	text-decoration: none;
	transition: transform 0.22s ease, box-shadow 0.22s ease;
}

.book-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 12px 32px rgba(44, 37, 32, 0.10);
}

.img-wrap {
	width: 100%;
	height: 230px;
	background: var(--accent-light);
	overflow: hidden;
	display: flex;
	align-items: center;
	justify-content: center;
}

.img-wrap img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	transition: transform 0.4s ease;
}

.book-card:hover .img-wrap img {
	transform: scale(1.04);
}

.no-img {
	font-size: 11px;
	letter-spacing: 1.5px;
	text-transform: uppercase;
	color: var(--text-muted);
}

.card-info {
	padding: 16px 18px 12px;
	border-top: 1px solid var(--border);
}

.card-title {
	font-family: 'DM Serif Display', serif;
	font-size: 15px;
	line-height: 1.4;
	color: var(--text);
	margin-bottom: 6px;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.card-author {
	font-size: 12px;
	font-weight: 300;
	color: var(--text-muted);
	margin-bottom: 10px;
}

.card-price {
	font-size: 14px;
	font-weight: 500;
	color: var(--red);
	margin-bottom: 12px;
}
.btn-action {
    display: flex;
    align-items: center;
    justify-content: center;
    flex: 1; /* 가로 공간을 1:1로 정확히 나눕니다 */
    padding: 12px 0;
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 0.5px;
    text-align: center;
    text-decoration: none;
    border-top: 1px solid var(--border);
    transition: all 0.2s;
}
/* 수정 버튼 */
.btn-update {
	display: block;
	width: 100%;
	padding: 9px 0;
	background: var(--accent-light);
	color: var(--accent);
	font-size: 12px;
	font-weight: 500;
	letter-spacing: 0.5px;
	text-align: center;
	text-decoration: none;
	border-top: 1px solid var(--border);
	transition: background 0.2s;
}
.btn-delete {
    background: var(--surface);
    color: var(--red);
}
.btn-delete:hover {
    background: #fff5f3; /* 옅은 빨간색 배경 */
    color: #c0392b;
}
.btn-update:hover {
	background: var(--accent);
	color: #faf7f2;
}

.empty {
	grid-column: 1/-1;
	text-align: center;
	padding: 80px 0;
	font-family: 'DM Serif Display', serif;
	font-size: 22px;
	color: var(--text-muted);
}
</style>
</head>
<body>
	<form id="logoutForm"
		action="${pageContext.request.contextPath}/member/logout"
		method="post" style="display: none;"></form>
	<header>
		<div style="display: flex; align-items: center;">
			<div class="logo">Booklist</div>
			<span class="admin-badge">Admin</span>
		</div>
		<nav>
			<a href="${pageContext.request.contextPath}/admin/insertform"
				class="btn-insert">+ 도서 등록</a> <a href="javascript:void(0);"
				onclick="logoutSubmit();"
				style="font-size: 13px; color: #fff; text-decoration: none; margin-left: 15px;">로그아웃</a>
		</nav>
	</header>

	<div class="page-header">
		<div class="page-header-inner">
			<div class="page-tag">관리자 페이지</div>
			<div class="page-title">도서 관리</div>
		</div>
	</div>

	<div class="book-grid">
		<c:choose>
			<c:when test="${not empty list}">
				<c:forEach var="book" items="${list}">
					<div class="book-card">
						<div class="img-wrap">
							<c:choose>
								<c:when test="${not empty book.image}">
									<img src="${pageContext.request.contextPath}${book.image}"
										alt="${book.bookname}">
								</c:when>
								<c:otherwise>
									<span class="no-img">No Image</span>
								</c:otherwise>
							</c:choose>
						</div>
						<div class="card-info">
							<div class="card-title">${book.bookname}</div>
							<div class="card-author">${book.author}·${book.publisher}</div>
							<div class="card-price">
								<c:choose>
									<c:when test="${not empty book.price}">${book.price}원</c:when>
									<c:otherwise>가격 미정</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div style="display: flex;">
							<a
								href="${pageContext.request.contextPath}/admin/updateform?isbn=${book.isbn}"
								class="btn-action btn-update">수정</a> <a
								href="${pageContext.request.contextPath}/admin/delete?isbn=${book.isbn}"
								class="btn-action btn-delete"
								onclick="return confirm('정말 삭제하시겠습니까?')">삭제</a>
						</div>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="empty">등록된 도서가 없습니다.</div>
			</c:otherwise>
		</c:choose>
	</div>
</body>
<script>
	function logoutSubmit() {
		if (confirm("관리자 세션을 종료하고 로그아웃 하시겠습니까?")) {
			document.getElementById('logoutForm').submit();
		}
	}
</script>
</html>
