<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

		<style>
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

			/* ── ISBN 자동 조회 관련 스타일 ── */
			.isbn-row {
				display: flex;
				gap: 8px;
				align-items: stretch;
			}

			.isbn-row input {
				flex: 1;
			}

			.btn-isbn-search {
				white-space: nowrap;
				padding: 12px 16px;
				font-family: 'DM Sans', sans-serif;
				font-size: 13px;
				font-weight: 500;
				color: #faf7f2;
				background: var(--accent, #5c4a3a);
				border: none;
				border-radius: 4px;
				cursor: pointer;
				transition: background 0.2s, transform 0.1s;
			}

			.btn-isbn-search:hover {
				background: #3e3028;
			}

			.btn-isbn-search:active {
				transform: scale(0.97);
			}

			.btn-isbn-search:disabled {
				opacity: 0.6;
				cursor: not-allowed;
			}

			/* 조회 결과 메시지 */
			.isbn-message {
				margin-top: 8px;
				font-size: 12px;
				padding: 8px 12px;
				border-radius: 4px;
				display: none;
			}

			.isbn-message.success {
				display: block;
				color: #2d6a4f;
				background: #d8f3dc;
				border: 1px solid #b7e4c7;
			}

			.isbn-message.error {
				display: block;
				color: #9b2226;
				background: #fde8e8;
				border: 1px solid #f5c6cb;
			}

			.isbn-message.loading {
				display: block;
				color: var(--text-sub, #8a7e74);
				background: var(--accent-light, #e8ddd3);
				border: 1px solid var(--border, #e2d9cc);
			}

			/* 미리보기 썸네일 */
			.isbn-preview {
				display: none;
				margin-top: 12px;
				padding: 12px;
				background: var(--bg, #f5f0e8);
				border: 1px solid var(--border, #e2d9cc);
				border-radius: 4px;
				text-align: center;
			}

			.isbn-preview img {
				max-height: 140px;
				border-radius: 3px;
				box-shadow: 0 2px 8px rgba(0,0,0,0.1);
			}

			.isbn-preview .preview-title {
				margin-top: 8px;
				font-size: 13px;
				font-weight: 500;
				color: var(--text, #2c2520);
			}

			/* 자동 채움 애니메이션 */
			@keyframes fieldHighlight {
				0%   { background: #d8f3dc; }
				100% { background: var(--bg, #f5f0e8); }
			}

			.field-filled input {
				animation: fieldHighlight 1s ease-out;
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
							<div class="isbn-row">
								<input type="number" name="isbn" id="isbnInput" placeholder="ISBN 번호 입력" required>
								<button type="button" class="btn-isbn-search" id="btnIsbnSearch" onclick="searchByIsbn()">
									📖 자동 조회
								</button>
							</div>
							<div class="isbn-message" id="isbnMessage"></div>
							<div class="isbn-preview" id="isbnPreview">
								<img id="previewImg" src="" alt="도서 표지">
								<div class="preview-title" id="previewTitle"></div>
							</div>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="field">
					<label>도서명</label>
					<input type="text" name="bookname" id="bookname" value="${admin.bookname}" required>
				</div>
				<div class="field">
					<label>저자</label>
					<input type="text" name="author" id="author" value="${admin.author}" required>
				</div>
				<div class="field">
					<label>출판사</label>
					<input type="text" name="publisher" id="publisher" value="${admin.publisher}" required>
				</div>
				<div class="field">
					<label>이미지 경로 URL</label>
					<input type="text" name="image" id="image" value="${admin.image}">
				</div>
				<div class="field">
					<label>가격</label>
					<input type="text" name="price" id="price" value="${admin.price}">
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

		<!-- ── 카카오 ISBN 자동 조회 JavaScript ── -->
		<script>
			function searchByIsbn() {
				var isbnInput = document.getElementById('isbnInput');
				var isbn = isbnInput.value.trim();
				var msgBox = document.getElementById('isbnMessage');
				var previewBox = document.getElementById('isbnPreview');
				var btn = document.getElementById('btnIsbnSearch');

				// 유효성 검사
				if (!isbn) {
					showMessage(msgBox, 'error', '⚠ ISBN 번호를 입력해주세요.');
					isbnInput.focus();
					return;
				}

				// 로딩 상태
				showMessage(msgBox, 'loading', '🔍 카카오 API에서 도서 정보를 조회 중...');
				btn.disabled = true;
				btn.textContent = '조회 중...';
				previewBox.style.display = 'none';

				// AJAX 요청
				var contextPath = '${pageContext.request.contextPath}';
				var xhr = new XMLHttpRequest();
				xhr.open('GET', contextPath + '/admin/kakaoBookSearch?isbn=' + encodeURIComponent(isbn), true);
				xhr.setRequestHeader('Accept', 'application/json');

				xhr.onreadystatechange = function() {
					if (xhr.readyState === 4) {
						btn.disabled = false;
						btn.textContent = '📖 자동 조회';

						if (xhr.status === 200) {
							try {
								var data = JSON.parse(xhr.responseText);

								if (data.success) {
									// 폼 자동 채우기
									fillField('bookname', data.title);
									fillField('author', data.authors);
									fillField('publisher', data.publisher);
									fillField('image', data.thumbnail);
									fillField('price', data.price);

									showMessage(msgBox, 'success', '✅ 도서 정보를 자동으로 불러왔습니다!');

									// 썸네일 미리보기
									if (data.thumbnail) {
										document.getElementById('previewImg').src = data.thumbnail;
										document.getElementById('previewTitle').textContent = data.title;
										previewBox.style.display = 'block';
									}
								} else {
									showMessage(msgBox, 'error', '❌ ' + (data.message || '검색 결과가 없습니다.'));
								}
							} catch (e) {
								showMessage(msgBox, 'error', '❌ 응답 데이터를 처리할 수 없습니다.');
							}
						} else {
							showMessage(msgBox, 'error', '❌ 서버 요청에 실패했습니다. (HTTP ' + xhr.status + ')');
						}
					}
				};

				xhr.onerror = function() {
					btn.disabled = false;
					btn.textContent = '📖 자동 조회';
					showMessage(msgBox, 'error', '❌ 네트워크 오류가 발생했습니다.');
				};

				xhr.send();
			}

			// 필드 값 채우기 + 하이라이트 애니메이션
			function fillField(fieldId, value) {
				var input = document.getElementById(fieldId);
				if (input && value !== undefined && value !== null) {
					input.value = value;
					// 하이라이트 애니메이션
					var parent = input.closest('.field');
					if (parent) {
						parent.classList.remove('field-filled');
						void parent.offsetWidth; // reflow
						parent.classList.add('field-filled');
					}
				}
			}

			// 메시지 표시
			function showMessage(el, type, text) {
				el.className = 'isbn-message ' + type;
				el.textContent = text;
				el.style.display = 'block';
			}

			// ISBN 입력 필드에서 Enter 키 입력 시 자동 조회
			document.addEventListener('DOMContentLoaded', function() {
				var isbnInput = document.getElementById('isbnInput');
				if (isbnInput) {
					isbnInput.addEventListener('keypress', function(e) {
						if (e.key === 'Enter') {
							e.preventDefault();
							searchByIsbn();
						}
					});
				}
			});
		</script>