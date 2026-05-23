<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

		<style>
			/* updateform 전용 스타일 (기존 디자인 유지) */
			.form-wrap {
				width: 100%;
				max-width: 560px;
				padding: 52px;
				background: var(--surface);
				border: 1px solid var(--border);
				border-radius: 4px;
				margin: 0 auto;
			}

			.form-tag {
				font-size: 11px;
				font-weight: 500;
				letter-spacing: 1.5px;
				text-transform: uppercase;
				color: var(--text-sub);
				background: var(--accent-light);
				padding: 4px 10px;
				border-radius: 2px;
				display: inline-block;
				margin-bottom: 20px;
			}

			.form-title {
				font-family: 'DM Serif Display', serif;
				font-size: 30px;
				color: var(--text);
				margin-bottom: 36px;
			}

			.field {
				margin-bottom: 20px;
			}

			.field label {
				display: block;
				font-size: 11px;
				font-weight: 500;
				letter-spacing: 1px;
				text-transform: uppercase;
				color: var(--text-muted);
				margin-bottom: 8px;
			}

			.field input {
				width: 100%;
				padding: 12px 14px;
				font-family: 'DM Sans', sans-serif;
				font-size: 14px;
				color: var(--text);
				background: var(--bg);
				border: 1px solid var(--border);
				border-radius: 4px;
				outline: none;
				transition: border-color 0.2s;
			}

			.field input:focus {
				border-color: var(--accent);
			}

			.field input[readonly] {
				background: var(--accent-light);
				color: var(--text-muted);
				cursor: not-allowed;
			}
		</style>

		<div class="form-wrap">
			<span class="form-tag">Admin</span>
			<div class="form-title">
				<c:choose>
					<c:when test="${not empty admin}">도서 수정</c:when>
					<c:otherwise>신규 도서 등록</c:otherwise>
				</c:choose>
			</div>

			<form action="${pageContext.request.contextPath}/admin/${not empty admin ? 'update' : 'insert'}"
				method="post">

				<div class="field">
					<label>ISBN</label>
					<c:choose>
						<c:when test="${not empty admin}">
							<input type="text" value="${admin.isbn}" readonly>
							<input type="hidden" name="isbn" value="${admin.isbn}">
						</c:when>
						<c:otherwise>
							<input type="number" name="isbn" required>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="field">
					<label>도서명</label>
					<input type="text" name="bookname" value="${admin.bookname}" required>
				</div>
				<div class="field">
					<label>저자</label>
					<input type="text" name="author" value="${admin.author}" required>
				</div>
				<div class="field">
					<label>출판사</label>
					<input type="text" name="publisher" value="${admin.publisher}" required>
				</div>
				<div class="field">
					<label>이미지 경로 URL</label>
					<input type="text" name="image" value="${admin.image}">
				</div>
				<div class="field">
					<label>가격</label>
					<input type="text" name="price" value="${admin.price}">
				</div>

				<div class="btn-group" style="margin-top: 32px; gap: 12px;">
					<a href="${pageContext.request.contextPath}/book/list" class="btn btn-back">취소</a>
					<button type="submit" class="btn btn-buy">
						<c:choose>
							<c:when test="${not empty admin}">수정 내용 저장</c:when>
							<c:otherwise>새 도서 등록하기</c:otherwise>
						</c:choose>
					</button>
				</div>

			</form>
		</div>